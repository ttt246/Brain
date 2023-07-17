import SwiftUI

struct ContactBubble: Shape {
    func path(in rect: CGRect) -> Path {
        let path = UIBezierPath(roundedRect: rect,
                                byRoundingCorners: [.allCorners],
                                cornerRadii: CGSize(width: 9, height: 9))
        return Path(path.cgPath)
    }
}

struct ContactRow: View {
    let contact: Contact

    var body: some View {
        HStack {
            Text(contact.displayName)
                .italic()
                .font(.system(size: 15))
                .foregroundColor(.white)
                .padding(5)
            Spacer()
            Image(systemName: "phone.fill")
                    .resizable()
                    .frame(width: 17, height: 17)
                    .foregroundColor(.white)
        }
        .background(Color.clear)
        .clipShape(ContactBubble())
    }
}

struct Contact_Previews: PreviewProvider {
    static var previews: some View {
        ContactRow(contact: Contact(
            contactId: "5",
            displayName: "Peter Luo",
            phoneNumbers: ["123-234-345"])
        )
    }
}
