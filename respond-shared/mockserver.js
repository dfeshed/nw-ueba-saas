/* eslint-disable */
const path = require('path');

const contextMockDirectory = require('../context').mockDestinations;

require('mock-server').startServer({
  subscriptionLocations: [
    contextMockDirectory
  ]
});
