/* eslint-disable */
var EmberApp = require('ember-cli/lib/broccoli/ember-app');
var shim = require('flexi/lib/pod-templates-shim');

shim(EmberApp);

module.exports = function(defaults) {
  var app = new EmberApp(defaults, {
    babel: {
      stage: 0
    },
    nodeAssets: {
      'highlightjs': {
        import: ['highlight.pack.js']
      }
    }
  });

  // Ember template compiler: Optional ember library for compiling templates in browser.
  // This app uses this compiler to generate live Components from HBS template strings.
  app.import(app.bowerDirectory + "/ember/ember-template-compiler.js");

  // Pikaday-time library: for calendar picker.
  app.import(app.bowerDirectory + '/pikaday-time/pikaday.js');

  // Pikaday-time library: Default stylesheet for Pikadate-time.
  app.import(app.bowerDirectory + '/pikaday-time/css/pikaday.css');

  return app.toTree();
};
