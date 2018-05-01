/* eslint-disable */
const path = require('path');
const subscriptionPath = path.join(__dirname, 'tests', 'data');
const usmConfiguration = require('../admin-source-management').mockDestinations;

require('mock-server').startServer({
  subscriptionLocations: [subscriptionPath, usmConfiguration]
});
