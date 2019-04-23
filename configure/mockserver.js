/* eslint-disable */
const mockDir = require('./index').mockDestinations;
const licenseMockDirectory = require('../license').mockDestinations;

require('mock-server').startServer({
  subscriptionLocations: [ mockDir, licenseMockDirectory ]
});
