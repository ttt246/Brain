/**
 * @typedef {object} SiteConfigAction
 * @property {function} init
 */
/**
 * @typedef {object} SiteConfig
 * @property {string[]|function} inputQuery - for search box
 * @property {string[]} sidebarContainerQuery - prepend child to
 * @property {string[]} appendContainerQuery - if sidebarContainer not exists, append child to
 * @property {string[]} resultsContainerQuery - prepend child to if insertAtTop is true
 * @property {SiteConfigAction} action
 */
/**
 * @type {Object.<string,SiteConfig>}
 */
export const config = {
  google: {
    inputQuery: ["input[name='q']", "textarea[name='q']"],
    sidebarContainerQuery: ['#rhs'],
    appendContainerQuery: ['#rcnt'],
    resultsContainerQuery: ['#rso'],
  },
}
