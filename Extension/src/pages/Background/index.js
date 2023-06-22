// Create the context menu item
chrome.runtime.onInstalled.addListener(function() {
    chrome.contextMenus.create({
        id: 'risingExtension',
        title: 'rising extension',
        contexts: ['page'],
    });
});

// Handle the context menu item click
chrome.contextMenus.onClicked.addListener(function(info, tab) {
    if (info.menuItemId === 'risingExtension') {
        chrome.tabs.query({ active: true, currentWindow: true }, function (tabs) {
            chrome.tabs.sendMessage(tabs[0].id, { action: "open-modal" });
        });
    }
});