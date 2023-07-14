// Create the context menu item
chrome.runtime.onInstalled.addListener(function() {
    chrome.contextMenus.create({
        id: 'risingExtension',
        title: 'rising extension',
        contexts: ['page'],
    });
});

// Handle the context menu item click
chrome.contextMenus.onClicked.addListener(function(info) {
    if (info.menuItemId === 'risingExtension') {
        chrome.tabs.query({ active: true, currentWindow: true }, function (tabs) {
            chrome.tabs.sendMessage(tabs[0].id, { action: "open-modal" });
        });
    }
});

// Handle the local storage get value
chrome.runtime.onMessage.addListener((request, sender, sendResponse) => {
    if (request.method === 'getLocalStorage') {
        chrome.storage.local.get(function(result) {
            sendResponse({ data: result });
        });
    }
    return true; // Important for asynchronous sendMessage
});