/* eslint-env node */
const EmberAddon = require('ember-cli/lib/broccoli/ember-addon');
const shim = require('@html-next/flexi-layouts/lib/pod-templates-shim');

shim(EmberAddon);

module.exports = function(defaults) {
  const app = new EmberAddon(defaults, {
    'ember-cli-babel': {
      includePolyfill: true
    },
    babel: {
      plugins: [
        'transform-object-rest-spread',
        'transform-decorators-legacy'
      ]
    }
  });
  return app.toTree();
};
