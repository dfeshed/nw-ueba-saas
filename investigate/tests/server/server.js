/* eslint-env node */

const recon = require('../../../recon').mockDestinations;

require('mock-server').startServer({
  subscriptionLocations: [__dirname, recon]
});