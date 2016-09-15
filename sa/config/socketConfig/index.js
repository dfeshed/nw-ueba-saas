var investigateConfigGen = require('./investigate');
var responseConfigGen = require('./response');
var contextConfigGen = require('./context');
var testConfigGen = require('./test');
var reconConfigGen = require('../../../recon').socketRouteGenerator;

// order matters, first config in wins if there are matching configs
var configGenerators = [
  testConfigGen,
  investigateConfigGen,
  responseConfigGen,
  contextConfigGen,
  reconConfigGen
];

var cache = null;

var generateSocketConfiguration = function(environment) {

  // this gets called a looooot on ember start up so use cache
  if (cache) {
    return cache;
  }

  var socketConfig = configGenerators
    .map((cG) => cG(environment))
    .reduce((previous, current) => {
      Object.keys(current).forEach((modelName) => {
        // Don't have this model? Add it
        if (!previous[modelName]) {
          previous[modelName] = current[modelName];
        } else {
          // Have this model? then check methods
          // and if method not present, add it,
          // otherwise ignore
          var methods = current[modelName];
          Object.keys(methods).forEach((method) => {
            // won't be overriding any socketUrls
            // and its the only special case in this config
            // that isn't a method
            if (method === 'socketUrl') {
              return;
            }

            if (!previous[modelName][method]) {
              previous[modelName][method] = methods[method];
            } else {
              console.log('Ignoring duplicate socket config for', modelName, method);
            }
          });
        }
      });
      return previous;
    }, {});

  cache = socketConfig;

  // UNCOMMENT to see combined socketConfig on startup
  // console.log(socketConfig)

  return socketConfig;
};

module.exports = generateSocketConfiguration;