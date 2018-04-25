import { module, test } from 'qunit';
import { patchSocket } from '../../../helpers/patch-socket';
import { Incidents } from 'respond/actions/api';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { setupTest } from 'ember-qunit';

module('Unit | Utility | Incidents API', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('it creates the proper query for the getAlerts API method', function(assert) {
    assert.expect(3);
    patchSocket((method, modelName, query) => {
      assert.equal(method, 'stream');
      assert.equal(modelName, 'related-alerts-search');
      assert.deepEqual(query, {
        criteria: {
          data: '1.1.1.1',
          entityType: 'IP',
          timeRange: {
            lower: 0
          }
        },
        limit: 1000,
        chunkSize: 100
      });
    });
    Incidents.getRelatedAlerts('IP', '1.1.1.1', 'ALL_TIME', {});
  });

  test('it creates the proper query for the delete API method', function(assert) {
    assert.expect(3);
    patchSocket((method, modelName, query) => {
      assert.equal(method, 'deleteRecord');
      assert.equal(modelName, 'incidents');
      assert.deepEqual(query, {
        filter: [{
          'field': '_id',
          'value': 'INC-123'
        }],
        sort: [ undefined ],
        stream: {
          batch: 100,
          limit: 1000
        }
      });
    });
    Incidents.delete('INC-123');
  });

  test('it creates the proper request payload for the updateIncident API method', function(assert) {
    assert.expect(3);
    patchSocket((method, modelName, query) => {
      assert.equal(method, 'updateRecord');
      assert.equal(modelName, 'incidents');
      assert.deepEqual(query, {
        entityIds: ['INC-123', 'INC-321'],
        updates: { priority: 'MEDIUM' }
      });
    });
    Incidents.updateIncident(['INC-123', 'INC-321'], 'priority', 'MEDIUM');
  });

  test('it creates the proper request payload for the getIncidentSettings API method', function(assert) {
    assert.expect(3);
    patchSocket((method, modelName, query) => {
      assert.equal(method, 'findAll');
      assert.equal(modelName, 'incidents-settings');
      assert.deepEqual(query, {});
    });
    Incidents.getIncidentsSettings();
  });
});