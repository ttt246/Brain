import React from 'react';
import ReactDOM from 'react-dom'
import DraggablePanel from './modules/DraggablePanel'

const element = document.createElement('div')
element.style.position = 'fixed'
element.style.zIndex = 99999
element.style.right = 100 + 'px'
element.style.bottom = 100 + 'px'
document.documentElement.appendChild(element)

ReactDOM.render(<DraggablePanel />, element);
