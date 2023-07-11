---
title: RisingBrain
emoji: 🌍
colorFrom: yellow
colorTo: indigo
app_file: app.py
sdk: gradio
sdk_version: 2.9.1
python_version: 3.10.4
pinned: false
license: other
---

# 🧠 RisingBrain - Powering Your AI Enhanced OS 💡

Welcome to the heartbeat of **RisingBrain**, our main backend component. ⚽ Kickstart your **RisingBrain** project right from here.
<p align="center">
  <img align="center" src="assets/img/brain-diagram.png" width="100%">
</p>

## Getting Started 🏁

The first step involves setting up the necessary environment. You'll need two files:

1. **Firebase Credential JSON file**: Place your firebase_credential.json in the following directory:

<p align="center">
  <img src="assets/img/firebase_credential.png" width="88%"/>
  <p align="center">[Project Directory]/Brain/firebase_credential.json</p>
</p>

2. **Environment Variable file**: The .env file is essential for any application configuration. Make sure to have it at the location:
<p align="center">
  <img src="assets/img/dotenv.png" width="88%"/>
  <p align="center">[Project Directory]/.env</p>
</p>

## Running FastAPI Application 🚀
Our backend runs on a <a href="https://fastapi.tiangolo.com/">FastAPI</a> application. Here's a quick guide to get it up and running:

### Step 1: Create the virtual environment.
This aims to isolate your Python/system install from the application's project packages.

- On Windows:

  ``` bach 
  python -m venv [virtualenv name]
  ```

  ``` bash
    venv\Scripts\activate.bat\
  ```

- On Linux:

  ``` bash
  python3 -m venv [virtualenv name]
  ```

  ``` bash
  source [virtualenv name]/bin/activate
  ```


### Step 2: Install all required packages using the provided requirements.txt file.

  ``` bash
  pip install -r requirements.txt
  ```

### Step 3: Start the FastAPI application with hot reloads enabled using Uvicorn.
  ``` bash
  uvicorn app:app --reload
  ```

Bravo!👏 You should now see your **Brain Backend** is alive and ready for action, empowering your AI interactions in **RisingBrain**.

Happy coding! 🎉

## Contributing 💪
We appreciate your interest in enhancing our work! Please respect the style and contribution guidelines of every project when submitting patches and additions. Our general Git workflow of choice is "fork-and-pull".

 1. **Fork** the repository on GitHub
 2. **Clone** your fork to your machine
 3. **Commit** the changes to your personal branch
 4. **Push** these updates back to your fork
 5. Don't forget to submit a **Pull Request** for us to study your contributions.

NOTE: Sync with "upstream" to have the latest updates before you make a pull request!
