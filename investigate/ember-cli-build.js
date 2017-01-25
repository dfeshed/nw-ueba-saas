/* eslint-disable */
var EmberAddon = require('ember-cli/lib/broccoli/ember-addon');
var shim = require('flexi/lib/pod-templates-shim');

shim(EmberAddon);

module.exports = function(defaults) {
  var app = new EmberAddon(defaults, {
    babel: {
      stage: 0,
      includePolyfill: true
    }
  });

  return app.toTree();
};
