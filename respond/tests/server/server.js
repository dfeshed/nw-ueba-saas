/* eslint-disable */

var contextMockDirectory = require('../../../context').mockDestinations;

require('mock-server').startServer({
  subscriptionLocations: [__dirname, contextMockDirectory]
});
