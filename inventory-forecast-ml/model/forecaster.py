import os
import joblib
import pandas as pd
import numpy as np
from prophet import Prophet
from utils.metrics import calculate_mae, calculate_rmse, calculate_mape, get_accuracy_label

# Directory where trained models are saved
MODEL_DIR = "saved_models"
os.makedirs(MODEL_DIR, exist_ok=True)

def get_model_path(product_id: int) -> str:
    return os.path.join(MODEL_DIR, f"prophet_product_{product_id}.pkl")

def save_model(model: Prophet, product_id: int):
    """Save trained Prophet model to disk"""
    joblib.dump(model, get_model_path(product_id))

def load_model(product_id: int):
    """Load existing trained model from disk if available"""
    path = get_model_path(product_id)
    if os.path.exists(path):
        return joblib.load(path)
    return None

def fallback_prediction(df: pd.DataFrame) -> dict:
    """
    Fallback strategy when data is insufficient for Prophet
    Uses simple average-based prediction
    """
    avg_daily = float(df["y"].mean()) if len(df) > 0 else 0
    return {
        "predicted_7_days": max(0, int(avg_daily * 7)),
        "predicted_15_days": max(0, int(avg_daily * 15)),
        "predicted_30_days": max(0, int(avg_daily * 30)),
        "mae": 0.0,
        "rmse": 0.0,
        "mape": 0.0,
        "accuracy_label": "Fair",
        "model_status": "fallback_average"
    }

def predict_demand(product_id: int, sales_data: list,
                   force_retrain: bool = False) -> dict:

    # Step 1 — Build DataFrame
    df = pd.DataFrame([{
        "ds": item["sale_date"],
        "y": item["quantity_sold"]
    } for item in sales_data])

    df["ds"] = pd.to_datetime(df["ds"])
    df["y"] = df["y"].astype(float).clip(lower=0)  # no negative sales

    # Step 2 — Use fallback if not enough data
    if len(df) < 7:
        return fallback_prediction(df)

    # Step 3 — Load existing model or train new one
    model_status = "loaded_existing"
    model = None

    if not force_retrain:
        model = load_model(product_id)

    if model is None:
        # Train fresh Prophet model
        model = Prophet(
            daily_seasonality=True,
            weekly_seasonality=True,
            yearly_seasonality=False,
            interval_width=0.95,
            changepoint_prior_scale=0.05  # controls trend flexibility
        )

        # Add custom seasonality — month end buying pattern
        model.add_seasonality(
            name="monthly",
            period=30.5,
            fourier_order=5
        )

        model.fit(df)

        # Save trained model for future use
        save_model(model, product_id)
        model_status = "trained_fresh"

    # Step 4 — Calculate accuracy metrics using last 20% of data as test set
    split_index = max(1, int(len(df) * 0.8))
    train_df = df.iloc[:split_index]
    test_df = df.iloc[split_index:]

    mae, rmse, mape = 0.0, 0.0, 0.0

    if len(test_df) >= 2:
        # Train a temporary model on train set only for evaluation
        eval_model = Prophet(
            daily_seasonality=True,
            weekly_seasonality=True,
            yearly_seasonality=False
        )
        eval_model.fit(train_df)

        # Predict for test period
        future_eval = eval_model.make_future_dataframe(
            periods=len(test_df), freq="D"
        )
        eval_forecast = eval_model.predict(future_eval)

        # Compare predictions vs actual test values
        predicted_values = eval_forecast.tail(len(test_df))["yhat"] \
                                        .clip(lower=0).tolist()
        actual_values = test_df["y"].tolist()

        mae = calculate_mae(actual_values, predicted_values)
        rmse = calculate_rmse(actual_values, predicted_values)
        mape = calculate_mape(actual_values, predicted_values)

    # Step 5 — Generate future predictions using full model
    future = model.make_future_dataframe(periods=30, freq="D")
    forecast = model.predict(future)

    # Only keep future dates
    future_forecast = forecast[forecast["ds"] > df["ds"].max()].copy()
    future_forecast["yhat"] = future_forecast["yhat"].clip(lower=0)

    predicted_7 = int(future_forecast.head(7)["yhat"].sum())
    predicted_15 = int(future_forecast.head(15)["yhat"].sum())
    predicted_30 = int(future_forecast.head(30)["yhat"].sum())

    return {
        "predicted_7_days": predicted_7,
        "predicted_15_days": predicted_15,
        "predicted_30_days": predicted_30,
        "mae": mae,
        "rmse": rmse,
        "mape": mape,
        "accuracy_label": get_accuracy_label(mape),
        "model_status": model_status
    }