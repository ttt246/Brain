import Foundation

struct ContactRes : Codable {
    let message : [String]
    let result : [Contact]
    let status_code : Int
}

class Contact : Codable {
    var contactId: String
    var displayName: String
    var phoneNumbers: [String]

    init(contactId: String, displayName: String, phoneNumbers: [String]) {
        self.contactId = contactId
        self.displayName = displayName
        self.phoneNumbers = phoneNumbers
    }
}
