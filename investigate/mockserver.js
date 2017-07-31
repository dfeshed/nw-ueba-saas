/* eslint-env node */
const path = require('path');
const subscriptionPath = path.join(__dirname, 'tests', 'data');
const recon = require('../recon').mockDestinations;

require('mock-server').startServer({
  subscriptionLocations: [subscriptionPath, recon]
});