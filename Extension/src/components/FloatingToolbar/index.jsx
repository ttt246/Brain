import ConversationCard from '../ConversationCard'
import { useState } from 'react'
import PropTypes from 'prop-types'
import { getClientPosition, setElementPositionInViewport } from '../../utils'
import Draggable from 'react-draggable'
import { useClampWindowSize } from '../../hooks/use-clamp-window-size'
import { useConfig } from '../../hooks/use-config.mjs'

function FloatingToolbar(props) {
  const [render, setRender] = useState(false)
  const [closeable, setCloseable] = useState(props.closeable)
  const [position, setPosition] = useState(getClientPosition(props.container))
  const [virtualPosition, setVirtualPosition] = useState({ x: 0, y: 0 })
  const windowSize = useClampWindowSize([750, 1500], [0, Infinity])
  const config = useConfig(() => {
    setRender(true)
    if (!props.triggered) {
      props.container.style.position = 'absolute'
      setTimeout(() => {
        const left = Math.min(
          Math.max(0, window.innerWidth - props.container.offsetWidth - 30),
          Math.max(0, position.x),
        )
        props.container.style.left = left + 'px'
      })
    }
  })

  if (!render) return <div />

  if (props.triggered) {
    const updatePosition = () => {
      const newPosition = setElementPositionInViewport(props.container, position.x, position.y)
      if (position.x !== newPosition.x || position.y !== newPosition.y) setPosition(newPosition) // clear extra virtual position offset
    }

    const dragEvent = {
      onDrag: (e, ui) => {
        setVirtualPosition({ x: virtualPosition.x + ui.deltaX, y: virtualPosition.y + ui.deltaY })
      },
      onStop: () => {
        setPosition({ x: position.x + virtualPosition.x, y: position.y + virtualPosition.y })
        setVirtualPosition({ x: 0, y: 0 })
      },
    }

    if (virtualPosition.x === 0 && virtualPosition.y === 0) {
      updatePosition() // avoid jitter
    }

    const onDock = () => {
      props.container.className = 'chatgptbox-toolbar-container-not-queryable'
      setCloseable(true)
    }

    if (config.alwaysPinWindow) onDock()

    return (
      <div data-theme={config.themeMode}>
        <Draggable
          handle=".draggable"
          onDrag={dragEvent.onDrag}
          onStop={dragEvent.onStop}
          position={virtualPosition}
        >
          <div
            className="chatgptbox-selection-window"
            style={{ width: windowSize[0] * 0.4 + 'px' }}
          >
            <div className="chatgptbox-container">
              <ConversationCard
                session={props.session}
                question={props.prompt}
                draggable={true}
                closeable={closeable}
                onClose={() => {
                  props.container.remove()
                }}
                dockable={props.dockable}
                onDock={onDock}
                onUpdate={() => {
                  updatePosition()
                }}
              />
            </div>
          </div>
        </Draggable>
      </div>
    )
  }
}

FloatingToolbar.propTypes = {
  session: PropTypes.object.isRequired,
  selection: PropTypes.string.isRequired,
  container: PropTypes.object.isRequired,
  triggered: PropTypes.bool,
  closeable: PropTypes.bool,
  dockable: PropTypes.bool,
  prompt: PropTypes.string,
}

export default FloatingToolbar
