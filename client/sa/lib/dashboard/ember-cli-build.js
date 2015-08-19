/* jshint node: true */
/* global require, module */

var EmberAddon = require('ember-cli/lib/broccoli/ember-addon');

module.exports = function(defaults) {
    /*
    This ember-cli-build file specifes the options for the dummy test app of this
    addon, located in `/tests/dummy`

    This file does *not* influence how the addon or the app using it
    behave. You most likely want to be modifying `./index.js` or app's ember-cli-build
    */

    var app = new EmberAddon(defaults, {
        // Any other options
    });

    app.import('bower_components/highlightjs/highlight.pack.js');
    app.import('bower_components/highlightjs/styles/github.css');

    return app.toTree();
};
