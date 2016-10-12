import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import Ember from 'ember';
import selectors from 'sa/tests/selectors';

const { Object: EmberObject } = Ember;

moduleForComponent('rsa-incident-tile', 'Integration | Component | rsa respond/landing page/incident tile', {
  integration: true
});

test('The tile component is rendered properly.', function(assert) {

  let testInc = EmberObject.create({
    'id': 'INC-490',
    'name': 'Suspected command and control communication with www.media.gwu.edu',
    'riskScore': 96,
    'prioritySort': 0,
    'statusSort': 1,
    'created': 1452485774539,
    'assignee': {
      'id': '1'
    },
    'createdBy': 'Suspected Command & Control Communication By Domain',
    'alertCount': 1,
    'eventCount': 5,
    'categories': [],'sources': ['Event Stream Analysis'],'lastUpdated': 1452485774539,
    'ruleId': '5681b379e4b0947bc54e6c9d',
    'summary': 'SA detected communications with www.media.gwu.edu that may be malware command and control.\n\n1. Evaluate if the domain is legitimate (online radio, news feed, partner, automated testing, etc.).\n2. Review domain registration for suspect information (Registrant country, registrar, no registration data found, etc).\n3. If the domain is suspect, go to the Investigations module to locate other activity to/from it.',
    groupBySourceIp: ['1.1.1.1'],
    groupByDestinationIp: ['2.2.2.2']
  });

  let users = [EmberObject.create({ id: '1', firstName: 'User 1', lastName: 'LastName 1', friendlyName: 'user1', email: 'user1@rsa.com' }),
    EmberObject.create({ id: '2', firstName: 'User 2', lastName: 'LastName 2', friendlyName: 'user2', email: 'user2@rsa.com' }),
    EmberObject.create({ id: '3', firstName: 'User 3', lastName: 'LastName 3', friendlyName: 'user3', email: 'user3@rsa.com' }) ];

  this.set('testInc', testInc);
  this.set('users', users);

  this.render(hbs`{{rsa-respond/landing-page/incident-tile incident=testInc users=users}}`);

  assert.equal(this.$('.rsa-update-indicator__dot').length, 1, 'The rsa-update-indicator__dot element was not found.');
  assert.equal(this.$('.rsa-incident-tile').length, 1, 'Testing to see if a rsa-incident-tile element exists.');
  assert.equal(this.$('.rsa-incident-tile-section').length, 4, 'Testing to see if a rsa-incident-tile-section element exists.');
  assert.equal(this.$('.rsa-incident-tile-id').length, 1, 'Testing to see if a rsa-incident-id elements exist.');
  assert.ok(this.$('.rsa-incident-tile-header').length, 'Incident tile header not found in DOM');
  assert.ok((this.$('.rsa-incident-tile-score').text().indexOf(testInc.riskScore) >= 0), 'Unexpected incident risk score');
  assert.equal(this.$('.rsa-incident-tile-id').text().trim(), testInc.id, 'Unexpected incident id in the tile');
  assert.ok(this.$('.rsa-incident-tile-name').length, 'Incident tile name not found in DOM');
  assert.ok(this.$('.rsa-incident-tile-name').text().indexOf('Suspected command and control communication with www.media.gwu.edu'.substr(0, 60)) >= 0, 'Unexpected name value');
  assert.ok(this.$('.rsa-incident-tile-created-date').length, 'Incident tile created date not found in DOM');
  assert.ok(this.$('.rsa-incident-tile-status-selector').length, 'Incident tile status not found in DOM');
  assert.ok(this.$('.rsa-incident-tile-status').text().indexOf('Assigned') >= 0, 'Unexpected assigned value');
  assert.ok(this.$('.rsa-incident-tile-priority-selector').length, 'Incident tile priority not found in DOM');
  assert.ok((this.$('.rsa-incident-tile-priority-selector').text().indexOf('Low') >= 0), 'Unexpected incident severity');
  assert.ok(this.$('.rsa-incident-tile-assignee-selector').length, 'Incident tile assignee not found in DOM');
  assert.ok((this.$('.rsa-incident-tile-assignee-selector').text().indexOf('User 1') >= 0), 'Unexpected Assignee value');
  assert.equal(this.$('.rsa-incident-tile-alert-count').length, 1, 'Incident tile alert count not found in DOM');
  assert.equal(this.$('.rsa-incident-tile-alert-count').text().trim(), '1', 'Unexpected alert count value');
  assert.equal(this.$('.rsa-incident-tile-event-count').length, 1, 'Incident tile Event count not found in DOM');
  assert.equal(this.$('.rsa-incident-tile-event-count').text().trim(), '5', 'Unexpected Event count value');
  assert.equal(this.$('.rsa-incident-tile-sources').length, 1, 'Incident tile sources not found in DOM');
  assert.equal(this.$('.rsa-incident-tile-sources').text().trim(), ['ESA'], 'Unexpected alert count value');
  assert.equal(this.$('.rsa-incident-tile-score .label').text().trim(), 'Risk Score', 'Incident tile does include risk score label in DOM');
  assert.equal(this.$('.rsa-content-ip-connections').length, 1, 'Incident tile renders source-dest IP component');

  this.render(hbs`{{rsa-respond/landing-page/incident-tile incident=testInc users=users size='small'}}`);
  assert.equal(this.$('.rsa-update-indicator__dot').length, 1, 'The rsa-update-indicator__dot element was not found.');
  assert.equal(this.$('.rsa-incident-tile-status-selector').length, 0, 'Small incident tile does not include status selector in DOM');
  assert.equal(this.$('.rsa-incident-tile-priority-selector').length, 0, 'Small incident tile does not include priority selector in DOM');
  assert.equal(this.$('.rsa-incident-tile-assignee-selector').length, 0, 'Small incident tile does not include assignee selector in DOM');
  assert.equal(this.$('.rsa-incident-tile-score .score').text().trim(), testInc.riskScore, 'Small incident tile does include risk score label in DOM');
  assert.equal(this.$('.rsa-incident-tile-id').text().trim(), testInc.id, 'Small incident tile contains id in the tile');
  assert.equal(this.$('.rsa-incident-tile-name').length, 1, 'Small incident tile name not found in DOM');
  assert.equal(this.$('.rsa-incident-tile-score .label').text().trim().length, 0, 'Small incident tile does not include risk score label in DOM');
  assert.equal(this.$('.rsa-incident-tile-alert-count').length, 1, 'Small Incident tile alert count is present in DOM');
  assert.equal(this.$('.rsa-incident-tile-alert-count').text().trim(), '1', 'Small Incident tile alert count contains the proper number of alerts.');
  assert.equal(this.$('.rsa-incident-tile-event-count').length, 0, 'Small incident tile does not render Event count');
  assert.equal(this.$('.rsa-incident-tile-sources').length, 0, 'Small incident tile does not render sources');
  assert.equal(this.$('.rsa-incident-tile-priority').length, 1, 'Small incident tile renders priority label');
  assert.equal(this.$('.rsa-incident-tile-priority').text().trim(), 'Low', 'Small incident tile displays expected priority');
});

test('The tile component renders the proper contextual timestamp.', function(assert) {
  /*
   * The dates used below should match the following:
   * | STATUS  | DATE                | MILLISECONDS  |
   * | New     | 05/01/2016 10:25:15 | 1462123515000 |
   * | Updated | 05/20/2016 11:23:15 | 1463768595000 |
   */
  let mockIncident = EmberObject.create({
    'id': 'INC-490',
    'name': 'Suspected command and control communication with www.media.gwu.edu',
    'riskScore': 96,
    'prioritySort': 0,
    'statusSort': 0,
    'created': 1462123515000,
    'assignee': {
      'id': '1'
    },
    'createdBy': 'Suspected Command & Control Communication By Domain',
    'alertCount': 1,
    'categories': [],
    'sources': ['Event Stream Analysis'],
    'ruleId': '5681b379e4b0947bc54e6c9d',
    'summary': 'SA detected communications with www.media.gwu.edu that may be malware command and control.\n\n1. Evaluate if the domain is legitimate (online radio, news feed, partner, automated testing, etc.).\n2. Review domain registration for suspect information (Registrant country, registrar, no registration data found, etc).\n3. If the domain is suspect, go to the Investigations module to locate other activity to/from it.'
  });

  this.set('mockIncident', mockIncident);
  this.render(hbs`{{rsa-respond/landing-page/incident-tile incident=mockIncident}}`);
  assert.equal(this.$('.rsa-incident-tile-created-date').text().indexOf('created') !== -1, true, 'Testing whether or not a created date is shown.');

  this.set('mockIncident.lastUpdated', 1463768595000);
  this.set('mockIncident.statusSort', 2);
  assert.equal(this.$('.rsa-incident-tile-created-date').text().indexOf('updated') !== -1, true, 'Testing whether or not an updated date is shown.');

  this.set('mockIncident.statusSort', 4);
  assert.equal(this.$('.rsa-incident-tile-created-date').text().indexOf('updated') !== -1, true, 'Testing whether or not an updated date is shown for other statuses.');
  assert.equal(this.$('.rsa-incident-tile-status-selector option[selected="selected"]').text() !== 'Updated', true, 'Testing to ensure that the status is something other than Updated.');
});

test('Edit button stays visible after click and the mouse leaves the component', function(assert) {
  let incident = EmberObject.create({
    riskScore: 1,
    id: 'INC-491',
    createdBy: 'User X',
    created: '2015-10-10',
    statusSort: 0,
    prioritySort: 0,
    alertCount: 10,
    sources: ['Event Stream Analysis'],
    assignee: {
      id: '1'
    }
  });

  let users = [EmberObject.create({ id: '1', firstName: 'User 1', lastName: 'LastName 1', email: 'user1@rsa.com' }),
    EmberObject.create({ id: '2', firstName: 'User 2', lastName: 'LastName 2', email: 'user2@rsa.com' }),
    EmberObject.create({ id: '3', firstName: 'User 3', lastName: 'LastName 3', email: 'user3@rsa.com' }) ];

  this.set('incident', incident);
  this.set('users', users);

  this.render(hbs`
    {{rsa-respond/landing-page/incident-tile incident=incident}}
  `);

  let container = this.$('.rsa-incident-tile');
  container.trigger('mouseenter');
  this.$('.rsa-edit-tool').trigger('click');
  container.trigger('mouseleave');

  assert.equal(this.$('.rsa-edit-tool.hide').length, 0, 'Edit button is present after click it');

});

test('Edit mode is disabled if starting to edit another tile', function(assert) {
  let incident = EmberObject.create({
    riskScore: 1,
    id: 'INC-491',
    createdBy: 'User X',
    created: '2015-10-10',
    statusSort: 0,
    prioritySort: 0,
    alertCount: 10,
    sources: ['Event Stream Analysis'],
    assignee: {
      id: '1'
    }
  });

  let users = [EmberObject.create({ id: '1', firstName: 'User 1', lastName: 'LastName 1', email: 'user1@rsa.com' }),
    EmberObject.create({ id: '2', firstName: 'User 2', lastName: 'LastName 2', email: 'user2@rsa.com' }),
    EmberObject.create({ id: '3', firstName: 'User 3', lastName: 'LastName 3', email: 'user3@rsa.com' }) ];

  this.set('incident', incident);
  this.set('users', users);

  this.render(hbs`
    {{rsa-respond/landing-page/incident-tile id='tile1' incident=incident users=users}}{{rsa-respond/landing-page/incident-tile id='tile2' incident=incident users=users}}
  `);

  let tile1 = this.$('#tile1');
  let tile2 = this.$('#tile2');

  tile1.trigger('mouseenter');
  tile1.find('.rsa-edit-tool').trigger('click');
  tile1.trigger('mouseleave');

  tile2.trigger('mouseenter');
  tile2.find('.rsa-edit-tool').trigger('click');

  tile1.trigger('focusout');

  assert.equal(tile1.find('.rsa-edit-tool').css('visibility'), 'hidden', 'Tile 1 hides the edit button');
  assert.equal(tile2.find('.rsa-edit-tool').css('visibility'), 'visible', 'Tile 2 shows the edit button');

});

test('Clicking off a card in edit mode exits edit mode without saving any field changes', function(assert) {
  let preStatusValue = 0;
  let newStatusValue = 1;

  let incident = EmberObject.create({
    riskScore: 1,
    id: 'INC-491',
    createdBy: 'User X',
    created: '2015-10-10',
    statusSort: preStatusValue,
    prioritySort: 0,
    alertCount: 10,
    sources: ['Event Stream Analysis'],
    assignee: {
      id: '1'
    }
  });

  let users = [EmberObject.create({ id: '1', firstName: 'User 1', lastName: 'LastName 1', email: 'user1@rsa.com' })];

  this.set('incident', incident);
  this.set('users', users);

  this.render(hbs`
    {{rsa-respond/landing-page/incident-tile incident=incident users=users}} <div class='.other-component'/>
  `);

  let container = this.$('.rsa-incident-tile');
  container.trigger('mouseenter');

  this.$('.rsa-edit-tool').trigger('click');

  let tileStatusVal = container.find('.rsa-incident-tile-status-selector').val();
  assert.equal(incident.statusSort, tileStatusVal, 'Tile displays the current Incident status.');

  container.find('.rsa-incident-tile-status-selector').val(newStatusValue);
  assert.equal(incident.statusSort, preStatusValue, 'After updating the Select, the incident status has its prev value before saving the model');
  this.$('.other-component').trigger('click');

  assert.equal(incident.statusSort, preStatusValue, 'After exiting tile, new Incident status must retain the old value');
  assert.notEqual(incident.statusSort, newStatusValue, 'After exiting tile, new Incident status should not be saved');

});

test('Assignee field contains at least one option', function(assert) {
  let incident = EmberObject.create({
    riskScore: 1,
    id: 'INC-491',
    createdBy: 'User X',
    created: '2015-10-10',
    statusSort: 0,
    prioritySort: 0,
    alertCount: 10,
    sources: ['Event Stream Analysis'],
    assignee: {
      id: 1
    }
  });

  let users = [EmberObject.create({ id: '1', firstName: 'User 1', lastName: 'LastName 1', email: 'user1@rsa.com' })];

  this.set('incident', incident);
  this.set('users', users);

  this.render(hbs`
    {{rsa-respond/landing-page/incident-tile incident=incident users=users}}
  `);

  let container = this.$('.rsa-incident-tile');
  container.trigger('mouseenter');

  this.$('.rsa-edit-tool').trigger('click');

  let selectorOptionCount = container.find('.rsa-incident-tile-assignee-selector option').length;
  assert.notEqual(0, selectorOptionCount, 'Tile displays the current Incident status.');

});

test('Incident status changed after press save', function(assert) {
  let preStatusValue = 0;
  let newStatusValue = '1';
  let incident = EmberObject.create({
    riskScore: 1,
    id: 'INC-491',
    createdBy: 'User X',
    created: '2015-10-10',
    statusSort: preStatusValue,
    prioritySort: 0,
    alertCount: 10,
    sources: ['Event Stream Analysis'],
    assignee: {
      id: '1'
    }
  });
  let users = [EmberObject.create({ id: '1', firstName: 'User 1', lastName: 'LastName 1', email: 'user1@rsa.com' }),
    EmberObject.create({ id: '2', firstName: 'User 2', lastName: 'LastName 2', email: 'user2@rsa.com' }),
    EmberObject.create({ id: '3', firstName: 'User 3', lastName: 'LastName 3', email: 'user3@rsa.com' }) ];

  // this.set({ incident: incident, users: users });
  this.set('incident', incident);
  this.set('users', users);

  this.render(hbs`
    {{rsa-respond/landing-page/incident-tile incident=incident users=users}}
  `);

  let container = this.$('.rsa-incident-tile');
  container.trigger('mouseenter');

  this.$('.rsa-edit-tool').trigger('click');

  let tileStatusVal = container.find('.rsa-incident-tile-status-selector select').val();
  assert.equal(incident.statusSort, tileStatusVal, 'Tile displays the current Incident status.');

  container.find('.rsa-incident-tile-status-selector .prompt').click();
  container.find('.rsa-incident-tile-status-selector select').val(newStatusValue).trigger('change');
  assert.equal(incident.statusSort, preStatusValue, 'After updating the Select, the incident status has its prev value before saving the model');

  container.find('.rsa-edit-tool').trigger('click');
  assert.equal(incident.status, 'ASSIGNED', 'After clicking Save, Incident status has changed to its new value');

});

test('Incident priority changed after press save', function(assert) {
  let prePriorityValue = 0;
  let newPriorityValue = 1;
  let incident = EmberObject.create({
    riskScore: 1,
    id: 'INC-491',
    createdBy: 'User X',
    created: '2015-10-10',
    statusSort: 0,
    prioritySort: prePriorityValue,
    alertCount: 10,
    sources: ['Event Stream Analysis'],
    assignee: {
      id: '1'
    }
  });
  let users = [EmberObject.create({ id: '1', firstName: 'User 1', lastName: 'LastName 1', email: 'user1@rsa.com' }),
    EmberObject.create({ id: '2', firstName: 'User 2', lastName: 'LastName 2', email: 'user2@rsa.com' }),
    EmberObject.create({ id: '3', firstName: 'User 3', lastName: 'LastName 3', email: 'user3@rsa.com' }) ];

  this.set('incident', incident);
  this.set('users', users);

  this.render(hbs`
    {{rsa-respond/landing-page/incident-tile incident=incident users=users}}
  `);

  let container = this.$('.rsa-incident-tile');
  container.trigger('mouseenter');

  this.$('.rsa-edit-tool').trigger('click'); // switching to edit mode

  let tilePriorityVal = container.find('.rsa-incident-tile-priority-selector select').val();
  assert.equal(incident.prioritySort, tilePriorityVal, 'Tile displays the current Incident priority.');

  container.find('.rsa-incident-tile-priority-selector .prompt').click();
  container.find('.rsa-incident-tile-priority-selector select').val(newPriorityValue).trigger('change');
  assert.equal(incident.prioritySort, prePriorityValue, 'After updating the Select, the incident model priority has its prev value before saving the model');

  container.find('.rsa-edit-tool').trigger('click');
  assert.equal(incident.priority, 'MEDIUM', 'After clicking Save, Incident priority has changed to its new value');

});

test('Incident Assignee changed after press save', function(assert) {
  let assigneeIdOne = 1;
  let assigneeIdTwo = 2;
  let incident = EmberObject.create({
    riskScore: 1,
    id: 'INC-491',
    createdBy: 'User X',
    created: '2015-10-10',
    statusSort: 0,
    prioritySort: 0,
    alertCount: 10,
    sources: ['Event Stream Analysis'],
    assignee: {
      id: assigneeIdOne
    }
  });
  let users = [EmberObject.create({ id: '1', firstName: 'User 1', lastName: 'LastName 1', email: 'user1@rsa.com' }),
    EmberObject.create({ id: '2', firstName: 'User 2', lastName: 'LastName 2', email: 'user2@rsa.com' }),
    EmberObject.create({ id: '3', firstName: 'User 3', lastName: 'LastName 3', email: 'user3@rsa.com' }) ];

  this.set('incident', incident);
  this.set('users', users);

  this.render(hbs`
    {{rsa-respond/landing-page/incident-tile incident=incident users=users}}
  `);

  let container = this.$('.rsa-incident-tile');
  container.trigger('mouseenter');

  this.$('.rsa-edit-tool').trigger('click'); // switching to edit mode

  let tileAssigneeVal = container.find('.rsa-incident-tile-assignee-selector select').val();
  assert.equal(incident.assignee.id, tileAssigneeVal, 'Tile displays the current Incident assignee.');

  container.find('.rsa-incident-tile-assignee-selector .prompt').click();
  container.find('.rsa-incident-tile-assignee-selector select').val(assigneeIdTwo).trigger('change');
  assert.equal(incident.assignee.id, assigneeIdOne, 'After updating the Select, the incident model assignee has its prev value before saving the model');

  container.find('.rsa-edit-tool').trigger('click');
  assert.equal(incident.assignee.id, assigneeIdTwo, 'After clicking Save, Incident assignee has changed to its new value');

});

test('The update indicator component is rendered properly when an asynchronous update is available', function(assert) {

  let testInc = EmberObject.create({
    'id': 'INC-490',
    'name': 'Suspected command and control communication with www.media.gwu.edu',
    'riskScore': 96,'prioritySort': 0,
    'statusSort': 1,
    'created': 1452485774539,
    'assignee': { 'id': '1' },
    'asyncUpdate': true
  });
  let users = [EmberObject.create({ id: '1', firstName: 'User 1', lastName: 'LastName 1', friendlyName: 'user1', email: 'user1@rsa.com' }) ];

  this.set('testInc', testInc);
  this.set('users', users);

  this.render(hbs`{{rsa-respond/landing-page/incident-tile incident=testInc users=users}}`);

  assert.equal(this.$('.rsa-update-indicator.is-icon-only').length, 1, 'Testing to see if the update indicator element exists with the is-icon-only class.');
  assert.equal(this.$('.rsa-update-indicator.is-icon-only.is-hidden').length, 0, 'Testing to see if the update indicator element exists with the is-icon-only and is-hidden classes.');
});

test('The update indicator component is rendered properly when an asynchronous update is not available', function(assert) {

  let testInc = EmberObject.create({
    'id': 'INC-490',
    'name': 'Suspected command and control communication with www.media.gwu.edu',
    'riskScore': 96,'prioritySort': 0,
    'statusSort': 1,
    'created': 1452485774539,
    'assignee': { 'id': '1' }
  });
  let users = [EmberObject.create({ id: '1', firstName: 'User 1', lastName: 'LastName 1', friendlyName: 'user1', email: 'user1@rsa.com' }) ];

  this.set('testInc', testInc);
  this.set('users', users);

  this.render(hbs`{{rsa-respond/landing-page/incident-tile incident=testInc users=users}}`);

  assert.equal(this.$('.rsa-update-indicator.is-icon-only').length, 1, 'Testing to see if the update indicator element exists with the is-icon-only class.');
  assert.equal(this.$('.rsa-update-indicator.is-icon-only.is-hidden').length, 1, 'Testing to see if the update indicator element exists with the is-icon-only and is-hidden classes.');
});

test('If the alert count is missing, then the default value is "-".', function(assert) {

  let testInc = EmberObject.create({
    'id': 'INC-490',
    'name': 'Suspected command and control communication with www.media.gwu.edu',
    'eventCount': 5
  });
  let users = [EmberObject.create({ id: '1', firstName: 'User 1', lastName: 'LastName 1', friendlyName: 'user1', email: 'user1@rsa.com' })];

  this.set('testInc', testInc);
  this.set('users', users);

  this.render(hbs`{{rsa-respond/landing-page/incident-tile incident=testInc users=users}}`);
  assert.equal(this.$('.rsa-incident-tile-alert-count').length, 1, 'The .rsa-incident-tile-alert-count element was not found in the DOM.');
  assert.equal(this.$('.rsa-incident-tile-alert-count').text().trim(), '-', 'The default value for missing alerts "-" was not found.');
});

test('If the event count is missing, then the default value is "-".', function(assert) {

  let testInc = EmberObject.create({
    'id': 'INC-490',
    'name': 'Suspected command and control communication with www.media.gwu.edu',
    'alertCount': 5
  });
  let users = [EmberObject.create({ id: '1', firstName: 'User 1', lastName: 'LastName 1', friendlyName: 'user1', email: 'user1@rsa.com' })];

  this.set('testInc', testInc);
  this.set('users', users);

  this.render(hbs`{{rsa-respond/landing-page/incident-tile incident=testInc users=users}}`);
  assert.equal(this.$('.rsa-incident-tile-event-count').length, 1, 'The .rsa-incident-tile-event-count element was not found in the DOM.');
  assert.equal(this.$('.rsa-incident-tile-event-count').text().trim(), '-', 'The default value for missing events "-" was not found.');
});

test('Incident priority order check (Critical -> Low)', function(assert) {

  let incident = EmberObject.create({
    riskScore: 1,
    id: 'INC-491',
    createdBy: 'User X',
    created: '2015-10-10',
    statusSort: 0,
    prioritySort: 0,
    alertCount: 10,
    sources: ['Event Stream Analysis'],
    assignee: {
      id: '1'
    }
  });
  let users = [EmberObject.create({ id: '1', firstName: 'User 1', lastName: 'LastName 1', email: 'user1@rsa.com' }),
    EmberObject.create({ id: '2', firstName: 'User 2', lastName: 'LastName 2', email: 'user2@rsa.com' }),
    EmberObject.create({ id: '3', firstName: 'User 3', lastName: 'LastName 3', email: 'user3@rsa.com' }) ];

  this.set('incident', incident);
  this.set('users', users);

  this.render(hbs`
    {{rsa-respond/landing-page/incident-tile incident=incident users=users}}
  `);

  let container = this.$(selectors.pages.respond.card.incTile.incidentTile);
  container.trigger('mouseenter');

  this.$(selectors.pages.respond.card.incTile.editButton).trigger('click');

  let priorityOptionList = container.find(this.$(selectors.pages.respond.card.incTile.prioritySelectOpt));

  assert.equal(priorityOptionList[0].text, 'Critical', 'First priority is Critical');
  assert.equal(priorityOptionList[1].text, 'High', 'Second priority is High');
  assert.equal(priorityOptionList[2].text, 'Medium', 'Third priority is Medium');
  assert.equal(priorityOptionList[3].text, 'Low', 'Fourth priority is Low');

});

test('Incident priority order check (Critical -> Low)', function(assert) {

  let incident = EmberObject.create({
    riskScore: 1,
    id: 'INC-491',
    createdBy: 'User X',
    created: '2015-10-10',
    statusSort: 0,
    prioritySort: 0,
    alertCount: 10,
    sources: ['Event Stream Analysis'],
    assignee: {
      id: '1'
    }
  });
  let users = [EmberObject.create({ id: '1', firstName: 'User 1', lastName: 'LastName 1', email: 'user1@rsa.com' }),
    EmberObject.create({ id: '2', firstName: 'User 2', lastName: 'LastName 2', email: 'user2@rsa.com' }),
    EmberObject.create({ id: '3', firstName: 'User 3', lastName: 'LastName 3', email: 'user3@rsa.com' }) ];

  this.set('incident', incident);
  this.set('users', users);

  this.render(hbs`
    {{rsa-respond/landing-page/incident-tile incident=incident users=users}}
  `);

  let container = this.$(selectors.pages.respond.card.incTile.incidentTile);
  container.trigger('mouseenter');

  this.$(selectors.pages.respond.card.incTile.editButton).trigger('click');

  let priorityOptionList = container.find(this.$(selectors.pages.respond.card.incTile.prioritySelectOpt));

  assert.equal(priorityOptionList[0].text, 'Critical', 'First priority is Critical');
  assert.equal(priorityOptionList[1].text, 'High', 'Second priority is High');
  assert.equal(priorityOptionList[2].text, 'Medium', 'Third priority is Medium');
  assert.equal(priorityOptionList[3].text, 'Low', 'Fourth priority is Low');

});


test('Incident Tile gets rendered in queue mode', function(assert) {
  let incident = EmberObject.create({
    riskScore: 1,
    id: 'INC-491',
    createdBy: 'User X',
    created: '2015-10-10',
    statusSort: 0,
    prioritySort: 0,
    alertCount: 10,
    sources: ['Event Stream Analysis'],
    assignee: {}
  });
  let users = [EmberObject.create({ id: '1', firstName: 'User 1', lastName: 'LastName 1', email: 'user1@rsa.com' }),
    EmberObject.create({ id: '2', firstName: 'User 2', lastName: 'LastName 2', email: 'user2@rsa.com' }),
    EmberObject.create({ id: '3', firstName: 'User 3', lastName: 'LastName 3', email: 'user3@rsa.com' }) ];

  this.set('incident', incident);
  this.set('users', users);

  this.render(hbs`
    {{rsa-respond/landing-page/incident-tile incident=incident users=users size='small' mode='queue'}}
  `);

  assert.equal(this.$('.rsa-incident-tile-status').length, 1, 'Testing to see if the status is rendered in queue mode');
  assert.equal(this.$('.rsa-incident-tile-priority').length, 1, 'Testing to see if the priority is rendered in queue mode');
  assert.equal(this.$('.rsa-incident-tile-assignee').length, 1, 'Testing to see if the assignee is rendered in queue mode');

  this.render(hbs`
    {{rsa-respond/landing-page/incident-tile incident=incident users=users}}
  `);

  assert.equal(this.$('.rsa-incident-tile-sources').length, 1, 'Testing to see if the source is rendered in non queue mode');
  assert.equal(this.$('.rsa-incident-tile-event-count').length, 1, 'Testing to see if the event count is rendered in tile mode');
  assert.equal(this.$('.rsa-incident-tile-alert-count').length, 1, 'Testing to see if the alert count is rendered in tile mode');

});
