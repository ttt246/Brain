import SwiftUI
import KeychainSwift

struct ContactResponse : Codable {
    let message : [String]
    let result : [Contact]
    let status_code : Int
}

struct ChatView: View {
    @State private var messages: [ChatMessage] = []
    @State private var newMessage: String = ""
    @State private var uuid: String = Constants.DEFAULT_UUID
    @State private var isStarted = false
    
    var body: some View {
        VStack{
            ScrollView {
                    LazyVStack(spacing: 0) {
                        ForEach(messages, id: \.self) { message in
                            ChatMessageRow(chatItem: message)
                        }
                    }
                }
                .frame(maxHeight: .infinity).padding(.bottom, 10)
            HStack() {
                Button(action: {}) {
                    NavigationLink(destination: SettingView()){
                        Image(systemName: "gearshape.fill")
                            .font(.system(size: 20))
                            .frame(maxWidth: .infinity)
                    }
                }.frame(width: 45, height: 45).foregroundColor(.green)
                    .background(Color.green.opacity(0.3))
                    .cornerRadius(5).buttonStyle(PlainButtonStyle())
                
                TextField("", text: $newMessage, onEditingChanged: { (isChanged) in
                    if isStarted && !isChanged {
                        isStarted = false
                        print(newMessage)
                    }
                }).frame(height: 45)
                    .overlay(
                        RoundedRectangle(cornerRadius: 6)
                            .stroke(Color.green, lineWidth: 1)
                    )
                Button(action: {
                    
                    sendNotification()
                }) {
                    Image(systemName: "paperplane.fill")
                        .font(.system(size: 20))
                        .frame(maxWidth: .infinity)
                }.frame(width: 45, height: 45).foregroundColor(.green)
                    .background(Color.green.opacity(0.3))
                    .cornerRadius(5).buttonStyle(PlainButtonStyle())
            }
        }.edgesIgnoringSafeArea(.bottom)
    }

    func sendNotification() {
        if newMessage == "" {
            return
        }
        // Try to get the UUID from the keychain
        let keychain = KeychainSwift()

        if keychain.get("uuid") == "" {
            let uuid = "956a11be45cba4a4"
            keychain.set(uuid, forKey: "uuid")
        }
        
        // Add sent message to chat list
        let message = ChatMessage(
            type: TypeChat.SEND,
            message: newMessage,
            widgetType: TypeWidget.EMPTY,
            widgetDesc: ""
        )
        messages.append(message)

        if let url = URL(string: Constants.URL_SEND_NOTIFICATION) {
            var request = URLRequest(url: url)
            request.httpMethod = "POST"
            request.addValue("application/json", forHTTPHeaderField: "Content-Type")

            let postData: [String: Any] = ["confs": Constants.getConfs(uuid: uuid), "message": newMessage]

            do {
                let jsonPostData = try JSONSerialization.data(withJSONObject: postData, options: .prettyPrinted)
                URLSession.shared.uploadTask(with: request, from: jsonPostData) { data, response, error in
                    guard error == nil, let data = data else {
                        print("Error: \(error?.localizedDescription ?? "Unknown error")")
                        return
                    }

                    do {
                        if let jsonResult = try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any] {
                            if let result = jsonResult["result"] as? [String: Any], let program = result["program"] as? String {

                                /**
                                 * Analyzed user's query are classified in this switch code.
                                 * Currently available command types are contact, image and message.
                                 */
                                switch program {
                                case Constants.RESPONSE_TYPE_CONTACT:
                                    if let strList = result["content"] as? String {
                                        DispatchQueue.main.async {
                                            let cleanedStr = strList.filter("0123456789,".contains)
                                            let strArray = cleanedStr.components(separatedBy: ",")
                                            searchContacts(contactIds: strArray)
                                        }
                                    }
                                    
                                case Constants.RESPONSE_TYPE_IMAGE:
                                    if let content = result["content"] as? [String: Any] {
                                        DispatchQueue.main.async {
                                            if let imageName = content["image_name"] as? String {
                                                let message = ChatMessage(
                                                    type: TypeChat.WIDGET,
                                                    message: imageName,
                                                    widgetType: TypeWidget.IMAGE,
                                                    widgetDesc: ""
                                                )
                                                messages.append(message)
                                            }
                                        }
                                    }
                                    
                                default:
                                    if let content = result["content"] as? String {
                                        DispatchQueue.main.async {
                                            let message = ChatMessage(
                                                type: TypeChat.RECEIVE,
                                                message: content,
                                                widgetType: TypeWidget.EMPTY,
                                                widgetDesc: ""
                                            )
                                            messages.append(message)
                                        }
                                    }
                                }
                            }
                        }
                    } catch {
                        print("JSONSerialization error: \(error.localizedDescription)")
                    }

                }.resume()
            } catch {
                print("Error: \(error.localizedDescription)")
            }
        }
        newMessage = ""
    }
    
    func searchContacts(contactIds: Array<String>) {
        
        let keychain = KeychainSwift()

        // Try to get the UUID from the keychain
        if keychain.get("uuid") == "" {
            let uuid = "ac2293beb9f3b1ca"
            keychain.set(uuid, forKey: "uuid")
        }
        
        // Send Http request with POST method
        if let url = URL(string: Constants.URL_GET_CONTACTS_BY_IDS) {
            var request = URLRequest(url: url)
            request.httpMethod = "POST"
            request.addValue("application/json", forHTTPHeaderField: "Content-Type")
            
            let postData: [String: Any] = ["confs": Constants.getConfs(uuid: uuid), "contactIds": contactIds]
            
            do {
                let postJsonData = try JSONSerialization.data(withJSONObject: postData, options: .prettyPrinted)
                URLSession.shared.uploadTask(with: request, from: postJsonData) { data, response, error in
                    guard error == nil, let data = data else {
                        print("Error: \(error?.localizedDescription ?? "Unknown error")")
                        return
                    }
                    
                    do {
                        let decoder = JSONDecoder()
                        let contactResponse = try decoder.decode(ContactResponse.self, from: data)
                        let contacts = contactResponse.result
                        for contact in contacts {
                            do {
                                let jsonContact = try JSONEncoder().encode(contact)
                                if let strContact = String(data: jsonContact, encoding: .utf8) {
                                    let message = ChatMessage(
                                        type: TypeChat.WIDGET,
                                        message: "",
                                        widgetType: TypeWidget.CONTACT,
                                        widgetDesc: strContact
                                    )
                                    messages.append(message)
                                }
                            } catch {
                                print("Error converting to JSON string: \(error)")
                            }
                        }
                    } catch {
                        print("JSONDecoding error: \(error.localizedDescription)")
                    }
                }.resume()
            } catch {
                print("Error: \(error.localizedDescription)")
            }
        }
        newMessage = ""
    }
}



struct ChatView_Previews: PreviewProvider {
    static var previews: some View {
        ChatView()
    }
}
