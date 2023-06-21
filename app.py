from Brain.src.firebase.firebase import initialize_app
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
import uvicorn

from Brain.src.router.browser_router import construct_blueprint_browser_api
from Brain.src.router.train_router import construct_blueprint_train_api

initialize_app()

from Brain.src.router.api import construct_blueprint_api

app = FastAPI()
# Set up CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Allow all origins (domains)
    allow_credentials=True,
    allow_methods=["*"],  # Allow all methods (GET, POST, PUT, DELETE, OPTIONS, etc.)
    allow_headers=["*"],  # Allow all headers
)

# Set up routers
app.include_router(construct_blueprint_api(), tags=["ai_app"])
app.include_router(
    construct_blueprint_browser_api(), prefix="/browser", tags=["ai_browser"]
)

app.include_router(
    construct_blueprint_train_api(), prefix="/train", tags=["ai_train"]
)


if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=7860)
