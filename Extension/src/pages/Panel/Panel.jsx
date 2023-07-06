import React, {useEffect, useRef, useState} from 'react';
import {Divider, Input, Layout} from 'antd';
import {SendOutlined} from '@ant-design/icons';
import { getDatabase, onValue, ref } from "firebase/database";

import Message from './Message'
import './Panel.css';
import app from '../../configs/firebase-config'

const {Footer, Content} = Layout;
const confs = {
    "openai_key": "",
    "pinecone_key": "",
    "pinecone_env": "",
    "firebase_key": "",
    "token": "",
    "uuid": "extension-uuid",
    "settings": {
        "temperature": 0.6
    }
}

const URL_BASE = 'https://ttt246-brain.hf.space/'
const URL_SEND_NOTIFICATION = URL_BASE + 'sendNotification'
const URL_BROWSER_ITEM = URL_BASE + 'browser/item'
const URL_ASK_WEBSITE = URL_BASE + 'browser/ask'
const URL_DELETE_RTD = URL_BASE + 'auto_task/delete'

let prompt = ""

const Panel = () => {
    const [question, setQuestion] = useState("");
    const [messages, setMessages] = useState([]);
    const [isLoading, setLoadingStatus] = useState(false);
    const chat_box = useRef(null);

    const handleQuestionUpdated = (event) => {
        if (event.key === "Enter" && !isLoading) {
            addMessage(question, true);
            sendNotification(question)
            prompt = question
        }
    };

    const handleQuestionChange = (event) => {
        setQuestion(event.target.value);
    };

    useEffect(() => {
        chat_box.current.scrollTop = chat_box.current.scrollHeight;
    }, [messages]);

    const addMessage = (message, type, isLoading = false) => {
        if (message === "") return;

        if (!type && !isLoading) {
            messages.pop()
        }

        setMessages((messages) => [
            ...messages,
            {
                "message": message,
                "type": type,
                "isLoading": isLoading
            }]);
    };

    /*
     * methods for sending request to server
     */
    const sendRequest =  async (params, url) => {
        const requestOptions = {
            method: 'POST', headers: {
                'Content-Type': 'application/json'
            }, body: JSON.stringify(params)
        }

        setLoadingStatus(true)
        const response = await fetch(url, requestOptions)
        const data = await response.json()
        setLoadingStatus(false)

        return data
    }

    const sendNotification = async () => {
        const params = {
            "confs": confs,
            "message": question
        }
        setQuestion("")

        sendRequest(params, URL_SEND_NOTIFICATION).then(data => {            
            if (data.result === undefined || data.result == null) {
                return
            }

            try {
                if (data.result.program === undefined || data.result.program == null) {
                    addMessage(data.result, false)
                    return
                }

                let page = 0
                const currentUrl = window.location.href
                
                switch (data.result.program) {
                    case "browser":
                        addMessage(data.result.content, false)
                        window.location.assign(data.result.content)
                        break
                    case 'opentab':
                        addMessage("open tab", false)
                        window.open('https://google.com/search?q=', '_blank', 'noreferrer')
                        break
                    case 'opentabsearch':
                        addMessage("open tab and search", false)
                        window.open('https://google.com/search?q=' + data.result.content, '_blank', 'noreferrer')
                        break
                    case 'closetab':
                        addMessage("close tab", false)
                        window.close()
                        break
                    case 'previouspage':
                        if (page === 0) {
                            page = 0
                        } else {
                            page -= 10
                        }

                        addMessage("go to previous page", false)
                        window.location.assign(currentUrl + '&start=' + page)
                        break
                    case 'nextpage':
                        page += 10

                        addMessage("go to next page", false)
                        window.location.assign(currentUrl + '&start=' + page)
                        break
                    case 'scrollup':
                        addMessage("scroll up", false)
                        window.scrollBy(0, -300)
                        break
                    case 'scrolldown':
                        addMessage("scroll down", false)
                        window.scrollBy(0, 300)
                        break
                    case 'scrolltop':
                        addMessage("scroll to top", false)
                        window.scrollTo(0, 0)
                        break
                    case 'scrollbottom':
                        addMessage("scroll to bottom", false)
                        window.scrollTo(0, document.body.scrollHeight)
                        break
                    case 'message':
                        addMessage(data.result.content, false)
                        break
                    case 'selectitemdetailinfo':
                        addMessage("I need more detail info to select item", false)
                        selectItem()
                        break
                    case 'askwebsite':
                        askAboutCurrentWebsite()
                        break
                    case 'autotask':
                        autoTask(data.result.content)
                        break
                    default:
                        break
                }
            } catch (e) {
                throw e
            }
        })
    }

    const selectItem = async () => {
        const params = {
            "confs": confs,
            "prompt": prompt,
            "items": scrapeATags()
        }
        sendRequest(params, URL_BROWSER_ITEM).then(data => {
            window.open(data.result.content, '_blank', 'noreferrer')
        }).catch(err => {       
        })
    }

    const askAboutCurrentWebsite = async () => {
        const params = {
            "confs": confs,
            "prompt": prompt,
            "items": scrapeWebsites()
        }
        sendRequest(params, URL_ASK_WEBSITE).then(data => {
            addMessage(data.result.content, false)            
        }).catch(err => {            
        })
    }

    const deleteRtdInFirebase = async (refLink) => {
        const params = {
            "confs": confs,
            "data": {
                "reference_link": refLink
            }
        }

        sendRequest(params, URL_DELETE_RTD).then(data => {
            console.log(data.result)
        }).catch(err => {   
            console.error(err)         
        })
    }

    /*
     * methods for scraping websites
     */
    const scrapeATags = () => {
        const links = []

        const aTags = document.querySelectorAll('a')

        aTags.forEach((aTag) => {
            const content = aTag.textContent || ''
            const hrefElement = document.createElement('a')

            hrefElement.href = aTag.getAttribute('href') || ''
            const href = hrefElement.href

            links.push({ title: content, link: href })
        })

        return links
    }

    const scrapeWebsites = () => {
        const data = []
        const tags = document.querySelectorAll(['a', 'p', 'h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'h7'])
        tags.forEach((tag) => {
            const content = tag.textContent || ''
            if(content !== '')
                data.push(content)
        })
        return data
    }

    const autoTask = (referenceUrl) => {
        const refUrl = referenceUrl.slice(1)
        
        // getting real time database
        const db = getDatabase(app)
        const dbRef = ref(db, refUrl);

        // listen for data changes
        onValue(dbRef, (snapshot) => {    
            let data = []    
            let result = [] 
            let resultToStr = ""

            // getting data object from real time database
            if (snapshot.val() !== null && snapshot.val() !== undefined) {
                data = Object.values(snapshot.val())
            }

            if (typeof data[data.length - 1] !== 'undefined' && data[data.length-1].hasOwnProperty('thoughts')) {
                    result.push(data[data.length-1].thoughts)
            } else if (typeof data[data.length - 1] !== 'undefined' && data[data.length-1].hasOwnProperty('result')) {
                    result.push(data[data.length-1])
            }

            // convert json object to string
            result?.map(item => {
                if (item.hasOwnProperty('criticism')) {
                    resultToStr += "criticism:" + item.criticism + "\n" + 
                        "plan: " + item.plan + "\n" +
                        "reasoning: " + item.reasoning + "\n" +
                        "speak: " + item.speak + "\n" +
                        "text: " + item.text
                } else if (item.hasOwnProperty('result')) {
                    resultToStr += "result: " + item.result + "\n"
                }
            })
            
            addMessage(resultToStr, false)

            // delete database of firebase when finish auto task
            data?.map(item => {
                if (item.hasOwnProperty('command') && item.command.name === 'finish') {
                    deleteRtdInFirebase(referenceUrl).then(() => {
                        addMessage("Task is successfully completed!", false)
                    }).catch(err => {
                        console.error((err))
                    })
                }
            })
        }, (error) => {
            console.error(error);
        })
    }

    /// Check if the user's system is in dark mode
    const darkModeMediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
    let isDarkMode = darkModeMediaQuery.matches;

    const handleThemeChange = (e) => {
        isDarkMode = e.matches;
    };

    // Listen for changes in the theme data
    darkModeMediaQuery.addEventListener('change', handleThemeChange);

    return (
        <Layout className="main-layout" data-theme={isDarkMode ? 'dark': 'light'}>
            <div className="header">
                <h4>RisingBrowser</h4>
            </div>
            <Divider className="divider"/>
            <Content className="content" ref={chat_box}>
                {
                    messages.map((message) => (
                        <Message message={message.message} type={message.type} isLoading={message.isLoading}/>
                    ))
                }
                {isLoading ? <Message type={false} isLoading={true}/> : <div/>}
            </Content>
            <Divider className="divider"/>
            <Footer className="footer">
                <Input
                    addonAfter={<SendOutlined/>}
                    value={question}
                    onChange={handleQuestionChange}
                    onKeyDown={handleQuestionUpdated}/>
            </Footer>
        </Layout>
    );
};

export default Panel;
