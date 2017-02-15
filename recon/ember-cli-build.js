/* eslint-disable */
var EmberAddon = require('ember-cli/lib/broccoli/ember-addon');
var shim = require('@html-next/flexi-layouts/lib/pod-templates-shim');

shim(EmberAddon);

module.exports = function(defaults) {
  var app = new EmberAddon(defaults, {
    babel: {
      stage: 0,
      includePolyfill: true
    }
  });

  /*
    This build file specifies the options for the dummy test app of this
    addon, located in `/tests/dummy`
    This build file does *not* influence how the addon or the app using it
    behave. You most likely want to be modifying `./index.js` or app's build file
  */

  app.import('vendor/haxors/promise.js');

  return app.toTree();
};
