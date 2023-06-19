import PropTypes from 'prop-types'
import { ApiModes } from './ApiModes'

ModulesPart.propTypes = {
  config: PropTypes.object.isRequired,
  updateConfig: PropTypes.func.isRequired,
}

export function ModulesPart({ config, updateConfig }) {
  return (
    <>
      <ApiModes config={config} updateConfig={updateConfig} />
    </>
  )
}
