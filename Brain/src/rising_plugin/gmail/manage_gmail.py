from langchain import OpenAI
from langchain.agents import initialize_agent, AgentType
from langchain.tools.gmail.utils import build_resource_service, get_gmail_credentials
from langchain.agents.agent_toolkits import GmailToolkit
from langchain.agents.agent import AgentExecutor


def get_agent() -> AgentExecutor:
    # Can review scopes here https://developers.google.com/gmail/api/auth/scopes
    # For instance, readonly scope is 'https://www.googleapis.com/auth/gmail.readonly'
    credentials = get_gmail_credentials(
        token_file="token.json",
        scopes=["https://mail.google.com/"],
        client_secrets_file="credentials.json",
    )
    api_resource = build_resource_service(credentials=credentials)
    toolkit = GmailToolkit(api_resource=api_resource)

    llm = OpenAI(temperature=0)
    agent = initialize_agent(
        tools=toolkit.get_tools(),
        llm=llm,
        agent=AgentType.STRUCTURED_CHAT_ZERO_SHOT_REACT_DESCRIPTION,
    )
    return agent


def read_emails() -> str:
    agent = get_agent()
    result = agent.run(
        "Could you search in my inbox for the latest email?"
    )
    return result


def write_email(query: str) -> str:
    agent = get_agent()
    result = agent.run(query)
    return result


def send_email(query: str) -> str:
    agent = get_agent()
    result = agent.run(query)
    return result
