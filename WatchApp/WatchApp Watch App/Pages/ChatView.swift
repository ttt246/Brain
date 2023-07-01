//
//  ContentView.swift
//  WatchApp Watch App
//
//  Created by Hideki Sato on 6/27/23.
//

import SwiftUI
import KeychainSwift

struct ChatView: View {
    @Binding var isPresented: Bool
    @State private var messages: [ChatMessage] = []
    @State private var newMessage = ""
    @State private var uuid: String = "956a11be45cba4a4"
    
    var body: some View {
        VStack{
            Button(action: {
                self.isPresented = true
            }) {
                Image(systemName: "gearshape.fill")
                    .resizable()
                    .frame(width: 15, height: 15)
            }
            .frame(width: 50, height: 30)
            .background(Color.clear).buttonStyle(PlainButtonStyle()).padding(.trailing)
            
            List {
                ForEach(messages, id: \.self) { message in
                    ChatMessageRow(message:message)
                }
            }.listStyle(PlainListStyle()).padding(.bottom, 10)
            
            
            HStack() {
                TextField("Message...", text: $newMessage).frame(width: 120.0, height: 0)
                Button(action: {
                    let keychain = KeychainSwift()

                    // Try to get the UUID from the keychain
                    if keychain.get("uuid") == "" {
                        let uuid = "956a11be45cba4a4"
                        keychain.set(uuid, forKey: "uuid")
                    }
                    
                    let userMessage = ChatMessage(message: newMessage, isUser: true)
                    messages.append(userMessage)
                    
                    // Replace this URL with your server's URL
                    if let url = URL(string: "https://ttt246-brain.hf.space/sendNotification") {
                        var request = URLRequest(url: url)
                        request.httpMethod = "POST" // Change this to any HTTP method you want
                        request.addValue("application/json", forHTTPHeaderField: "Content-Type") // Add any necessary headers
                        
                        let confsData: [String: Any] = [
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
                        
                        let postData: [String: Any] = ["confs": confsData, "message": newMessage]
                        
                        do {
                            let jsonData = try JSONSerialization.data(withJSONObject: postData, options: .prettyPrinted)
                            URLSession.shared.uploadTask(with: request, from: jsonData) { data, response, error in
                                guard error == nil, let data = data else {
                                    print("Error: \(error?.localizedDescription ?? "Unknown error")")
                                    return
                                }
                                
                                do {
                                    // Here we are converting data into JSON using JSONSerialization
                                    if let jsonResult = try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any] {
                                        if let result = jsonResult["result"] as? [String: Any], let content = result["content"] as? String {
                                            DispatchQueue.main.async {
                                                let serverResponse = ChatMessage(message: content, isUser: false)
                                                messages.append(serverResponse)
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
                }) {
                    Image(systemName: "paperplane.fill")
                }.frame(width: 50.0, height: 35)
            }
        }.padding().edgesIgnoringSafeArea(.all)
    }
}



struct ChatView_Previews: PreviewProvider {
    static var previews: some View {
        ChatView(isPresented: .constant(false))
    }
}
