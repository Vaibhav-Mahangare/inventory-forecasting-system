import numpy as np

def calculate_mae(actual: list, predicted: list) -> float:
    """
    Mean Absolute Error
    Average of absolute differences between actual and predicted
    Lower is better. 0 = perfect.
    """
    actual = np.array(actual)
    predicted = np.array(predicted)
    return round(float(np.mean(np.abs(actual - predicted))), 4)

def calculate_rmse(actual: list, predicted: list) -> float:
    """
    Root Mean Square Error
    Penalizes large errors more than MAE
    Lower is better. 0 = perfect.
    """
    actual = np.array(actual)
    predicted = np.array(predicted)
    return round(float(np.sqrt(np.mean((actual - predicted) ** 2))), 4)

def calculate_mape(actual: list, predicted: list) -> float:
    """
    Mean Absolute Percentage Error
    Accuracy expressed as a percentage
    Lower is better. 0% = perfect. < 10% = excellent.
    """
    actual = np.array(actual, dtype=float)
    predicted = np.array(predicted, dtype=float)

    # Avoid division by zero
    mask = actual != 0
    if not np.any(mask):
        return 0.0

    return round(float(np.mean(np.abs((actual[mask] - predicted[mask])
                                       / actual[mask])) * 100), 4)

def get_accuracy_label(mape: float) -> str:
    """
    Human readable accuracy label based on MAPE
    Used to explain model quality in the response
    """
    if mape < 10:
        return "Excellent"
    elif mape < 20:
        return "Good"
    elif mape < 30:
        return "Fair"
    else:
        return "Poor"