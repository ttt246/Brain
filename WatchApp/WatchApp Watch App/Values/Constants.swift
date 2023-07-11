import Foundation

struct Constants {
    static let DEFAULT_UUID = "ac2293beb9f3b1ca"
    
    static let BASE_URL = "https://ttt246-brain.hf.space/"
    static let URL_SEND_NOTIFICATION = BASE_URL + "sendNotification"
    static let URL_GET_CONTACTS_BY_IDS = BASE_URL + "contacts/get_by_ids"
    
    static let RESPONSE_TYPE_CONTACT = "contact"
    static let RESPONSE_TYPE_IMAGE = "image"
    
    static func getConfs(uuid: String) -> [String: Any] {
        return [
            "openai_key": "",
            "pinecone_key": "",
            "pinecone_env": "",
            "firebase_key": "",
            "token":"cI3EvimJQv-G5imdWrBprf:APA91bEZ5u6uq9Yq4a6NglN0L9pVM7p-rlxKB_FikbfKlzHnZT5GeAjxF0deuPT2GurS8bK6JTE2XPZLQqbsrtjxeRGhGOH5INoQ7MrRlr4TR3xFswKxJSkfi1aBUWDaLGALeirZ7GuZ",
            "uuid": uuid,
            "settings": [
                "temperature": 0.6
            ]
        ]
    }
}

