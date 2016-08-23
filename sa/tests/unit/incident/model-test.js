import { moduleForModel, test } from 'ember-qunit';

moduleForModel('incident', 'Unit | Model | incident', {
  // Specify the other units that are required for this test.
  needs: []
});

test('it exists', function(assert) {
  let model = this.subject();
  // let store = this.store();
  assert.ok(!!model);
});

test('check model values', function(assert) {
  let myModel = {
    name: 'Incident 1',
    alertCount: 10,
    eventCount: 20,
    averageAlertRiskScore: 80,
    riskScore: 79,
    createdBy: 'Event Stream analysis',
    prioritySort: 1,
    summary: 'Incident summary',
    statusSort: 1,
    assignee: 'ian@rsa.com',
    lastUpdated: '1344555555',
    lastUpdatedByUser: 'tony@rsa.com',
    alerts: { 'id': 'alert-1' },
    created: '14444221',
    sources: 'ESA',
    categories: 'Test',
    notes: []
  };

  let model = this.subject(myModel);

  assert.equal(model.get('name'), 'Incident 1', 'Invalid incident name');
  assert.equal(model.get('averageAlertRiskScore'), 80, 'Invalid incident averageAlertRiskScore');
  assert.equal(model.get('lastUpdatedByUser'), 'tony@rsa.com', 'Invalid incident lastUpdatedByUser');
  assert.equal(model.get('alertCount'), 10, 'Invalid incident alert count');
  assert.equal(model.get('eventCount'), 20, 'Invalid incident event count');
  assert.equal(model.get('riskScore'), 79, 'Invalid incident riskScore');
  assert.equal(model.get('createdBy'), 'Event Stream analysis', 'Invalid incident createdBy');
  assert.equal(model.get('prioritySort'), 1, 'Invalid incident prioritySort');
  assert.equal(model.get('summary'), 'Incident summary', 'Invalid incident summary');
  assert.equal(model.get('statusSort'), 1, 'Invalid incident statusSort');
  assert.equal(model.get('assignee'), 'ian@rsa.com', 'Invalid incident assignee');
  assert.equal(model.get('lastUpdated'), '1344555555', 'Invalid incident lastUpdated');
  assert.equal(model.get('alerts').id, 'alert-1', 'Invalid incident alerts');
  assert.equal(model.get('created'), '14444221', 'Invalid incident created');
  assert.equal(model.get('sources'), 'ESA', 'Invalid incident sources');
  assert.equal(model.get('categories'), 'Test', 'Invalid incident categories');
  assert.equal(model.get('notes').length, 0, 'Invalid incident notes');
});
