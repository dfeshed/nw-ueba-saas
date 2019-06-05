/* eslint-disable */
const mockDir = require('./index').mockDestinations;
const licenseMockDirectory = require('../license').mockDestinations;

require('mock-server').startServer({
  subscriptionLocations: [ mockDir, licenseMockDirectory ],
  routes: [
    {
      path: '/api/respond/rules/export',
      method: 'post',
      response: (req, res) => {
        res.download('tests/data/routes/export/dummy-success.zip', 'foobar.zip');
      }
    },
    {
      path: '/api/respond/rules/import',
      method: 'post',
      response: (req, res) => {
        res.status(200).json(['rule-id1', 'rule-id2']);
      }
    }
  ]
});
