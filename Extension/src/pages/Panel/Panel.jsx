import React, {useEffect, useRef, useState} from 'react';
import {Divider, Input, Layout} from 'antd';
import {SendOutlined} from '@ant-design/icons';
import Message from './Message'

import './Panel.css';

const {Footer, Content} = Layout;

let currentATags = []
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

    const addMessage = (message, type, isLoading = false) => {
        if (message === "") return;

        if (!type && !isLoading) {
            console.log("delete loading")
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

    const sendNotification = async () => {
        const params = {
            "token": "string", "uuid": "string", "model": "gpt-3.5-turbo", "message": question
        }

        setQuestion("")

        const requestOptions = {
            method: 'POST', headers: {
                'Content-Type': 'application/json'
            }, body: JSON.stringify(params)
        }

        setLoadingStatus(true)
        const response = await fetch('https://ttt246-brain.hf.space/sendNotification', requestOptions)
        const data = await response.json()
        setLoadingStatus(false)

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
                case 'open_tab':
                    addMessage("open tab", false)
                    window.open('https://google.com/search?q=', '_blank', 'noreferrer')
                    break
                case 'open_tab_search':
                    addMessage("open tab and search", false)
                    window.open('https://google.com/search?q=' + data.result.content, '_blank', 'noreferrer')
                    break
                case 'close_tab':
                    addMessage("close tab", false)
                    window.close()
                    break
                case 'previous_page':
                    if (page === 0) {
                        page = 0
                    } else {
                        page -= 10
                    }

                    addMessage("go to previous page", false)
                    window.location.assign(currentUrl + '&start=' + page)
                    break
                case 'next_page':
                    page += 10

                    addMessage("go to next page", false)
                    window.location.assign(currentUrl + '&start=' + page)
                    break
                case 'scroll_up':
                    addMessage("scroll up", false)
                    window.scrollBy(0, -300)
                    break
                case 'scroll_down':
                    addMessage("scroll down", false)
                    window.scrollBy(0, 300)
                    break
                case 'scroll_top':
                    addMessage("scroll to top", false)
                    window.scrollTo(0, 0)
                    break
                case 'scroll_bottom':
                    addMessage("scroll to bottom", false)
                    window.scrollTo(0, document.body.scrollHeight)
                    break
                case 'message':
                    addMessage(data.result.content, false)
                    break
                case 'select_item_detail_info':
                    addMessage("I need more detail info to select item", false)
                    setLoadingStatus(true)
                    let data = await selectItem()
                    setLoadingStatus(false)

                    console.log(data)

                    if(data.result.program === "select_item")
                        window.open(data.result.content, '_blank', 'noreferrer')
                    break
                case 'select_item':
                    addMessage("select item", false)
                    break
                default:
                    break
            }
        } catch (e) {
            throw e
        }
    }

    const selectItem = async () => {
        const params = {
            "token": "string", "uuid": "string", "prompt": prompt, "items": currentATags
        }
        console.log(params)

        const requestOptions = {
            method: 'POST', headers: {
                'Content-Type': 'application/json'
            }, body: JSON.stringify(params)
        }

        setLoadingStatus(true)
        const response = await fetch('https://ttt246-brain.hf.space/browser/item', requestOptions)
        const data = await response.json()
        setLoadingStatus(false)

        return data
    }

    useEffect(() => {
        chat_box.current.scrollTop = chat_box.current.scrollHeight;
    }, [messages]);

    useEffect(() => {
        currentATags = scrapeATags()
    }, [])

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


    return (
        <Layout className="main-layout">
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
        </Layout>);
};

export default Panel;
