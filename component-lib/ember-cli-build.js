/* eslint-env node */
const EmberAddon = require('ember-cli/lib/broccoli/ember-addon');
const { commonBuildOptions } = require('../common');

module.exports = function(defaults) {
  const app = new EmberAddon(defaults, commonBuildOptions(__dirname));
  return app.toTree();
};