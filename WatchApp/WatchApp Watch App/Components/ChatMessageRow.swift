//
//  MessageView.swift
//  WatchApp Watch App
//
//  Created by DarkHorse on 7/1/23.
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
                                cornerRadii: CGSize(width: 3, height: 3))
        return Path(path.cgPath)
    }
}

struct ChatMessageRow: View {
    let message: ChatMessage

    var body: some View {
        HStack {
            if message.isUser {
                Spacer()
                Text(message.message)
                    .font(.system(size: 15))
                    .foregroundColor(.white)
                    .padding(5)
                    .background(Color.blue)
                    .clipShape(ChatBubble(isUser: message.isUser))
            } else {
                Text(message.message)
                    .italic()
                    .font(.system(size: 15))
                    .foregroundColor(.black)
                    .padding(5)
                    .background(Color.primary)
                    .clipShape(ChatBubble(isUser: message.isUser))
                Spacer()
            }
        }.listRowBackground(Color.clear)
    }
}

struct ChatMessageRow_Previews: PreviewProvider {
    static var previews: some View {
        ChatMessageRow(message: ChatMessage(message: "Hello!", isUser: false))
    }
}
