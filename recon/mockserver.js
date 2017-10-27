/* eslint-disable */
const path = require('path');
const subscriptionPath = path.join(__dirname, 'tests', 'data');
const preferences = require('../preferences').mockDestinations;

require('mock-server').startServer({
  subscriptionLocations: [subscriptionPath, preferences]
});