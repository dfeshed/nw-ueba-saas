/* eslint-env node */

const EmberApp = require('ember-cli/lib/broccoli/ember-app');
const shim = require('@html-next/flexi-layouts/lib/pod-templates-shim');

const appEnv = EmberApp.env();

shim(EmberApp);

module.exports = function(defaults) {
  const app = new EmberApp(defaults, {
    autoprefixer: {
      browsers: ['last 2 versions', 'IE > 10'],
      enabled: appEnv !== 'test'
    },
    babel: {
      plugins: [
        'transform-object-rest-spread',
        'transform-decorators-legacy'
      ]
    },
    'ember-cli-babel': {
      includePolyfill: true
    }
  });

  return app.toTree();
};
