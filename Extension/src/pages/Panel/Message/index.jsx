import React from 'react';

import './index.css';
import BouncingDotsLoader from "../BouncingDotsLoader";

const Message = (props) => {

    return (
        <div className="message">
            <div className={props.type ? "message_sent" : "message_received"}>
                {
                    props.isLoading ? <BouncingDotsLoader/> : props.message
                }
            </div>
        </div>
    );
};

export default Message;
