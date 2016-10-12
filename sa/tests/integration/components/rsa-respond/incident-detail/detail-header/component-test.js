import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import Ember from 'ember';
import { incStatus } from 'sa/incident/constants';
import selectors from 'sa/tests/selectors';

const { Object: EmberObject } = Ember;

moduleForComponent('rsa-incident-detail-header', 'Integration | Component | rsa respond/incident detail/detail header', {
  integration: true
});

test('The incident detail header component is rendered properly.', function(assert) {

  let incident = EmberObject.create({
    riskScore: 1,
    id: 'INC-491',
    name: 'Suspected command and control communication with www.mozilla.com',
    createdBy: 'User X',
    created: '2015-10-10',
    lastUpdated: '2015-10-10',
    statusSort: 0,
    prioritySort: 0,
    alertCount: 10,
    eventCount: 2,
    sources: ['Event Stream Analysis'],
    assignee: {
      id: '1'
    },
    groupBySourceIp: ['1.1.1.1'],
    groupByDestinationIp: ['2.2.2.2']
  });
  let users = [EmberObject.create({ id: 1, firstName: 'User 1', lastName: 'LastName 1', email: 'user1@rsa.com' }),
      EmberObject.create({ id: 2, firstName: 'User 2', lastName: 'LastName 2', email: 'user2@rsa.com' }),
      EmberObject.create({ id: 3, firstName: 'User 3', lastName: 'LastName 3', email: 'user3@rsa.com' }) ];

  this.setProperties({
    incident,
    users
  });

  this.render(hbs`{{rsa-respond/incident-detail/detail-header incident=incident users=users}}`);

  assert.equal(this.$('.rsa-incident-detail-header').length, 1, 'Testing rsa-incident-detail-header element exists');
  assert.equal(this.$('.rsa-incident-detail-header__id').length, 1, 'Testing rsa-incident-detail-header__id element exists');
  assert.equal(this.$('.rsa-incident-detail-header__name').length, 1, 'Testing rsa-incident-detail-header__name element exists');
  assert.equal(this.$('.rsa-incident-detail-header__priority').length, 1, 'Testing rsa-incident-detail-header__priority element exists');
  assert.equal(this.$('.rsa-incident-detail-header__alerts').length, 1, 'Testing rsa-incident-detail-header__alerts element exists');
  assert.equal(this.$('.rsa-incident-detail-header__events').length, 1, 'Testing rsa-incident-detail-header__events element exists');
  assert.equal(this.$('.rsa-incident-detail-header__sources').length, 1, 'Testing rsa-incident-detail-header__sources element exists');
  assert.equal(this.$('.rsa-incident-detail-header__assignee').length, 1, 'Testing rsa-incident-detail-header__assignee element exists');
  assert.equal(this.$('.rsa-incident-detail-header__status').length, 1, 'Testing rsa-incident-detail-header__status element exists');

  assert.equal(this.$('.rsa-incident-detail-header__source-ip').length, 1, 'Testing rsa-incident-detail-header__source-ip element exists');
  assert.equal(this.$('.rsa-incident-detail-header__destination-ip').length, 1, 'Testing rsa-incident-detail-header__destination-ip element exists');
  assert.equal(this.$('.rsa-incident-detail-header__created').length, 1, 'Testing rsa-incident-detail-header__created element exists');
  assert.equal(this.$('.rsa-incident-detail-header__last-updated').length, 1, 'Testing rsa-incident-detail-header__last-updated element exists');

  assert.equal(this.$('.rsa-incident-detail-header__id').text().indexOf('INC-491') >= 0 , true, 'Testing correct incident ID is rendered');
  assert.equal(this.$('.rsa-incident-detail-header__name input').val(), 'Suspected command and control communication with www.mozilla.com', 'Testing correct incident Name is rendered');
  assert.equal(this.$('.rsa-incident-detail-header__priority .prompt').text().trim(), 'Low', 'Testing correct incident Priority is rendered');
  assert.equal(this.$('.rsa-incident-detail-header__alerts label').text(), 10, 'Testing correct incident Alerts is rendered');
  assert.equal(this.$('.rsa-incident-detail-header__events label').text(), 2, 'Testing correct incident Events is rendered');
  assert.equal(this.$('.rsa-incident-detail-header__sources').length, 1, 'Testing correct number of incident Sources is rendered');
  assert.equal(this.$('.rsa-incident-detail-header__sources .rsa-content-label').text().trim(), 'ESA', 'Testing correct incident Sources is rendered');
  assert.equal(this.$('.rsa-incident-detail-header__assignee .prompt').text().trim(), 'User 1', 'Testing correct incident Assignee is rendered');
  assert.equal(this.$('.rsa-incident-detail-header__status .prompt').text().trim(), 'New', 'Testing correct incident Status is rendered');

  assert.equal(this.$('.rsa-incident-detail-header__source-ip label').text(), '1.1.1.1', 'Testing correct incident Source-IP is rendered');
  assert.equal(this.$('.rsa-incident-detail-header__destination-ip label').text(), '2.2.2.2', 'Testing correct incident Destination-IP is rendered');

});

test('The incident status, priority and assignee are saved', function(assert) {

  let incident = EmberObject.create({
    riskScore: 1,
    id: 'INC-491',
    name: 'Suspected command and control communication with www.mozilla.com',
    createdBy: 'User X',
    created: '2015-10-10',
    lastUpdated: '2015-10-10',
    statusSort: 0,
    prioritySort: 0,
    alertCount: 10,
    eventCount: 2,
    sources: ['Event Stream Analysis'],
    assignee: {
      id: '1'
    },
    groupBySourceIp: ['1.1.1.1'],
    groupByDestinationIp: ['2.2.2.2']
  });
  let users = [EmberObject.create({ id: '1', firstName: 'User 1', lastName: 'LastName 1', email: 'user1@rsa.com' }),
      EmberObject.create({ id: '2', firstName: 'User 2', lastName: 'LastName 2', email: 'user2@rsa.com' }),
      EmberObject.create({ id: '3', firstName: 'User 3', lastName: 'LastName 3', email: 'user3@rsa.com' }) ];

  incident.save = function() { };

  this.setProperties({
    incident,
    users
  });

  this.render(hbs`{{rsa-respond/incident-detail/detail-header incident=incident users=users}}`);

  let statusVal = this.$('.rsa-incident-detail-header__status select').val();
  assert.equal(statusVal, 0, 'Tile displays the current Incident status.');

  this.$('.rsa-incident-detail-header__status .prompt').click();
  this.$('.rsa-incident-detail-header__status select').val(1).trigger('change');
  assert.equal(incident.statusSort, 1, 'After updating the Select, the incident status has the new value');

  let priorityVal = this.$('.rsa-incident-detail-header__priority select').val();
  assert.equal(priorityVal, 0, 'Tile displays the current Incident priority.');

  this.$('.rsa-incident-detail-header__priority .prompt').click();
  this.$('.rsa-incident-detail-header__priority select').val(1).trigger('change');
  assert.equal(incident.prioritySort, 1, 'After updating the Select, the incident priority has the new value');

  let assigneeVal = this.$('.rsa-incident-detail-header__assignee select').val();
  assert.equal(assigneeVal, 1, 'Tile displays the current Incident priority.');

  this.$('.rsa-incident-detail-header__assignee .prompt').click();
  this.$('.rsa-incident-detail-header__assignee select').val(2).trigger('change');
  assert.equal(incident.assignee.id, 2, 'After updating the Select, the incident assignee has the new value');

  this.$('.rsa-incident-detail-header__assignee .prompt').click();
  this.$('.rsa-incident-detail-header__assignee select').val(-1).trigger('change');
  assert.equal(incident.assignee, undefined, 'After updating the Select, the incident assignee has been removed');
});


test('Manually changing the state of an incident to Closed disables editable fields', function(assert) {

  let incident = EmberObject.create({
    riskScore: 1,
    id: 'INC-491',
    name: 'Suspected command and control communication with www.mozilla.com',
    createdBy: 'User X',
    created: '2015-10-10',
    lastUpdated: '2015-10-10',
    statusSort: 0,
    prioritySort: 0,
    alertCount: 10,
    eventCount: 2,
    sources: ['Event Stream Analysis'],
    assignee: {
      id: '1'
    }
  });
  let users = [EmberObject.create({ id: '1', firstName: 'User 1', lastName: 'LastName 1', email: 'user1@rsa.com' }),
      EmberObject.create({ id: '2', firstName: 'User 2', lastName: 'LastName 2', email: 'user2@rsa.com' }),
      EmberObject.create({ id: '3', firstName: 'User 3', lastName: 'LastName 3', email: 'user3@rsa.com' }) ];

  incident.save = function() { };

  this.setProperties({
    incident,
    users
  });

  this.render(hbs`{{rsa-respond/incident-detail/detail-header incident=incident users=users}}`);

  this.$('.rsa-incident-detail-header__status .prompt').click();
  this.$('.rsa-incident-detail-header__status select').val(incStatus.CLOSED).trigger('change');

  assert.equal(this.$('.rsa-incident-detail-header__name').hasClass('is-read-only'), true, 'When Incident is in Closed state, Name input is disabled');
  assert.equal(this.$('.rsa-incident-detail-header__priority').hasClass('is-disabled'), true, 'When Incident is in Closed state, Priority dropdown is disabled');
  assert.equal(this.$('.rsa-incident-detail-header__assignee').hasClass('is-disabled'), true, 'When Incident is in Closed state, Assignee dropdown is disabled');
});


test('Incident priority order check (Critical -> Low)', function(assert) {

  let incident = EmberObject.create({
    riskScore: 1,
    id: 'INC-491',
    name: 'Suspected command and control communication with www.mozilla.com',
    createdBy: 'User X',
    created: '2015-10-10',
    lastUpdated: '2015-10-10',
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

  this.setProperties({
    incident,
    users
  });

  this.render(hbs`{{rsa-respond/incident-detail/detail-header model=incident users=users}}`);

  let container = this.$(selectors.pages.respond.details.header.detailHeader);

  let priorityOptionList = container.find(this.$(selectors.pages.respond.details.header.prioritySelectOption));

  assert.equal(priorityOptionList[0].text, 'Critical', 'First priority is Critical');
  assert.equal(priorityOptionList[1].text, 'High', 'Second priority is High');
  assert.equal(priorityOptionList[2].text, 'Medium', 'Third priority is Medium');
  assert.equal(priorityOptionList[3].text, 'Low', 'Fourth priority is Low');
});

test('Alert and event count missing test', function(assert) {

  let incident = EmberObject.create({
    riskScore: 1,
    id: 'INC-491',
    name: 'Suspected command and control communication with www.mozilla.com',
    createdBy: 'User X',
    created: '2015-10-10',
    lastUpdated: '2015-10-10',
    statusSort: 0,
    prioritySort: 0,
    sources: ['Event Stream Analysis'],
    assignee: {
      id: '1'
    }
  });
  let users = [EmberObject.create({ id: '1', firstName: 'User 1', lastName: 'LastName 1', email: 'user1@rsa.com' }),
    EmberObject.create({ id: '2', firstName: 'User 2', lastName: 'LastName 2', email: 'user2@rsa.com' }),
    EmberObject.create({ id: '3', firstName: 'User 3', lastName: 'LastName 3', email: 'user3@rsa.com' }) ];

  incident.save = function() { };

  this.setProperties({
    incident,
    users
  });

  this.render(hbs`{{rsa-respond/incident-detail/detail-header incident=incident users=users}}`);

  assert.equal(this.$('.rsa-incident-detail-header__alerts label').text(), '-', 'Missing alert count is shown');
  assert.equal(this.$('.rsa-incident-detail-header__events label').text(), '-', 'Missing event count is shown');

});

test('Testing source & destination IP values.', function(assert) {

  let incident = EmberObject.create({
    riskScore: 1,
    id: 'INC-491',
    name: 'Suspected command and control communication with www.mozilla.com',
    createdBy: 'User X',
    created: '2015-10-10',
    lastUpdated: '2015-10-10',
    statusSort: 0,
    prioritySort: 0,
    alertCount: 10,
    eventCount: 2,
    sources: ['Event Stream Analysis'],
    assignee: {
      id: '1'
    },
    groupBySourceIp: ['1.1.1.1', '1.1.1.2'],
    groupByDestinationIp: ['2.2.2.2', '2.1.1.1']
  });
  let users = [EmberObject.create({ id: 1, firstName: 'User 1', lastName: 'LastName 1', email: 'user1@rsa.com' }),
    EmberObject.create({ id: 2, firstName: 'User 2', lastName: 'LastName 2', email: 'user2@rsa.com' }),
    EmberObject.create({ id: 3, firstName: 'User 3', lastName: 'LastName 3', email: 'user3@rsa.com' }) ];

  this.setProperties({
    incident,
    users
  });

  this.render(hbs`{{rsa-respond/incident-detail/detail-header incident=incident users=users}}`);

  assert.equal(this.$('.rsa-incident-detail-header__source-ip label').text(), '(2 IPs)', 'When multiple source-Ips displays number of IPs');
  assert.equal(this.$('.rsa-incident-detail-header__destination-ip label').text(), '(2 IPs)', 'When multiple destination-Ips displays number of IPs');

  this.setProperties({
    'incident.groupBySourceIp': [],
    'incident.groupByDestinationIp': []
  });

  assert.equal(this.$('.rsa-incident-detail-header__source-ip label').text(), '-', 'When no source-IP values, it displays a -');
  assert.equal(this.$('.rsa-incident-detail-header__destination-ip label').text(), '-', 'When no destination-IP values, it displays a -');

  this.setProperties({
    'incident.groupBySourceIp': null,
    'incident.groupByDestinationIp': null
  });

  assert.equal(this.$('.rsa-incident-detail-header__source-ip label').text(), '-', 'When source-IP is not present a - is displayed');
  assert.equal(this.$('.rsa-incident-detail-header__destination-ip label').text(), '-', 'When destination-IP is not present a - is displayed');

  this.setProperties({
    'incident.groupBySourceIp': 'some string',
    'incident.groupByDestinationIp': 111
  });

  assert.equal(this.$('.rsa-incident-detail-header__source-ip label').text(), '-', 'When source-IP is not an array a - is displayed');
  assert.equal(this.$('.rsa-incident-detail-header__destination-ip label').text(), '-', 'When destination-IP is not an array a - is displayed');
});