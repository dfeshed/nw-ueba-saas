/* global require, module */

var EmberApp = require('ember-cli/lib/broccoli/ember-app');

module.exports = function(defaults) {
    var app = new EmberApp(defaults, {
        // Any other options
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

    // Websocket libraries: SockJS & STOMP
    app.import(app.bowerDirectory + "/sockjs/sockjs.js");
    app.import(app.bowerDirectory + "/stomp-websocket/lib/stomp.js");

    // Crossfilter library (for filtering, aggregating & sorting).
    app.import(app.bowerDirectory + "/crossfilter/crossfilter.js");

    // Dropdown libraries: tether & drop (drop depends on tether)
    app.import(app.bowerDirectory + "/tether/dist/js/tether.js");
    app.import(app.bowerDirectory + "/tether-drop/dist/js/drop.js");

    // Mock websocket library: MockSocket (only imported with mirage)
    (function(){
        function _mirageIsEnabled(){
            var addonConfig = app.project.config(app.env)['ember-cli-mirage'] || {},
                enabledInProd = (app.env === "production") && addonConfig.enabled;
            return enabledInProd || (app.env !== 'production');
        }
        if (_mirageIsEnabled()){
            app.import(app.bowerDirectory + "/mock-socket/dist/mock-socket.js");

            // Load the JSON file with incidents
            app.import("vendor/incident.json");
            app.import("vendor/alerts.json");
        }
    })();
    return app.toTree();
};
