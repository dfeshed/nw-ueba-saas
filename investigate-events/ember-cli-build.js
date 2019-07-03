/* eslint-env node */
const EmberAddon = require('ember-cli/lib/broccoli/ember-addon');
const shim = require('@html-next/flexi-layouts/lib/pod-templates-shim');
const { commonBuildOptions } = require('../common');

shim(EmberAddon);

module.exports = function(defaults) {
  const app = new EmberAddon(defaults, commonBuildOptions(__dirname));

  app.import('node_modules/fast-sort/sort.es5.js', {
    using: [{ transformation: 'amd', as: 'fast-sort' }]
  });

  app.import('node_modules/ip-address/dist/ip-address-globals.js', {
    using: [{ transformation: 'amd', as: 'ip-address' }]
  });

  return app.toTree();
};
