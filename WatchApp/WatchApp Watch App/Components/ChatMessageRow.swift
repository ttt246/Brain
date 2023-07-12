import SwiftUI

public enum TypeChat: String {
    case SEND = "SEND"
    case RECEIVE = "RECEIVE"
    case WIDGET = "WIDGET"
}

public enum TypeWidget: String {
    case EMPTY = "EMPTY"
    case CONTACT = "CONTACT"
    case IMAGE = "IMAGE"
}

struct ChatMessage: Hashable {
    let type: TypeChat
    let message: String
    let widgetType: TypeWidget
    let widgetDesc: String
}

struct ChatBubble: Shape {
    func path(in rect: CGRect) -> Path {
        let path = UIBezierPath(roundedRect: rect,
                                byRoundingCorners: [.allCorners],
                                cornerRadii: CGSize(width: 9, height: 9))
        return Path(path.cgPath)
    }
}

struct ChatMessageRow: View {
    let chatItem: ChatMessage

    var body: some View {
        HStack {
            switch chatItem.type {
            case TypeChat.SEND:
                Spacer()
                Text(chatItem.message)
                    .font(.system(size: 15))
                    .foregroundColor(.white)
                    .padding(5)
                    .background(Color.green)
                    .clipShape(ChatBubble())
            case TypeChat.RECEIVE:
                Text(chatItem.message)
                    .font(.system(size: 15))
                    .foregroundColor(.black)
                    .padding(5)
                    .background(Color.primary)
                    .clipShape(ChatBubble())
                Spacer()
            case TypeChat.WIDGET:
                switch chatItem.widgetType {
                case TypeWidget.CONTACT:
                    let contact = convertStringToContact(jsonData: chatItem.widgetDesc)
                    NavigationLink(destination: ContactView(contact: contact)){
                        ContactRow(contact: contact)
                    }
                case TypeWidget.IMAGE:
                    ImageRow()
                default:
                    EmptyView()
                }
            }
        }.padding(5)
    }
}

func convertStringToContact(jsonData: String) -> Contact {
    do {
        let jsonData = jsonData.data(using: .utf8)!
        let contact = try JSONDecoder().decode(Contact.self, from: jsonData)

        return contact
    } catch {
        print("Error converting JSON string to object: \(error)")
    }
    return Contact(contactId: "", displayName: "Empty", phoneNumbers: [])
}

struct ChatMessageRow_Previews: PreviewProvider {
    static var previews: some View {
        ChatMessageRow(chatItem:ChatMessage(
                type: TypeChat.WIDGET,
                message: "Hello!",
                widgetType: TypeWidget.IMAGE,
                widgetDesc: "Nothing"
            )
        )
    }
}
