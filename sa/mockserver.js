/* eslint-disable */

var reconMocks = require('../recon').mockDestinations;
var respondMocks = require('../respond').mockDestinations;
var contextMocks = require('../context').mockDestinations;
var investigateMocks = require('../investigate-events').mockDestinations;

require('mock-server').startServer({
  subscriptionLocations: [reconMocks, respondMocks, contextMocks, investigateMocks]
});
