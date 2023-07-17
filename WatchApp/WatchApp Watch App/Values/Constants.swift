import Foundation

struct Constants {
    static let DEFAULT_UUID = "ac2293beb9f3b1ca"
    static let BASE_URL = "https://ttt246-brain.hf.space/"
    
    static func getConfs(uuid: String) -> [String: Any] {
        return [
            "openai_key": "your openai key",
            "pinecone_key": "your pinecone key",
            "pinecone_env": "your pinecone env",
            "firebase_key": "your firebase key",
            "token":"your token",
            "uuid": uuid,
            "settings": [
                "temperature": 0.6
            ]
        ]
    }
}

