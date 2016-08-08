/* global require, module */
var EmberApp = require('ember-cli/lib/broccoli/ember-app');
var shim = require('flexi/lib/pod-templates-shim');

shim(EmberApp);

module.exports = function(defaults) {
  var app = new EmberApp(defaults, {
    babel: {
      stage: 0
    }
  });

  // Use `app.import` to add additional libraries to the generated
  // output files.
  //
  // If you need to use different assets in different
  // environments, specify an object as the first parameter. That
  // object's keys should be the environment name and the values
  // should be the asset to use in that environment.
  //
  // If the library that you are including contains AMD or ES6
  // modules that you would like to import into your application
  // please specify an object with the list of modules as keys
  // along with the exports of each module as its value.

  // Ember template compiler: Optional ember library for compiling templates in browser.
  // This app uses this compiler to generate live Components from HBS template strings.
  app.import(app.bowerDirectory + "/ember/ember-template-compiler.js");

  // HighlightJS library: for highlighting HBS snippets.
  app.import(app.bowerDirectory + '/highlightjs/highlight.pack.js');

  // ClipboardJS library: for Copy To Clipboard functionality.
  app.import(app.bowerDirectory + '/clipboard/dist/clipboard.js');

  return app.toTree();
};
