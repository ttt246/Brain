import SwiftUI

struct ImageRow: View {
    var body: some View {
        HStack {
            AsyncImage(url: URL(string: "https://firebasestorage.googleapis.com/v0/b/test3-83ffc.appspot.com/o/images%2F00c08a46-8812-4b3c-ad9f-72d8d04a9368?alt=media&token=cacba50a-0241-445a-892b-5a0661aaaa19")) { image in
                image.resizable().aspectRatio(contentMode: .fit)
            } placeholder: {
                ProgressView()
            }
        }.padding(5).background(Color.white).clipShape(ChatBubble())
    }
}

struct ImageRow_Previews: PreviewProvider {
    static var previews: some View {
        ImageRow()
    }
}
