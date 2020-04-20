/* eslint-disable */
const path = require('path');
const subscriptionPath = path.join(__dirname, 'tests', 'data');

const contextMockDirectory = require('../context').mockDestinations;
const investigateMocks = require('../investigate').mockDestinations;

const alertsResponse = require('./tests/data/ueba/alerts');
const eventsResponse = require('./tests/data/ueba/events');
const detailsResponse = require('./tests/data/ueba/details');
const historicalDataResponse = require('./tests/data/ueba/historical-data');

require('mock-server').startServer({
  subscriptionLocations: [
    subscriptionPath,
    ...investigateMocks,
    contextMockDirectory
  ],
  routes: [
    {
      path: '/presidio/api/alerts',
      method: 'get',
      response: (req, res) => {
        // USE THIS TO SIMULATE THE RACE ALERTS/EVENTS RACE CONDITION
        res.json(alertsResponse);
        /*res.setTimeout(1000, () => {
          res.json(alertsResponse);
        });*/
      }
    },
    {
      path: '/presidio/api/evidences/\*/events',
      method: 'get',
      response: (req, res) => {
        res.json(eventsResponse);
        // USE THIS TO SIMULATE THE RACE ALERTS/EVENTS RACE CONDITION
        /*res.setTimeout(2000, () => {
          res.json(eventsResponse);
        });*/
      }
    },
    {
      path: '/presidio/api/entity/\*/details',
      method: 'get',
      response: (req, res) => {
        res.json(detailsResponse);
      }
    },
    {
      path: '/presidio/api/evidences/\*/historical-data',
      method: 'get',
      response: (req, res) => {
        res.json(historicalDataResponse);
      }
    },
  ]
});