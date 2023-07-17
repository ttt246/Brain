import Foundation

struct CommandRes: Codable {
    let message : [String]
    let result : CommandResult
    let status_code : Int
}

struct CommandResult: Codable {
    let program: String
    let content: ContentType
}

/**
 * ContentType can be string, object or array. This has encode/decode functions.
 */
enum ContentType: Codable {
    case string(String)
    case object([String: String])
    case array([String])

    init(from decoder: Decoder) throws {
        let container = try decoder.singleValueContainer()
        if let x = try? container.decode(String.self) {
            self = .string(x)
            return
        }
        if let x = try? container.decode([String: String].self) {
            self = .object(x)
            return
        }
        if let x = try? container.decode([String].self) {
            self = .array(x)
            return
        }
        throw DecodingError.typeMismatch(ContentType.self, DecodingError.Context(codingPath: decoder.codingPath, debugDescription: "Mismatched Types"))
    }

    func encode(to encoder: Encoder) throws {
        var container = encoder.singleValueContainer()
        switch self {
        case .string(let x):
            try container.encode(x)
        case .object(let x):
            try container.encode(x)
        case .array(let x):
            try container.encode(x)
        }
    }
}
