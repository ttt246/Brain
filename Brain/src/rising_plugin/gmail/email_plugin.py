import email
import imaplib
import json
import mimetypes
import os
import re
import smtplib
import time
import base64
from email.header import decode_header
from email.message import EmailMessage
from socket import socket

from bs4 import BeautifulSoup

from Brain.src.common.utils import PROXY_IP, PROXY_PORT

# email variables
EMAIL_SMTP_HOST = "smtp.gmail.com"
EMAIL_SMTP_PORT = 587
EMAIL_IMAP_SERVER = "imap.gmail.com"
EMAIL_SIGNATURE = "This was sent by Rising Brain"


class EmailPlugin:
    def send_email(
        self, sender: str, pwd: str, to: str, subject: str, body: str, to_send: bool
    ) -> str:
        return self.send_email_with_attachment_internal(
            sender=sender,
            pwd=pwd,
            to=to,
            title=subject,
            message=body,
            attachment=None,
            attachment_path=None,
            to_send=to_send,
        )

    def send_email_with_attachment(
        self,
        sender: str,
        pwd: str,
        to: str,
        subject: str,
        body: str,
        filename: str,
        to_send: bool,
    ) -> str:
        attachment_path = filename
        attachment = os.path.basename(filename)
        return self.send_email_with_attachment_internal(
            sender=sender,
            pwd=pwd,
            to=to,
            title=subject,
            message=body,
            attachment_path=attachment_path,
            attachment=attachment,
            to_send=to_send,
        )

    def send_email_with_attachment_internal(
        self,
        sender: str,
        pwd: str,
        to: str,
        title: str,
        message: str,
        attachment_path: str | None,
        attachment: str | None,
        to_send: bool,
    ) -> str:
        """Send an email

        Args:
            sender (str): The email of the sender
            pwd (str): The password of the sender
            to (str): The email of the recipient
            title (str): The title of the email
            message (str): The message content of the email

        Returns:
            str: Any error messages
        """
        email_sender = sender
        email_password = pwd

        msg = EmailMessage()
        msg["Subject"] = title
        msg["From"] = email_sender
        msg["To"] = to

        signature = EMAIL_SIGNATURE
        if signature:
            message += f"\n{signature}"

        msg.set_content(message)

        if attachment_path:
            ctype, encoding = mimetypes.guess_type(attachment_path)
            if ctype is None or encoding is not None:
                # No guess could be made, or the file is encoded (compressed)
                ctype = "application/octet-stream"
            maintype, subtype = ctype.split("/", 1)
            with open(file=attachment_path, mode="rb") as fp:
                msg.add_attachment(
                    fp.read(), maintype=maintype, subtype=subtype, filename=attachment
                )

        if to_send:
            smtp_host = EMAIL_SMTP_HOST
            smtp_port = EMAIL_SMTP_PORT
            # send email
            with smtplib.SMTP(host=smtp_host, port=smtp_port) as smtp:
                smtp.ehlo()
                smtp.starttls()
                smtp.login(user=email_sender, password=email_password)
                smtp.send_message(msg)
                smtp.quit()
            return f"Email was sent to {to}!"
        else:
            conn = self.imap_open(
                imap_folder="[Gmail]/Drafts",
                email_sender=email_sender,
                email_password=email_password,
            )
            conn.append(
                mailbox="[Gmail]/Drafts",
                flags="",
                date_time=imaplib.Time2Internaldate(time.time()),
                message=str(msg).encode("UTF-8"),
            )
            return f"Email went to [Gmail]/Drafts!"

    def read_emails(
        self,
        sender: str,
        pwd: str,
        imap_folder: str = "inbox",
        imap_search_command: str = "UNSEEN",
        limit: int = 5,
        page: int = 1,
    ) -> str:
        """Read emails from an IMAP mailbox.

        This function reads emails from a specified IMAP folder, using a given IMAP search command, limits, and page numbers.
        It returns a list of emails with their details, including the sender, recipient, date, CC, subject, and message body.

        Args:
            sender (str): The email of the sender
            pwd (str): The password of the sender
            imap_folder (str, optional): The name of the IMAP folder to read emails from. Defaults to "inbox".
            imap_search_command (str, optional): The IMAP search command to filter emails. Defaults to "UNSEEN".
            limit (int, optional): Number of email's the function should return. Defaults to 5 emails.
            page (int, optional): The index of the page result the function should resturn. Defaults to 0, the first page.

        Returns:
            str: A list of dictionaries containing email details if there are any matching emails.
        """
        email_sender = sender
        imap_folder = self.adjust_imap_folder_for_gmail(
            imap_folder=imap_folder, email_sender=email_sender
        )
        imap_folder = self.enclose_with_quotes(imap_folder)
        imap_search_ar = self.split_imap_search_command(imap_search_command)
        email_password = pwd

        mark_as_seen = "False"
        if isinstance(mark_as_seen, str):
            mark_as_seen = json.loads(mark_as_seen.lower())

        conn = self.imap_open(
            imap_folder=imap_folder,
            email_sender=email_sender,
            email_password=email_password,
        )

        imap_keyword = imap_search_ar[0]
        if len(imap_search_ar) == 1:
            _, search_data = conn.search(None, imap_keyword)
        else:
            argument = self.enclose_with_quotes(imap_search_ar[1])
            _, search_data = conn.search(None, imap_keyword, argument)

        messages = []
        for num in search_data[0].split():
            if mark_as_seen:
                message_parts = "(RFC822)"
            else:
                message_parts = "(BODY.PEEK[])"
            _, msg_data = conn.fetch(message_set=num, message_parts=message_parts)
            for response_part in msg_data:
                if isinstance(response_part, tuple):
                    msg = email.message_from_bytes(response_part[1])

                    # If the subject has unknown encoding, return blank
                    if msg["Subject"] is not None:
                        subject, encoding = decode_header(msg["Subject"])[0]
                    else:
                        subject = ""
                        encoding = ""

                    if isinstance(subject, bytes):
                        try:
                            # If the subject has unknown encoding, return blank
                            if encoding is not None:
                                subject = subject.decode(encoding)
                            else:
                                subject = ""
                        except [LookupError] as e:
                            pass

                    body = self.get_email_body(msg)
                    # Clean email body
                    body = self.clean_email_body(body)

                    from_address = msg["From"]
                    to_address = msg["To"]
                    date = msg["Date"]
                    cc = msg["CC"] if msg["CC"] else ""

                    messages.append(
                        {
                            "from": from_address,
                            "to": to_address,
                            "date": date,
                            "cc": cc,
                            "subject": subject,
                            "body": body,
                        }
                    )

        conn.logout()
        if not messages:
            messages.append(
                {
                    "from": "",
                    "to": "",
                    "date": "",
                    "cc": "",
                    "subject": "",
                    "body": "There are no Emails",
                }
            )
            return json.dumps(messages)

        # Confirm that integer parameters are the right type
        limit = int(limit)
        page = int(page)

        # Validate parameter values
        if limit < 1:
            raise ValueError("Error: The message limit should be 1 or greater")

        page_count = len(messages) // limit + (len(messages) % limit > 0)

        if page < 1 or page > page_count:
            raise ValueError(
                "Error: The page value references a page that is not part of the results"
            )

        # Calculate paginated indexes
        start_index = len(messages) - (page * limit + 1)
        end_index = start_index + limit
        start_index = max(start_index, 0)

        # Return paginated indexes
        if start_index == end_index:
            return json.dumps([messages[start_index]])
        else:
            return json.dumps(messages[start_index:end_index])

    def adjust_imap_folder_for_gmail(self, imap_folder: str, email_sender: str) -> str:
        if "@gmail" in email_sender.lower() or "@googlemail" in email_sender.lower():
            if "sent" in imap_folder.lower():
                return '"[Gmail]/Sent Mail"'
            if "draft" in imap_folder.lower():
                return "[Gmail]/Drafts"
        return imap_folder

    def imap_open(
        self, imap_folder: str, email_sender: str, email_password: str
    ) -> imaplib.IMAP4_SSL:
        # Create a new socket object for later connections as a proxy

        # IMAP Server Connect
        imap_server = EMAIL_IMAP_SERVER
        conn = imaplib.IMAP4_SSL(imap_server)
        conn.login(user=email_sender, password=email_password)
        conn.select(imap_folder)
        return conn

    def get_email_body(self, msg: email.message.Message) -> str:
        if msg.is_multipart():
            for part in msg.walk():
                content_type = part.get_content_type()
                content_disposition = str(part.get("Content-Disposition"))
                if (
                    content_type == "text/plain"
                    and "attachment" not in content_disposition
                ):
                    # If the email body has unknown encoding, return null
                    try:
                        return part.get_payload(decode=True).decode()
                    except UnicodeDecodeError as e:
                        pass
        else:
            try:
                # If the email body has unknown encoding, return null
                return msg.get_payload(decode=True).decode()
            except UnicodeDecodeError as e:
                pass

    def enclose_with_quotes(self, s):
        # Check if string contains whitespace
        has_whitespace = bool(re.search(r"\s", s))

        # Check if string is already enclosed by quotes
        is_enclosed = s.startswith(("'", '"')) and s.endswith(("'", '"'))

        # If string has whitespace and is not enclosed by quotes, enclose it with double quotes
        if has_whitespace and not is_enclosed:
            return f'"{s}"'
        else:
            return s

    def split_imap_search_command(self, input_string):
        input_string = input_string.strip()
        parts = input_string.split(maxsplit=1)
        parts = [part.strip() for part in parts]

        return parts

    def clean_email_body(self, email_body):
        """Remove formating and URL's from an email's body

        Args:
            email_body (str, optional): The email's body

        Returns:
            str: The email's body without any formating or URL's
        """

        # If body is None, return an empty string
        if email_body is None:
            email_body = ""

        # Remove any HTML tags
        email_body = BeautifulSoup(email_body, "html.parser")
        email_body = email_body.get_text()

        # Remove return characters
        email_body = "".join(email_body.splitlines())

        # Remove extra spaces
        email_body = " ".join(email_body.split())

        # Remove unicode characters
        email_body = email_body.encode("ascii", "ignore")
        email_body = email_body.decode("utf-8", "ignore")

        # Remove any remaining URL's
        email_body = re.sub(r"http\S+", "", email_body)

        return email_body

    def write_attachment(self, filename: str, file_content: str) -> (str, str):
        # create folder for temporarily saving attached file
        milliseconds = int(time.time() * 1000)
        file_path = f"Brain/assets/{milliseconds}/{filename}"
        file_directory = f"Brain/assets/{milliseconds}"
        os.mkdir(file_directory)

        # file write
        file_content = base64.b64decode(file_content).decode("utf-8")
        file = open(file_path, "w")
        file.write(file_content)
        file.close()
        return file_path, file_directory
