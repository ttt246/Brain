//
//  SettingView.swift
//  WatchApp Watch App
//
//  Created by DarkHorse on 7/1/23.
//

import SwiftUI
import KeychainSwift

struct SettingView: View {
    @Binding var isPresented: Bool
    @State private var uuid: String = "956a11be45cba4a4"
    
    var body: some View {
        VStack {
            TextField("uuid", text: $uuid)
                .foregroundColor(.white)
                .font(.system(size: 20))
                .padding()
            HStack {
                Spacer()
                Button(action: {
                    let keychain = KeychainSwift()
                    keychain.set("\($uuid)", forKey: "uuid")
                    self.isPresented = false
                }) {
                    Text("Yes")
                }
                Spacer()
                Button(action: {
                    self.isPresented = false
                }) {
                    Text("No")
                }
                Spacer()
            }
        }
    }
}

struct SettingView_Previews: PreviewProvider {
    static var previews: some View {
        SettingView(isPresented: .constant(true))
    }
}
