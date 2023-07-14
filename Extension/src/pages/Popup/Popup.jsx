import React, {useState, useEffect} from 'react';
import {
    Divider,
    Input,
    Layout,
    Button,
} from 'antd';
import './Popup.css';

const {Footer, Content} = Layout;

const Popup = () => {
    const [hostName,setHostName] = useState("")
    const [openaiKey,setOpenaiKey] = useState("")
    const [pineconeKey,setPineconeKey] = useState("")
    const [pineconeEnv,setPineconeEnv] = useState("")
    const [firebaseKey,setFirebaseKey] = useState("")
    const [token,setToken] = useState("")
    const [uuid,setUuid] = useState("")
    const [temperature,setTemperature] = useState(0)

    /// Check if the user's system is in dark mode
    const darkModeMediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
    let isDarkMode = darkModeMediaQuery.matches;

    useEffect(() => {
        // Send a message to background script to get the storage value
        chrome.runtime.sendMessage({ method: 'getLocalStorage' }, (response) => {
            setOpenaiKey(response.data.openai_key);
            setPineconeKey(response.data.pinecone_key)
            setPineconeEnv(response.data.pinecone_env)
            setFirebaseKey(response.data.firebase_key)
            setToken(response.data.token)
            setUuid(response.data.uuid)
            setTemperature(response.data.temperature)
            setHostName(response.data.host_name)
        });
    }, []);


    const handleThemeChange = (e) => {
        isDarkMode = e.matches;
    };

    // Listen for changes in the theme data
    darkModeMediaQuery.addEventListener('change', handleThemeChange);

    const handleSave = () => {
        chrome.storage.local.set({
            "host_name": hostName,
            "openai_key": openaiKey,
            "pinecone_key": pineconeKey,
            "pinecone_env": pineconeEnv,
            "firebase_key": firebaseKey,
            "token": token,
            "uuid": uuid,
            "temperature": temperature
        }, () => {
            console.log('Successfully set to local storage')
        })
    }

    const handleCancel = () => {

    }

    return (
        <Layout className="main-layout" data-theme={isDarkMode ? 'dark': 'light'}>
            <div className="header">
                <h1>Settings</h1>
            </div>
            <Divider className="divider"/>
            <Content className="content">
                <Input
                    name="hostName"
                    addonBefore="Host Name"
                    value={hostName}
                    className="custom-input"
                    onChange={(e) => setHostName(e.target.value)}
                />
                <Input
                    name="openaiKey"
                    addonBefore="OpenAI Key"
                    value={openaiKey}
                    className="custom-input"
                    onChange={(e) => setOpenaiKey(e.target.value)}
                />
                <Input
                    name="pineconeKey"
                    addonBefore="Pinecone Key"
                    value={pineconeKey}
                    className="custom-input"
                    onChange={(e) => setPineconeKey(e.target.value)}
                />
                <Input
                    name="pineconeEnv"
                    addonBefore="Pinecone Env"
                    value={pineconeEnv}
                    className="custom-input"
                    onChange={(e) => setPineconeEnv(e.target.value)}
                />
                <Input
                    name="firebaseKey"
                    addonBefore="Firebase Key"
                    value={firebaseKey}
                    className="custom-input"
                    onChange={(e) => setFirebaseKey(e.target.value)}
                />
                <Input
                    name="token"
                    addonBefore="Token"
                    value={token}
                    className="custom-input"
                    onChange={(e) => setToken(e.target.value)}
                />
                <Input
                    name="uuid"
                    addonBefore="UUID"
                    value={uuid}
                    className="custom-input"
                    onChange={(e) => setUuid(e.target.value)}
                />
                <Input
                    name="temperature"
                    addonBefore="Temperature"
                    type="number"
                    value={temperature}
                    step="0.1"
                    min="0"
                    max="1"
                    className="custom-input"
                    onChange={(e) => setTemperature(e.target.value)}
                />
            </Content>
            <Divider className="divider"/>
            <Footer className="footer">
                <Button className="footer-btn" onClick={handleSave}>Save</Button>
                <Button className="footer-btn" onClick={handleCancel}>Cancel</Button>
            </Footer>
        </Layout>
    );
};

export default Popup;
