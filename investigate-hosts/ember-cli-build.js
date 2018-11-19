/* eslint-env node */
const EmberAddon = require('ember-cli/lib/broccoli/ember-addon');
const shim = require('@html-next/flexi-layouts/lib/pod-templates-shim');
const { commonBuildOptions } = require('../common');

shim(EmberAddon);

module.exports = function(defaults) {
  const app = new EmberAddon(defaults, commonBuildOptions(__dirname));

  app.import('node_modules/normalizr/dist/normalizr.amd.js', {
    using: [{ transformation: 'amd', as: 'normalizr' }]
  });

  return app.toTree();
};