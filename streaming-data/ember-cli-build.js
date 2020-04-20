/* eslint-env node */
const EmberAddon = require('ember-cli/lib/broccoli/ember-addon');
const { basicOptions } = require('../common');

module.exports = function(defaults) {
  const app = new EmberAddon(defaults, basicOptions());
  return app.toTree();
};
