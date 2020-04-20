/* eslint-disable */
const path = require('path');
const fs = require('fs');

// Be explicit about where modules are
// as occasionally builds get confused
const requirePaths = [__dirname, path.join(__dirname, '..')];
const resolve = (mod) => {
  return require.resolve(mod, { paths: requirePaths });
};

let babelRegister;
let possiblePath = path.join(__dirname, 'node_modules', 'babel-register');
if (fs.existsSync(possiblePath)) {
  babelRegister = require(possiblePath);
} else {
  possiblePath = path.join(__dirname, '..', 'node_modules', 'babel-register');
  if (fs.existsSync(possiblePath)) {
    babelRegister = require(possiblePath);
  }
}

if (!babelRegister) {
  console.error('COULD NOT FIND BABEL REGISTER');
  process.exit(1);
} else {
  console.log('FOUND BABEL-REGISTER');
}

// Interim start file to allow babel-register to be brought in
// This lets all files required in after this be es6/7
babelRegister({
  // Normally babel ignores node_modules, so you have to tell it not too.
  // This allows mock-server to use used as a node_module without
  // first transpiling the code to es5.
  ignore: /node_modules\/(?!mock-server)/,

  // Presets have to be added here rather than in a .babelrc
  // because of how this module dynamically loads files from
  // other places in the file system
  presets: ['babel-preset-stage-0', 'babel-preset-es2015'].map(resolve)
});

const server = require('./lib/server');
const subscriptions = require('./shared/subscriptions');
const util = require('./shared/util/');

// The public interface of mock-server
module.exports = {
  startServer: server.start,
  shared: {
    subscriptions
  },
  util
};