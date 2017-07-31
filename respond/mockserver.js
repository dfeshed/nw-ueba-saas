/* eslint-disable */
const path = require('path');
const subscriptionPath = path.join(__dirname, 'tests', 'data');

var contextMockDirectory = require('../context').mockDestinations;

require('mock-server').startServer({
  subscriptionLocations: [subscriptionPath, contextMockDirectory]
});
