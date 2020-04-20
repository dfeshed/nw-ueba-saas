/* eslint-disable */
const path = require('path');
const subscriptionPath = path.join(__dirname, 'tests', 'data');

require('mock-server').startServer({
  subscriptionLocations: [subscriptionPath]
});
