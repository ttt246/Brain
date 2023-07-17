import SwiftUI
import KeychainSwift



struct ChatView: View {
    @State private var uuid: String = Constants.DEFAULT_UUID
    @State private var isStarted = false
    @StateObject var chatViewModel = ChatViewModel()
    
    var body: some View {
        VStack{
            ScrollView {
                    LazyVStack(spacing: 0) {
                        ForEach(chatViewModel.messages, id: \.self) { message in
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
                }.frame(width: 45, height: 45)
                    .cornerRadius(5).buttonStyle(PlainButtonStyle())
                
                TextField("", text: $chatViewModel.newMessage, onEditingChanged: { (isChanged) in
                    if isStarted && !isChanged {
                        isStarted = false
                    }
                }).frame(height: 45)
                    .overlay(
                        RoundedRectangle(cornerRadius: 6)
                            .stroke(Color.white, lineWidth: 1)
                    )
                Button(action: {
                    sendNotification()
                }) {
                    Image(systemName: "paperplane.fill")
                        .font(.system(size: 20))
                        .frame(maxWidth: .infinity)
                }.frame(width: 45, height: 45)
                    .cornerRadius(5)
            }
        }.edgesIgnoringSafeArea(.bottom)
    }
    
    func sendNotification() {
        chatViewModel.sendNotification(conf: Constants.getConfs(uuid: Constants.DEFAULT_UUID))
    }
}

struct ChatView_Previews: PreviewProvider {
    static var previews: some View {
        ChatView()
    }
}
