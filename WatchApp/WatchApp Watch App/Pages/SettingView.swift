import SwiftUI
import KeychainSwift

struct SettingView: View {
    @State private var uuid: String = Constants.DEFAULT_UUID
    
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
                }) {
                    Text("Save")
                }
                Spacer()
                Button(action: {}) {
                    Text("Cancel")
                }
                Spacer()
            }
        }
    }
}

struct SettingView_Previews: PreviewProvider {
    static var previews: some View {
        SettingView()
    }
}
