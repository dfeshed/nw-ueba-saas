import { module, test } from 'qunit';
import { patchSocket } from '../../../helpers/patch-socket';
import { alerts } from 'respond/actions/api';

module('Unit | Utility | Alerts API');

test('it creates the proper query for the getAlerts API method', function(assert) {
  assert.expect(3);
  patchSocket((method, modelName, query) => {
    assert.equal(method, 'stream');
    assert.equal(modelName, 'alerts');
    assert.deepEqual(query, {
      filter: [{
        field: 'alert.type',
        values: ['Correlation', 'File Share']
      }],
      sort: [{ descending: true, field: 'receivedTime' }],
      stream: {
        batch: 100,
        limit: 1000
      }
    });
  });
  alerts.getAlerts({ 'alert.type': ['Correlation', 'File Share'] }, { isSortDescending: true, sortField: 'receivedTime' }, {});
});


test('it creates the proper query for the getAlertsCount API method', function(assert) {
  assert.expect(3);
  patchSocket((method, modelName, query) => {
    assert.equal(method, 'queryRecord');
    assert.equal(modelName, 'alerts-count');
    assert.deepEqual(query, {
      filter: [{
        field: 'alert.type',
        values: ['Correlation', 'File Share']
      }],
      sort: [{ descending: true, field: 'receivedTime' }],
      stream: {
        batch: 100,
        limit: 1000
      }
    });
  });
  alerts.getAlertsCount({ 'alert.type': ['Correlation', 'File Share'] }, { isSortDescending: true, sortField: 'receivedTime' });
});

test('it creates the proper query for the delete API method', function(assert) {
  assert.expect(3);
  patchSocket((method, modelName, query) => {
    assert.equal(method, 'deleteRecord');
    assert.equal(modelName, 'alerts');
    assert.deepEqual(query, {
      filter: [{
        'field': '_id',
        'value': 'ABC123EFG456'
      }],
      sort: [ undefined ],
      stream: {
        batch: 100,
        limit: 1000
      }
    });
  });
  alerts.delete('ABC123EFG456');
});

test('it creates the propery payload for the getOriginalAlert API method', function(assert) {
  assert.expect(3);
  patchSocket((method, modelName, query) => {
    assert.equal(method, 'queryRecord');
    assert.equal(modelName, 'original-alert');
    assert.deepEqual(query, {
      filter: [{
        'field': '_id',
        'value': 'ABC123EFG456'
      }],
      sort: [ undefined ],
      stream: {
        batch: 100,
        limit: 1000
      }
    });
  });
  alerts.getOriginalAlert('ABC123EFG456');
});