import React from 'react';
import Panel from '../../Panel/Panel';

class DraggablePanel extends React.Component {
    constructor(props) {
        super(props);
        this.panelRef = React.createRef();
    }

    state = {
        dragging: false,
        initialX: 0,
        initialY: 0,
    };

    componentDidMount() {
        const rect = this.panelRef.current.getBoundingClientRect();
        this.panelRef.current.style.right = '25px'; // Update this property
        this.panelRef.current.style.bottom = '25px'; // Update this property

        window.addEventListener('mousemove', this.onMouseMove);
        window.addEventListener('mouseup', this.onMouseUp);
    }

    componentWillUnmount() {
        window.removeEventListener('mousemove', this.onMouseMove);
        window.removeEventListener('mouseup', this.onMouseUp);
    }

    onMouseDown = (event) => {
        if (event.button !== 0) return;
        const rect = this.panelRef.current.getBoundingClientRect();
        this.setState({
            dragging: true,
            initialX: event.clientX - rect.left,
            initialY: event.clientY - rect.top,
        });
    };

    onMouseMove = (event) => {
        if (!this.state.dragging) return;
        const x = event.clientX - this.state.initialX;
        const y = event.clientY - this.state.initialY;
        this.panelRef.current.style.left = `${x}px`;
        this.panelRef.current.style.top = `${y}px`;
    };

    onMouseUp = (event) => {
        if (event.button !== 0) return;
        this.setState({ dragging: false });
    };

    render() {
        return (
            <div
                ref={this.panelRef}
                onMouseDown={this.onMouseDown}
                style={{
                    position: 'fixed',
                    zIndex: 99999,
                    cursor: 'move',
                    right: 0, // Add this property
                    bottom: 0, // Add this property
                }}
            >
                <Panel />
            </div>
        );
    }
}

export default DraggablePanel;