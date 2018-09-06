/* eslint-env node */
// THE FOLLOWING IS AN EXAMPLE, WILL NEED TO BE CHANGED PER ENGINE

const common = require('../../common');
let mergedConfig;

module.exports = function(environment) {
  // cache it, prevents super spammy console as this gets called
  // many times during startup
  if (mergedConfig) {
    return mergedConfig;
  }

  // as of ember 2.14, for some reason environment can be undefined
  if (!environment) {
    return {};
  }

  mergedConfig = common.mergeSocketConfigs([], environment);
  return mergedConfig;
};
