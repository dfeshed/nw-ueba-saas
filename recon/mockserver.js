/* eslint-disable */
const path = require('path');
const allMocks = require('./index').mockDestinations;

require('mock-server').startServer({
  subscriptionLocations: allMocks
});