/* eslint-disable */

var EmberApp = require('ember-cli/lib/broccoli/ember-app');
var shim = require('flexi/lib/pod-templates-shim');
var environmentConfig = require('./config/environment');

var appEnv = EmberApp.env();
var ENV = environmentConfig(appEnv);
var addonConfig = ENV['ember-cli-mirage'] || {};
var enabledInProd = (appEnv === "production") && addonConfig.enabled;
var mirageEnabled = enabledInProd || (appEnv !== 'production')

shim(EmberApp);

module.exports = function(defaults) {
  var app = new EmberApp(defaults, {
    babel: {
      stage: 0,
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
    app.import("vendor/incident.json");
    app.import("vendor/alerts.json");
    app.import("vendor/context.json");
  }
  // Pikaday-time library: for calendar picker.
  app.import(app.bowerDirectory + '/pikaday-time/pikaday.js');

  // Pikaday-time library: Default stylesheet for Pikadate-time.
  app.import(app.bowerDirectory + '/pikaday-time/css/pikaday.css');

  return app.toTree();
};
