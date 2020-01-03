/* eslint-env node */

const licenseMocks = require('../license').mockDestinations;
const customData = require('../investigate-users').dataPath;

const path = require('path');
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
}, null, { urlPattern: '/presidio/*', customData });
