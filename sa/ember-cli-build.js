/* eslint-env node */

const EmberApp = require('ember-cli/lib/broccoli/ember-app');
const shim = require('@html-next/flexi-layouts/lib/pod-templates-shim');
const environmentConfig = require('./config/environment');

const appEnv = EmberApp.env();
const ENV = environmentConfig(appEnv);
const addonConfig = ENV['ember-cli-mirage'] || {};
const enabledInProd = (appEnv === 'production') && addonConfig.enabled;
const mirageEnabled = enabledInProd || (appEnv !== 'production');

shim(EmberApp);

module.exports = function(defaults) {
  const app = new EmberApp(defaults, {
    autoprefixer: {
      browsers: ['last 2 versions', 'IE > 10'],
      enabled: appEnv !== 'test'
    },
    babel: {
      stage: 0,
      optional: ['es6.spec.symbols'],
      includePolyfill: true
    },
    nodeAssets: {
      'crossfilter': {
        import: ['crossfilter.js']
      },
      'mock-socket': {
        srcDir: 'dist',
        import: ['mock-socket.js'],
        enabled: mirageEnabled
      }
    }
  });

  if (mirageEnabled) {
    // Load the JSON file with incidents
    app.import('vendor/incident.json');
    app.import('vendor/alerts.json');
    app.import('vendor/context.json');
    app.import('vendor/related-entity.json');
    app.import('vendor/liveconnect-feedback.json');
  }

  return app.toTree();
};
