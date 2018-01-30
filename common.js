 /* eslint-env node */
const os = require('os');
const featureFlagDefaultHash = require('./feature-flags');

let featureFlagConfig;
let featuresOn = [];
let featuresOff = [];

if (process.env.FF_ON) {
  featuresOn = process.env.FF_ON.split(',');
}

if (process.env.FF_OFF) {
  featuresOff = process.env.FF_OFF.split(',');
}

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

    if (process.env.EMBER_ENV === 'production') {
      return false;
    }

    // If it is the current project being processed
    // always true
    const projName = this.project.pkg.name;

    if (projName === projectName) {
      return true;
    }

    // If already processed...
    if (developedAddons.indexOf(projectName) > -1) {

      // In order for live recompile/reload of changed code
      // to work on Windows, all the dependant addons must
      // eventually return true. If return false from here
      // Windows users will not have nested addon reload.
      //
      // Skipping Windows for this performance improvement
      // will significantly slow down startup of ember cli
      // and it will result in multiple outputs of things
      // like lint warnings.

      if (os.platform() !== 'win32') {

        // Act as if not developing addon
        // This stops ember-cli from re-processing an addon
        // over and over.
        return false;
      }
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
 * This function is a utility for Ember apps to calculate socketUrls on startup.
 *
 * If environment in `development` or `test`
 *   AND NOMOCK is not set
 *     the URL is set to the mock-server endpoint
 *     In this case, the user is using the local mock-server as a backend
 *   AND NOMOCK is set
 *     the URL is set to the passed in socketPath
 *     In this case, the user is using a locally running micro-service
 * If environment is not `development` or `test` (ex: `production`)
 *   the URL has `api` prepended to it as it is assumed to be hitting nginx
 *   in a prod or prod-like environment
 *
 * If the DOCKER flag is provided, then return the prod URL as we have a prod-like
 * deployed environment.
 *
 * @public
 */
const determineSocketUrl = function(environment, socketPath) {
  // production
  //
  // Prefix with /api as it is routed by nginx to
  // the appropriate microservice
  let socketUrl = `/api${socketPath}`;

  if (process.env.DOCKER) {
    return socketUrl;
  }

  if ((environment === 'development' || environment === 'test')) {
    if (!process.env.NOMOCK) {

      // Using mock-server
      //
      // When running jenkins tests, the MOCK_PORT
      // is set to any of a number of possible ports
      // so need to get from 'process.env'
      const mockPort = process.env.MOCK_PORT || 9999;
      socketUrl = `http://localhost:${mockPort}/socket`;
    } else {
      // Using microservice
      //
      // Just use the path passed in as it matches the path
      // the microservice uses
      socketUrl = socketPath;
    }
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

/*
 * Use this function to add defaults to an ember project.
 *
 * This function takes a normal feature flag object hash
 * as input and determines based on environment flags
 * what the feature flag should be set to.
 *
 * The hash value for each flag passed in is considered
 * the default if no environment flag sets it.
 *
 * If the hash value is a function, the determined flag
 * setting, based on environment settings, is passed into
 * that function and the return from the function is used
 * as the feature flag setting. This allows for an override if,
 * for instance, a flag should always be one way regardless
 * of feature flags.
 *
 */
const addFeatureFlags = function(environment) {

  if (!environment) {
    return {};
  }

  if (featureFlagConfig) {
    return featureFlagConfig;
  }

  featureFlagConfig = {}

  Object.keys(featureFlagDefaultHash).forEach((featureFlag) => {
    const providedDefault = featureFlagDefaultHash[featureFlag];

    if (featuresOff.includes(featureFlag)) {
      featureFlagConfig[featureFlag] = false;
    } else if (featuresOn.includes(featureFlag)) {
      featureFlagConfig[featureFlag] = true;
    } else if (typeof providedDefault === 'boolean') {
      featureFlagConfig[featureFlag] = providedDefault;
    }

    // allow for override function
    if (typeof providedDefault === 'function') {
      featureFlagConfig[featureFlag] = providedDefault(environment, featureFlagConfig[featureFlag]);
    }
  });

  return featureFlagConfig;
};

module.exports = {
  isDevelopingAddon,
  determineSocketUrl,
  mergeSocketConfigs,
  addFeatureFlags
};
