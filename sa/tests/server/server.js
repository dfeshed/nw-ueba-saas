/* eslint-disable */

var reconMockDirectory = require('../../../recon').mockDestinations;

require('mock-server').startServer({
  subscriptionLocations: [__dirname, reconMockDirectory]
});