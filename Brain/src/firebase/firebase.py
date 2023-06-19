import firebase_admin
from src.common.utils import get_firebase_cred, FIREBASE_STORAGE_BUCKET


def initialize_app():
    firebase_admin.initialize_app(
        get_firebase_cred(), {"storageBucket": FIREBASE_STORAGE_BUCKET}
    )
