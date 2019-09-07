/* eslint-env node */
const EmberAddon = require('ember-cli/lib/broccoli/ember-addon');
const shim = require('@html-next/flexi-layouts/lib/pod-templates-shim');
const { commonBuildOptionsNoBabel } = require('../common');

shim(EmberAddon);

module.exports = function(defaults) {
  const app = new EmberAddon(defaults, commonBuildOptionsNoBabel(__dirname));
  return app.toTree();
};