import SwiftUI

struct ContactView: View {
    @State var contact: Contact
    
    var body: some View {
        VStack {
            Text(contact.displayName).padding()
                .bold()
                .foregroundColor(.white)
                .font(.system(size: 18))
                .padding()
            List {
                ForEach(contact.phoneNumbers, id: \.self) { phone in
                    Text(phone)
                        .foregroundColor(.white)
                        .font(.system(size: 15))
                        .padding()
                }
            }.listStyle(PlainListStyle()).padding(.bottom, 10)
        }
    }
}

struct ContactView_Previews: PreviewProvider {
    static var previews: some View {
        ContactView(contact: Contact(
            contactId: "5",
            displayName: "Peter Luo",
            phoneNumbers: ["123-234-345"])
        )
    }
}
