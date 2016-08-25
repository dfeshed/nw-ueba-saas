/* eslint-disable */
var EmberAddon = require('ember-cli/lib/broccoli/ember-addon');

module.exports = function(defaults) {
  var app = new EmberAddon(defaults, {
    babel: {
      stage: 0
    }
  });


  // Websocket libraries: SockJS & STOMP
  app.import(app.bowerDirectory + "/sockjs/sockjs.js");
  app.import(app.bowerDirectory + "/stomp-websocket/lib/stomp.js");

  /*
    This build file specifies the options for the dummy test app of this
    addon, located in `/tests/dummy`
    This build file does *not* influence how the addon or the app using it
    behave. You most likely want to be modifying `./index.js` or app's build file
  */

  return app.toTree();
};
