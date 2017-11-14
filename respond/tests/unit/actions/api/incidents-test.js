import { module, test } from 'qunit';
import { patchSocket } from '../../../helpers/patch-socket';
import { Incidents } from 'respond/actions/api';

module('Unit | Utility | Incidents API');

test('it creates the proper query for the getRelatedIndicators API method', function(assert) {
  assert.expect(3);
  patchSocket((method, modelName, query) => {
    assert.equal(method, 'stream');
    assert.equal(modelName, 'related-alerts-search');
    assert.deepEqual(query, {
      limit: 1000,
      chunkSize: 100,
      data: '1.1.1.1',
      entityType: 'IP',
      timeRange: {
        lower: 0
      }
    });
  });
  Incidents.getRelatedAlerts('IP', '1.1.1.1', 'ALL_TIME', {});
});