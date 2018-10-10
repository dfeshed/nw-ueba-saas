/* eslint-disable */
const allMocks = require('./index').mockDestinations;
const customData = require('./index').dataPath;

require('mock-server').startServer({
  subscriptionLocations: allMocks
}, null, { urlPattern: '/presidio/*', customData });
