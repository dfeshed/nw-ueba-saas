 /* eslint-env node */

const developedAddons = [];

 /**
  * Allows live-reloading when this addon changes even when being served by another projects `ember serve`.
  *
  * But isDevelopingAddon isn't just about live reload, it also controls
  * when certain linters lint the code in this project.
  *
  * We want linting to be run on this project when
  *   1) developing this addon
  *   2) developing something using this addon
  *   3) 'ember test'ing this addon
  *
  * We do not want linting to be run on this project when
  *   1) you are 'ember test'ing something using this addon
  *
  * @see https://github.com/ember-cli/ember-cli/blob/master/ADDON_HOOKS.md#isdevelopingaddon
  * @public
  */
const isDevelopingAddon = function(projectName) {
  return function() {

    // If it is the current project being processed
    // always true
    const projName = this.project.pkg.name;

    if (projName === projectName) {
      return true;
    }

    // If already processed, return false
    if (developedAddons.indexOf(projectName) > -1) {
      return false;
    }

    // We want to report as a 'developingAddon' when
    // we are NOT running 'ember test'
    const isDevAddon = process.env.EMBER_ENV !== 'test';

    if (isDevAddon) {
      developedAddons.push(projectName);
    }

    return isDevAddon;
  };
};

/**
 * This function is a utility for Ember apps for calculating socketUrls on startup.
 * This function takes the environment (`development`, `test`, `production`), the desired production socketUrl, inspects node.js process variables, and calculates the appropriate `socketUrl` to use.
 * If in `development` or `test` this function will calculate a URL that points to the `mock-server`.
 * To NOT point at the `mock-server` in `development` or `test`, start `ember` with the `NOMOCK` environment variable set to anything.
 * For example: `NOMOCK=1 ember s`
 * @public
 */
const determineSocketUrl = function(environment, productionPath) {
  // Set NOMOCK=anything (ex 'NOMOCK=1 ember s')
  // to not use mock in dev/test
  //
  // When running jenkins tests, the MOCK_PORT
  // is set to any of a number of possible ports
  // so need to get from 'process.env'

  let socketUrl;
  if ((environment === 'development' || environment === 'test') && !process.env.NOMOCK) {
    const mockPort = process.env.MOCK_PORT || 9999;
    socketUrl = `http://localhost:${mockPort}/socket/`;
  } else {
    socketUrl = productionPath;
  }
  return socketUrl;
};

const mergeSocketConfigs = function(configGenerators, environment) {
  return configGenerators
    .map((cG) => cG(environment))
    .reduce((previous, current) => {
      Object.keys(current).forEach((modelName) => {
        // Don't have this model? Add it
        if (!previous[modelName]) {
          previous[modelName] = current[modelName];
        } else {
          // Have this model? then check methods
          // and if method not present, add it,
          // otherwise ignore
          const methods = current[modelName];
          Object.keys(methods).forEach((method) => {
            // won't be overriding any socketUrls
            // and its the only special case in this config
            // that isn't a method
            if (method === 'socketUrl') {
              return;
            }

            if (!previous[modelName][method]) {
              previous[modelName][method] = methods[method];
            } else {
              console.log('Ignoring duplicate socket config for', modelName, method);
            }
          });
        }
      });
      return previous;
    }, {});
};

module.exports = {
  isDevelopingAddon,
  determineSocketUrl,
  mergeSocketConfigs
};