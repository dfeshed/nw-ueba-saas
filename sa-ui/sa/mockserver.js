/* eslint-env node */

const respondMocks = require('../respond').mockDestinations;
const configureMocks = require('../configure').mockDestinations;
const contextMocks = require('../context').mockDestinations;
const investigateMocks = require('../investigate').mockDestinations;
const preferencesMocks = require('../preferences').mockDestinations;
const adminEngineMocks = require('../admin').mockDestinations;
const licenseMocks = require('../license').mockDestinations;
const customData = require('../investigate-users').dataPath;

const path = require('path');
const administrationMocks = path.join(__dirname, 'tests', 'data', 'subscriptions');

require('mock-server').startServer({
  subscriptionLocations: [
    ...investigateMocks,
    respondMocks,
    configureMocks,
    contextMocks,
    preferencesMocks,
    ...adminEngineMocks,
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
