from src.firebase.firebase import initialize_app
from fastapi import Depends, FastAPI
import uvicorn

from src.router.browser_router import construct_blueprint_browser_api
from src.router.train_router import construct_blueprint_train_api
from src.router.document_router import construct_blueprint_document_api

initialize_app()

from src.router.api import construct_blueprint_api

app = FastAPI()
app.include_router(construct_blueprint_api(), tags=["ai_app"])
app.include_router(
    construct_blueprint_browser_api(), prefix="/browser", tags=["ai_browser"]
)
app.include_router(construct_blueprint_train_api(), prefix="/train", tags=["ai_train"])
app.include_router(
    construct_blueprint_document_api(), prefix="/document", tags=["ai_document"]
)


if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=7860)
