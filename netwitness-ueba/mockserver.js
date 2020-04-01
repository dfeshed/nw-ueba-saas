/* eslint-env node */

const path = require('path');

const licenseMocks = require('../license').mockDestinations;
const customData = path.join(__dirname, 'tests', 'data', 'presidio');

const administrationMocks = path.join(__dirname, 'tests', 'data', 'subscriptions');

require('mock-server').startServer({
  subscriptionLocations: [
    administrationMocks,
    licenseMocks
  ],
  routes: [
    {
      path: '/eula/rsa',
      method: 'get',
      response: (req, res) => {
        res.status(200).send('End user license agreement details.');
      }
    }
  ]
}, null, { urlPattern: '/api/*', customData });
