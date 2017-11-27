/* eslint-disable */
const path = require('path');
const subscriptionPath = path.join(__dirname, 'tests', 'data');
const hostsScanConfiguration = require('../hosts-scan-configure').mockDestinations;

require('mock-server').startServer({
  subscriptionLocations: [subscriptionPath, hostsScanConfiguration]
});
