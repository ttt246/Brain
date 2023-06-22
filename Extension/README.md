<img src="src/assets/img/icon-128.png" width="64"/>

# Chatbot Chrome Extension

[![npm](https://img.shields.io/npm/v/chrome-extension-boilerplate-react)](https://www.npmjs.com/package/chrome-extension-boilerplate-react)
[![npm-download](https://img.shields.io/npm/dw/chrome-extension-boilerplate-react)](https://www.npmjs.com/package/chrome-extension-boilerplate-react)
[![npm](https://img.shields.io/npm/dm/chrome-extension-boilerplate-react)](https://www.npmjs.com/package/chrome-extension-boilerplate-react)

## Announcements

- Recently updated from **[React](https://reactjs.org)** ~~17~~ to **18**!
- **_This boilerplate adopts [Manifest V3](https://developer.chrome.com/docs/extensions/mv3/intro/mv3-overview/)!_**
  - For V2 users, please check out the [manifest-v2](https://github.com/lxieyang/chrome-extension-boilerplate-react/tree/manifest-v2) branch, or use version [3.x](https://www.npmjs.com/package/chrome-extension-boilerplate-react/v/3.3.0).
  - Check out the [Manifest V3 Migration Guide](https://developer.chrome.com/docs/extensions/mv3/intro/mv3-migration/).
- Recently added [devtools](https://developer.chrome.com/docs/extensions/mv3/devtools/) Support! Thanks [GeekaholicLin](https://github.com/lxieyang/chrome-extension-boilerplate-react/issues/17)!
- Recently updated from **[Webpack Dev Server](https://webpack.js.org/configuration/dev-server/)** ~~3.x~~ to **4.x** and **[Webpack](https://webpack.js.org/)** ~~4~~ to **5**!
- Recently added [TypeScript](https://www.typescriptlang.org/) Support!
- Recently used [davinci 003](https://beta.openai.com/) and [gpt-3.5-turbo](https://beta.openai.com/) of OpenAI

## Features

This is a Chatbot Chrome Extensions.

This extension is updated with:

- [Gpt-3.5-turbo](https://beta.openai.com/)
- [Chrome Extension Manifest V3](https://developer.chrome.com/docs/extensions/mv3/intro/mv3-overview/)
- [React 18](https://reactjs.org)
- [Webpack 5](https://webpack.js.org/)
- [Webpack Dev Server 4](https://webpack.js.org/configuration/dev-server/)
- [React Refresh](https://www.npmjs.com/package/react-refresh)
- [react-refresh-webpack-plugin](https://github.com/pmmmwh/react-refresh-webpack-plugin)
- [eslint-config-react-app](https://www.npmjs.com/package/eslint-config-react-app)
- [Prettier](https://prettier.io/)
- [TypeScript](https://www.typescriptlang.org/)


## Installing and Running

### Procedures:

1. Check if your [Node.js](https://nodejs.org/) version is >= **18**.
2. Clone this repository.
3. Change the package's `name`, `description`, and `repository` fields in `package.json`.
4. Change the name of your extension on `src/manifest.json`.
5. Run `npm install` to install the dependencies.
6. Run `npm start`
7. Load your extension on Chrome following:
   1. Access `chrome://extensions/`
   2. Check `Developer mode`
   3. Click on `Load unpacked extension`
   4. Select the `build` folder.
8. Happy hacking.


Saeid Nobahari
