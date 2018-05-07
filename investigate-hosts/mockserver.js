/* eslint-disable */
const allMocks = require('./index').mockDestinations;

require('mock-server').startServer({
  subscriptionLocations: allMocks
});
