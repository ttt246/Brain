import pytest
from fastapi import FastAPI
from fastapi.testclient import TestClient


@pytest.fixture(scope="module")
def test_client():
    app = FastAPI()
    client = TestClient(app)
    # Create a test client using the FastApi application configured for testing
    with client as testing_client:
        # Establish an application context
        with client.app:
            yield testing_client  # this is where the testing happens!
