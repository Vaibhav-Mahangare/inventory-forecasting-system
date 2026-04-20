from pydantic import BaseModel
from typing import List, Optional

class SaleDataPoint(BaseModel):
    sale_date: str
    quantity_sold: int

class ForecastRequest(BaseModel):
    product_id: int
    sales_data: List[SaleDataPoint]
    force_retrain: Optional[bool] = False  # force retrain even if model exists

class ForecastResult(BaseModel):
    product_id: int
    predicted_7_days: int
    predicted_15_days: int
    predicted_30_days: int
    mae: float             # Mean Absolute Error
    rmse: float            # Root Mean Square Error
    mape: float            # Mean Absolute Percentage Error (accuracy %)
    accuracy_label: str    # "Excellent" / "Good" / "Fair" / "Poor"
    model_status: str      # "trained_fresh" / "loaded_existing"

class BatchForecastRequest(BaseModel):
    products: List[ForecastRequest]

class BatchForecastResult(BaseModel):
    results: List[ForecastResult]
    total_products: int
    successful: int
    failed: int