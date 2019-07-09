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
            res.status(500).end();
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
        let type = 3;
        switch (type) {
          case 1:
            // jackson databind error ( syntax error in json )
            res.status(400).send('FOO of alert-rule at index BAR is invalid');
            break;
          case 2:
            // BAD REQUEST - validation failure
            res.status(400).json([
              {
                name: 'alert-rule-1',
                errors: ['name is empty', 'another validation error here']
              },
              {
                name: 'alert-rule-2',
                errors: ['deep validation - too many group by']
              }
            ]);
            break;
          case 3:
            // WORKS !
            res.status(200).end();
            break;
        }
      }
    }
  ]
});
