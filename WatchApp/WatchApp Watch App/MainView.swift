//
//  MainView.swift
//  WatchApp Watch App
//
//  Created by DarkHorse on 7/1/23.
//

import SwiftUI

struct MainView: View {
    @State private var isPresented = false
    
    var body: some View {
        ZStack {
            if isPresented {
                SettingView(isPresented: $isPresented)
            } else {
                ChatView(isPresented: $isPresented)
            }
        }
    }
}

struct MainView_Previews: PreviewProvider {
    static var previews: some View {
        MainView()
    }
}
