/* eslint-env node */

const EmberApp = require('ember-cli/lib/broccoli/ember-app');
const shim = require('@html-next/flexi-layouts/lib/pod-templates-shim');
const { commonBuildOptions } = require('../common');

const buildOptions = commonBuildOptions(__dirname);

const appEnv = EmberApp.env();

shim(EmberApp);

module.exports = function(defaults) {

  buildOptions.autoprefixer = {
    browsers: ['last 2 versions'],
    enabled: appEnv !== 'test'
  };

  // Disabling fingerprinting allows the core team to much more
  // easily include our build assets
  buildOptions.fingerprint = {
    enabled: false
  };

  const options = {
    ...defaults,
    ...buildOptions
  };

  const app = new EmberApp(options);
  return app.toTree();
};
