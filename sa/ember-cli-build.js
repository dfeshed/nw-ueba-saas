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

  const app = new EmberApp(defaults, buildOptions);
  app.import('node_modules/normalizr/dist/normalizr.amd.js', {
    using: [{ transformation: 'amd', as: 'normalizr' }]
  });
  app.import('node_modules/fast-sort/sort.es5.js', {
    using: [{ transformation: 'amd', as: 'fast-sort' }]
  });
  return app.toTree();
};
