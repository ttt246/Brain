"""gradio implementaion"""
# import gradio as gr
# import requests
# import json
# from gradio.components import Textbox, Number
#
# DEFAULT_UUID = "c40a09075d11940f"
#
#
# def gradio_send_notification(uuid: str, query: str):
#     url = "http://localhost:7860/sendNotification"
#     data = {
#         "confs": {
#             "openai_key": "",
#             "pinecone_key": "",
#             "pinecone_env": "",
#             "firebase_key": "",
#             "settings": {"temperature": 0.6},
#             "token": "eSyP3i7ITZuq8hWn2qutTl:APA91bH1FtWkaTSJwuX4WKWAl3Q-ZFyrOw4UtMP4IfwuvNrHOThH7EvEGIhtguilLRyQNlLiXatEN0xntHAc8bbKobSGjge3wxIHlspbIWY_855CzONqaVdl3y3zOmgKZNnuhYi4gwbh",
#             "uuid": uuid,
#         },
#         "message": query,
#     }
#
#     response = requests.post(
#         url, data=json.dumps(data), headers={"Content-Type": "application/json"}
#     )
#     response = response.json()
#     return json.dumps(response["result"]), response["status_code"]
#
#
# debug_send_notification = gr.Interface(
#     fn=gradio_send_notification,
#     inputs=[
#         Textbox(
#             label="UUID",
#             placeholder="Please input your UUID.",
#             value="c40a09075d11940f",
#         ),
#         Textbox(label="query", placeholder="Please input your prompt."),
#     ],
#     outputs=[
#         Textbox(label="Result"),
#         Number(label="Status Code"),
#     ],
#     allow_flagging="never",
# )
