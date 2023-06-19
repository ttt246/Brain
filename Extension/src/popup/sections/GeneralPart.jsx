import { useTranslation } from 'react-i18next'
import { ModelMode, Models, ThemeMode, TriggerMode } from '../../config/index.mjs'
import PropTypes from 'prop-types'

GeneralPart.propTypes = {
  config: PropTypes.object.isRequired,
  updateConfig: PropTypes.func.isRequired,
}

export function GeneralPart({ config, updateConfig }) {
  const { t } = useTranslation()

  return (
    <>
      <label>
        <legend>{t('Triggers')}</legend>
        <select
          required
          onChange={(e) => {
            const mode = e.target.value
            updateConfig({ triggerMode: mode })
          }}
        >
          {Object.entries(TriggerMode).map(([key, desc]) => {
            return (
              <option value={key} key={key} selected={key === config.triggerMode}>
                {t(desc)}
              </option>
            )
          })}
        </select>
      </label>
      <label>
        <legend>{t('Theme')}</legend>
        <select
          required
          onChange={(e) => {
            const mode = e.target.value
            updateConfig({ themeMode: mode })
          }}
        >
          {Object.entries(ThemeMode).map(([key, desc]) => {
            return (
              <option value={key} key={key} selected={key === config.themeMode}>
                {t(desc)}
              </option>
            )
          })}
        </select>
      </label>
      <label>
        <legend>{t('Model Name')}</legend>
        <span style="display: flex; gap: 15px;">
          <select
            required
            onChange={(e) => {
              const modelName = e.target.value
              updateConfig({ modelName: modelName })
            }}
          >
            {config.activeApiModes.map((modelName) => {
              let desc
              if (modelName.includes('-')) {
                const splits = modelName.split('-')
                if (splits[0] in Models)
                  desc = `${t(Models[splits[0]].desc)} (${t(ModelMode[splits[1]])})`
              } else {
                if (modelName in Models) desc = t(Models[modelName].desc)
              }
              if (desc)
                return (
                  <option
                    value={modelName}
                    key={modelName}
                    selected={modelName === config.modelName}
                  >
                    {desc}
                  </option>
                )
            })}
          </select>
        </span>
      </label>
      <label>
        <input
          type="checkbox"
          checked={config.insertAtTop}
          onChange={(e) => {
            const checked = e.target.checked
            updateConfig({ insertAtTop: checked })
          }}
        />
        {t('Insert ChatGPT at the top of search results')}
      </label>
      <label>
        <input
          type="checkbox"
          checked={config.lockWhenAnswer}
          onChange={(e) => {
            const checked = e.target.checked
            updateConfig({ lockWhenAnswer: checked })
          }}
        />
        {t('Lock scrollbar while answering')}
      </label>
      <label>
        <input
          type="checkbox"
          checked={config.autoRegenAfterSwitchModel}
          onChange={(e) => {
            const checked = e.target.checked
            updateConfig({ autoRegenAfterSwitchModel: checked })
          }}
        />
        {t('Regenerate the answer after switching model')}
      </label>
      <label>
        <input
          type="checkbox"
          checked={config.alwaysPinWindow}
          onChange={(e) => {
            const checked = e.target.checked
            updateConfig({ alwaysPinWindow: checked })
          }}
        />
        {t('Always pin the floating window')}
      </label>
      <label>
        <input
          type="checkbox"
          checked={config.focusAfterAnswer}
          onChange={(e) => {
            const checked = e.target.checked
            updateConfig({ focusAfterAnswer: checked })
          }}
        />
        {t('Focus to input box after answering')}
      </label>
      <br />
    </>
  )
}
