/* eslint-disable */

var reconMockDirectory = require('../../../recon').mockDestinations;
var respondMockDirectory = require('../../../respond').mockDestinations;

require('mock-server').startServer({
  subscriptionLocations: [__dirname, reconMockDirectory, respondMockDirectory]
});