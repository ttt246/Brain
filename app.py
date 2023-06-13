from src.firebase.firebase import initialize_app
from fastapi import Depends, FastAPI
import uvicorn

initialize_app()

from src.router.api import construct_blueprint_api

app = FastAPI()
app.include_router(construct_blueprint_api(), tags=["ai"])


if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=7860)
