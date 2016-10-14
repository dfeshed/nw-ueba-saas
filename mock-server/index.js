/* eslint-disable */

// Interim start file to allow babel-register to be brought in
// This lets all files required in after this be es6/7
require('babel-register')({
  // Normally babel ignores node_modules, so you have to tell it not too.
  // This allows mock-server to use used as a node_module without
  // first transpiling the code to es5.
  ignore: /node_modules\/(?!mock-server)/,

  // Presets have to be added here rather than in a .babelrc
  // because of how this module dynamically loads files from
  // other places in the file system
  presets: ["babel-preset-stage-0", "babel-preset-es2015"].map(require.resolve)
});

var server = require('./lib/server');
var subscriptions = require('./shared/subscriptions');
var util = require('./shared/util/');

// The public interface of mock-server
module.exports = {
  startServer: server.start,
  shared: {
    subscriptions
  },
  util
};