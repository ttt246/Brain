import Foundation

class ChatViewModel: ObservableObject {
    private let RES_MESSAGE = "message"
    private let RES_CONTACT = "contact"
    private let RES_IMAGE = "image"
    
    @Published var messages = [ChatMessage]()
    @Published var newMessage: String = ""
    
    private let networkService = NetworkService()
    
    /**
     * This function is used to add error messages to chatting message list.
     */
    private func addErrorMessage(errorMessage: String) {
        let message = ChatMessage(type: TypeChat.RECEIVE, message: errorMessage, widgetType: TypeWidget.EMPTY, widgetDesc: "")
        self.messages.append(message)
    }
    
    /**
     * This function is used to send message to Brain and get response with CommandRes schema.
     * The reponse is identify by the value of program property in CommandRes and proper tasks are done.
     *
     * @param conf This is object that contains essential key values for switching backend
     */
    func sendNotification(conf: [String: Any]) {
        if newMessage == "" {
            return
        }
        let message = ChatMessage(type: TypeChat.SEND, message: newMessage, widgetType: TypeWidget.EMPTY, widgetDesc: "")
        self.messages.append(message)
        
        self.networkService.sendNotification(string: newMessage, object: conf) { result in
            switch result {
            case .success(let commandRes):
                DispatchQueue.main.async {
                    switch commandRes.result.program {
                        /**
                         * If users ask something general, this response might be received.
                         * If value of program is message, the message is added to chatting list.
                         * This means that answer for user's prompt that is analyzed by Brain, is general text answer.
                         */
                    case self.RES_MESSAGE:
                        if case .string(let contentString) = commandRes.result.content {
                            self.messages.append(
                                ChatMessage(type: TypeChat.RECEIVE,
                                            message: contentString,
                                            widgetType: TypeWidget.EMPTY,
                                            widgetDesc: "")
                            )
                        }
                        /**
                         * If users ask something related to search contacts, this reponse can be received.
                         * In this case, getContacts function is called.
                         */
                    case self.RES_CONTACT:
                        if case .string(let contentString) = commandRes.result.content {
                            let cleanedStr = contentString.filter("0123456789,".contains)
                            let contactIds = cleanedStr.components(separatedBy: ",")
                            print("contacts")
                            self.getContacts(array: contactIds, object: conf)
                        }
                        /**
                         * If users ask something related to search images, this reponse can be received.
                         * In this case, ImageRow component is added to chatting list.
                         */
                    case self.RES_IMAGE:
                        self.messages.append(
                            ChatMessage(
                                type: TypeChat.WIDGET,
                                message: "", widgetType:
                                    TypeWidget.IMAGE,
                                widgetDesc: "")
                        )
                        /**
                         * In other case, the below error message is added.
                         */
                    default:
                        self.addErrorMessage(errorMessage: "unknown error")
                    }
                }
                /**
                 * Server error is occured in backend or data isn't in the correct format, it can be called.
                 */
            case .failure(let error):
                /**
                 * To ensure convenience in development, this log function is added.
                 */
                print(error.localizedDescription)
                self.addErrorMessage(errorMessage: "network error")
            }
        }
        
        self.newMessage = ""
    }
    
    /**
     * This function is used to get contacts by ids and add contacts to chatting list.
     *
     * @param contactIds array data that includes ids to get contacts.
     * @param conf This is object that contains essential key values for switching backend
     */
    func getContacts(array contactIds: Array<String>, object conf: [String: Any]) {
        self.networkService.searchContacts(array: contactIds, object: conf) { result in
            switch result {
            case .success(let contactRes):
                DispatchQueue.main.async {
                    /**
                     * Contacta are added to chatting list.
                     */
                    for contact in contactRes.result {
                        do {
                            let jsonContact = try JSONEncoder().encode(contact)
                            if let strContact = String(data: jsonContact, encoding: .utf8) {
                                let message = ChatMessage(
                                    type: TypeChat.WIDGET,
                                    message: "",
                                    widgetType: TypeWidget.CONTACT,
                                    widgetDesc: strContact
                                )
                                self.messages.append(message)
                            }
                        } catch {
                            self.addErrorMessage(errorMessage: "json parsing error")
                        }
                    }
                }
            case .failure(let error):
                /**
                 * To ensure convenience in development, this log function is added.
                 */
                print(error.localizedDescription)
                self.addErrorMessage(errorMessage: "network error")
            }
        }
    }
}
