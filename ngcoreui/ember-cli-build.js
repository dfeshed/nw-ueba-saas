/* eslint-env node */

const mergeTrees = require('broccoli-merge-trees');
const postcssCompiler = require('broccoli-theme');
const EmberApp = require('ember-cli/lib/broccoli/ember-app');
const shim = require('@html-next/flexi-layouts/lib/pod-templates-shim');
const { commonBuildOptions } = require('../common');

const buildOptions = commonBuildOptions(__dirname);

const appEnv = EmberApp.env();

shim(EmberApp);

module.exports = function(defaults) {

  buildOptions.autoprefixer = {
    browsers: ['last 2 versions', 'IE > 10'],
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

  const tree = app.toTree();
  const ieTree = postcssCompiler([tree], 'assets', 'ngcoreui.css');
  return mergeTrees([tree, ieTree]);
};
