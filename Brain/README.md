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

# RisingBrain
WIP: This codebase is under active development and recently opened to the public. 

All complex software including operating systems will need to be rewritten from the ground up to take advantage of machine learning. In our OS, a AI will manage all apps via plugins, which can be prompted by the user. Our plugins can run as an openai plugin, or in our backend.

## 1. Description

#### 1.1 Achievement
<p align='center'>
  <img align='center' src='assets/img/desc.png' width='100%' />
</p>

- 📱 Support for mobile devices to manage all apps via plugin as its launcher.
- 🌍 Support all web browsers to manipulate it automatically as its extension.
- 🔗 Multiple API support (Web API for Free and Plus users, GPT-3.5, GPT-4, etc.).
- 🔍 Integration to all mainstream search engines, and custom queries to support additional sites.
- 
#### 1.2 Feature

- Chat with Rising AI as an assistant.
- Search something from browser(which installed in your device) with user's input without any additional behaviour automatically. 
- Search an image what you want for with its similar image or its description as user's prompt.
- Search a contact automatically and guide a user to have a call or Sms when they want.
- Manage contacts automatically and let a user going to any contact with its name or number. 
- Manipulate all browsers with user's input(tab, page, search, scroll, or etc) and it will follow you to go on the real website or blog what you are thinking of.

## 2. Installing / Getting started
#### 2.1 how to run on local
It's a standard fastapi application so no configuration is needed beyond the included Procfile
#### 2.2 Create firebase project
Create firebase project and take its credentials named .json from Google Cloude IAM. 
#### 2.3 Get Heroku Api key
Deploy it to Heroku in CI/CD automatically whenever there are some changes in main or develop branch.
#### 2.4 Set Github Secrets With its Access Keys
All credentials including openai, replicate and pinecone are shared with Github Secrets to be referenced by Unit Tests of CI/CD on its building

## Contributing
Please refer to each project's style and contribution guidelines for submitting patches and additions. In general, we follow the "fork-and-pull" Git workflow.

 1. **Fork** the repo on GitHub
 2. **Clone** the project to your own machine
 3. **Commit** changes to your own branch
 4. **Push** your work back up to your fork
 5. Submit a **Pull request** so that we can review your changes

NOTE: Be sure to merge the latest from "upstream" before making a pull request!
