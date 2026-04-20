from fastapi import FastAPI, HTTPException
from schema.schemas import (
    ForecastRequest, ForecastResult,
    BatchForecastRequest, BatchForecastResult
)
from model.forecaster import predict_demand
import os

app = FastAPI(
    title="Inventory Demand Forecasting API",
    description="Predicts product demand using Facebook Prophet with accuracy metrics",
    version="2.0.0"
)

@app.get("/health")
def health_check():
    return {"status": "running", "version": "2.0.0"}

# -------------------------------------------------------
# Single Product Forecast
# -------------------------------------------------------
@app.post("/predict", response_model=ForecastResult)
def predict(request: ForecastRequest):
    if not request.sales_data:
        raise HTTPException(
            status_code=400,
            detail="Sales data cannot be empty"
        )

    sales_data = [
        {
            "sale_date": item.sale_date,
            "quantity_sold": item.quantity_sold
        }
        for item in request.sales_data
    ]

    result = predict_demand(
        product_id=request.product_id,
        sales_data=sales_data,
        force_retrain=request.force_retrain
    )

    return ForecastResult(
        product_id=request.product_id,
        **result
    )

# -------------------------------------------------------
# Batch Forecast — All products at once
# -------------------------------------------------------
@app.post("/predict/batch", response_model=BatchForecastResult)
def predict_batch(request: BatchForecastRequest):
    results = []
    failed = 0

    for product_request in request.products:
        try:
            sales_data = [
                {
                    "sale_date": item.sale_date,
                    "quantity_sold": item.quantity_sold
                }
                for item in product_request.sales_data
            ]

            result = predict_demand(
                product_id=product_request.product_id,
                sales_data=sales_data,
                force_retrain=product_request.force_retrain
            )

            results.append(ForecastResult(
                product_id=product_request.product_id,
                **result
            ))

        except Exception as e:
            # Don't fail entire batch if one product fails
            failed += 1
            print(f"Failed to predict for product "
                  f"{product_request.product_id}: {str(e)}")

    return BatchForecastResult(
        results=results,
        total_products=len(request.products),
        successful=len(results),
        failed=failed
    )

# -------------------------------------------------------
# Force retrain a specific product's model
# -------------------------------------------------------
@app.delete("/model/{product_id}")
def delete_model(product_id: int):
    path = f"saved_models/prophet_product_{product_id}.pkl"
    if os.path.exists(path):
        os.remove(path)
        return {"message": f"Model for product {product_id} deleted. "
                            f"Will retrain on next prediction."}
    return {"message": f"No saved model found for product {product_id}"}