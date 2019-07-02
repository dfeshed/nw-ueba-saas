'use strict';

const EmberAddon = require('ember-cli/lib/broccoli/ember-addon');
const shim = require('@html-next/flexi-layouts/lib/pod-templates-shim');
const { commonBuildOptions } = require('../common');

shim(EmberAddon);

module.exports = function(defaults) {
  const app = new EmberAddon(defaults, commonBuildOptions(__dirname));
  return app.toTree();
};
