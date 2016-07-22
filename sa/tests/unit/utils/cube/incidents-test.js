import Ember from 'ember';
import { module, test } from 'qunit';
import IncidentsCube from 'sa/utils/cube/incidents';

const { run } = Ember;

module('Unit | Utility | crossfilter/incidents');

// Replace this with your real tests.
test('it exists and supports incident fields', function(assert) {
  let obj;
  run(function() {
    obj = IncidentsCube.create({
      channel: null   // to disable websocket usage for this unit test
    });
  });
  assert.ok(obj);

  // Add some data.
  obj.get('records').pushObjects([
    {
      id: 1,
      prioritySort: 0,
      riskScore: 0,
      statusSort: 1,
      assignee: { name: 'Ian RSA', login: 'ian' }
    },
    {
      id: 2,
      prioritySort: 3,
      riskScore: 100,
      statusSort: 0
    },
    {
      id: 3,
      prioritySort: 0,
      riskScore: 20,
      statusSort: 1,
      assignee: { name: 'Tony RSA', login: 'tony' }
    }
  ]);

  // Try sorting by priority field, which is an alias for the prioritySort property.
  obj.sort('priority', false);    // sort ascending
  assert.equal(obj.get('results')[2].id, 2, 'Unexpected results sorting by priority.');

  // Try sorting by status field, which is an alias for the statusSort property.
  obj.sort('status', false);    // sort ascending
  assert.equal(obj.get('results')[0].id, 2, 'Unexpected results sorting by status.');

  // Try sorting by assignee field, which is an alias for the assignee.name property.
  obj.sort('assignee', false);    // sort ascending
  assert.equal(obj.get('results')[2].id, 3, 'Unexpected results sorting by assignee.');

  // Try sorting by priorityRiskScore field, which is a combination of prioritySort & riskScore.
  obj.sort('riskScore', true);    // sort descending
  assert.equal(obj.get('results')[0].id, 2, 'Unexpected results at index 0 sorting by priorityRiskScore.');
  assert.equal(obj.get('results')[1].id, 3, 'Unexpected results at index 1 sorting by priorityRiskScore.');
});
