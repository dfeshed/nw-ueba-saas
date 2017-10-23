/* eslint-disable */

var reconMocks = require('../recon').mockDestinations;
var respondMocks = require('../respond').mockDestinations;
var contextMocks = require('../context').mockDestinations;
var investigateEventsMocks = require('../investigate-events').mockDestinations;
var investigateFilesMocks = require('../investigate-files').mockDestinations;
var investigateHostsMocks = require('../investigate-hosts').mockDestinations;
var preferencesMocks = require('../preferences').mockDestinations;

require('mock-server').startServer({
  subscriptionLocations: [
    reconMocks,
    respondMocks,
    contextMocks,
    investigateEventsMocks,
    investigateFilesMocks,
    investigateHostsMocks,
    preferencesMocks
  ]
});
