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
        let type = 3;
        switch (type) {
          case 1:
            // INTERNAL SERVER ERROR
            res.status(500).send();
            break;
          case 2:
            // BAD REQUEST
            res.status(200).json({
              missingIds: ['missing-1', 'missing-2'],
              advancedFilterEnabledIds: ['advanced-1', 'advanced-2', 'advanced-3']
            });
            break;
          case 3:
            // WORKS !
            res.download('tests/data/routes/export/dummy-success.zip', 'foobar.zip');
            break;
        }
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
