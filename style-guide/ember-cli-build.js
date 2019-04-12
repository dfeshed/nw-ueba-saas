/* eslint-env node */
const EmberApp = require('ember-cli/lib/broccoli/ember-app');
const shim = require('@html-next/flexi-layouts/lib/pod-templates-shim');
const appEnv = EmberApp.env();

shim(EmberApp);

module.exports = function(defaults) {
  const app = new EmberApp(defaults, {
    autoprefixer: {
      browsers: ['last 2 versions'],
      enabled: appEnv !== 'test'
    },
    'ember-cli-babel': {
      includePolyfill: true
    },
    babel: {
      plugins: [
        'transform-object-rest-spread',
        'transform-decorators-legacy'
      ]
    },
    sassOptions: {
      includePaths: [
        'node_modules/ember-power-select/app/styles/',
        'node_modules/ember-basic-dropdown/app/styles/'
      ]
      // onlyIncluded: false
    }
  });

  return app.toTree();
};
