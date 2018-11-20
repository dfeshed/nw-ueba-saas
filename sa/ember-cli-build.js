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

  const app = new EmberApp(defaults, buildOptions);
  app.import('node_modules/normalizr/dist/normalizr.amd.js', {
    using: [{ transformation: 'amd', as: 'normalizr' }]
  });
  const tree = app.toTree();
  const ieTree = postcssCompiler([tree], 'assets', 'sa.css');

  return mergeTrees([tree, ieTree]);
};
