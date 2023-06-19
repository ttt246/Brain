import '@picocss/pico'
import { useEffect, useState } from 'react'
import { defaultConfig, setUserConfig } from '../config/index.mjs'
import { Tab, TabList, TabPanel, Tabs } from 'react-tabs'
import 'react-tabs/style/react-tabs.css'
import './styles.scss'
import { useWindowTheme } from '../hooks/use-window-theme.mjs'
import { useTranslation } from 'react-i18next'
import { GeneralPart } from './sections/GeneralPart'
import { FeaturePages } from './sections/FeaturePages'
import { ModulesPart } from './sections/ModulesPart'

function Popup() {
  const { t } = useTranslation()
  const [config, setConfig] = useState(defaultConfig)
  const theme = useWindowTheme()

  const updateConfig = (value) => {
    setConfig({ ...config, ...value })
    setUserConfig(value)
  }

  useEffect(() => {
    document.documentElement.dataset.theme = config.themeMode === 'auto' ? theme : config.themeMode
  }, [config.themeMode, theme])

  const search = new URLSearchParams(window.location.search)
  const popup = search.get('popup') // manifest v2

  return (
    <div className={popup === 'true' ? 'container-popup-mode' : 'container-page-mode'}>
      <form style="width:100%;">
        <Tabs selectedTabClassName="popup-tab--selected">
          <TabList>
            <Tab className="popup-tab">{t('General')}</Tab>
            <Tab className="popup-tab">{t('Feature Pages')}</Tab>
            <Tab className="popup-tab">{t('Modules')}</Tab>
          </TabList>

          <TabPanel>
            <GeneralPart config={config} updateConfig={updateConfig} />
          </TabPanel>
          <TabPanel>
            <FeaturePages config={config} updateConfig={updateConfig} />
          </TabPanel>
          <TabPanel>
            <ModulesPart config={config} updateConfig={updateConfig} />
          </TabPanel>
        </Tabs>
      </form>
      <br />
    </div>
  )
}

export default Popup
