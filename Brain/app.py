from src.firebase.firebase import initialize_app
from fastapi import Depends, FastAPI
import uvicorn

from src.router.browser_router import construct_blueprint_browser_api

initialize_app()

from src.router.api import construct_blueprint_api

app = FastAPI()
app.include_router(construct_blueprint_api(), tags=["ai_app"])
app.include_router(
    construct_blueprint_browser_api(), prefix="/browser", tags=["ai_browser"]
)


if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=7860)
