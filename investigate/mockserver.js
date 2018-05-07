/* eslint-env node */
const allMocks = require('./index').mockDestinations;

require('mock-server').startServer({
  subscriptionLocations: allMocks
});
