//
//  ContentView.swift
//  WatchApp Watch App
//
//  Created by DarkHorse on 6/28/23.
//

import SwiftUI

struct ChatMessage: Hashable {
    let message: String
    let isUser: Bool
}

struct ChatBubble: Shape {
    let isUser: Bool

    func path(in rect: CGRect) -> Path {
        let path = UIBezierPath(roundedRect: rect,
                                byRoundingCorners: [.topLeft, .topRight, isUser ? .bottomLeft : .bottomRight],
                                cornerRadii: CGSize(width: 10, height: 10))
        return Path(path.cgPath)
    }
}

struct ContentView: View {
    @State private var messages: [ChatMessage] = []
        @State private var newMessage = ""
    
    var body: some View {
        VStack {
            List {
                ForEach(messages, id: \.self) { message in
                    HStack {
                        if message.isUser {
                            Spacer()
                            Text(message.message)
                                .foregroundColor(.white)
                                .padding(10)
                                .background(Color.blue)
                                .clipShape(ChatBubble(isUser: message.isUser))
                        } else {
                            Text(message.message)
                                .foregroundColor(.white)
                                .padding(10)
                                .background(Color.gray)
                                .clipShape(ChatBubble(isUser: message.isUser))
                                    Spacer()
                        }
                    }
                }
            }
                    
            HStack {
                TextField("Message...", text: $newMessage)
                Button(action: {
                    let userMessage = ChatMessage(message: newMessage, isUser: true)
                    messages.append(userMessage)
                    newMessage = ""
                }) {
                    Text("Send")
                }
            }
            .padding()
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
