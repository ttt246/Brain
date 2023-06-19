from Brain.src.firebase.firebase import initialize_app
from fastapi import Depends, FastAPI
import uvicorn

from Brain.src.router.browser_router import construct_blueprint_browser_api
from Brain.src.router.train_router import construct_blueprint_train_api
initialize_app()

from Brain.src.router.api import construct_blueprint_api

app = FastAPI()
app.include_router(construct_blueprint_api(), tags=["ai_app"])
app.include_router(
    construct_blueprint_browser_api(), prefix="/browser", tags=["ai_browser"]
)
app.include_router(construct_blueprint_train_api(), prefix="/train", tags=["ai_train"])


if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=7860)
