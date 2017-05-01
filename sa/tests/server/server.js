/* eslint-disable */

var reconMockDirectory = require('../../../recon').mockDestinations;
var respondMockDirectory = require('../../../respond').mockDestinations;
var contextMockDirectory = require('../../../context').mockDestinations;

require('mock-server').startServer({
  subscriptionLocations: [__dirname, reconMockDirectory, respondMockDirectory, contextMockDirectory]
});
