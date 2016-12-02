import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import Ember from 'ember';
import selectors from 'sa/tests/selectors';
import { clickTrigger, nativeMouseUp } from '../../../../../helpers/ember-power-select';
import wait from 'ember-test-helpers/wait';

const {
  Object: EmberObject,
  $
} = Ember;

moduleForComponent('rsa-incident-detail-header', 'Integration | Component | rsa respond/incident detail/detail header', {
  integration: true,

  beforeEach() {
    const users = [
      EmberObject.create({ id: '1', name: 'User 1', email: 'user1@rsa.com' }),
      EmberObject.create({ id: '2', name: 'User 2', email: 'user2@rsa.com' }),
      EmberObject.create({ id: '3', name: 'User 3', email: 'user3@rsa.com' })
    ];
    this.set('users', users);
  }
});

test('The incident detail header component is rendered properly.', function(assert) {
  const done = assert.async();

  const incident = EmberObject.create({
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

  this.set('incident', incident);

  this.render(hbs`{{rsa-respond/incident-detail/detail-header incident=incident users=users}}`);

  wait().then(() => {
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

    assert.equal(this.$('.rsa-incident-detail-header__id').text().indexOf('INC-491') >= 0, true, 'Testing correct incident ID is rendered');
    assert.equal(this.$('.rsa-incident-detail-header__name input').val(), 'Suspected command and control communication with www.mozilla.com', 'Testing correct incident Name is rendered');
    assert.equal(this.$('.rsa-incident-detail-header__priority .ember-power-select-selected-item').text().trim(), 'Low', 'Testing correct incident Priority is rendered');
    assert.equal(this.$('.rsa-incident-detail-header__alerts label').text(), 10, 'Testing correct incident Alerts is rendered');
    assert.equal(this.$('.rsa-incident-detail-header__events label').text(), 2, 'Testing correct incident Events is rendered');
    assert.equal(this.$('.rsa-incident-detail-header__sources').length, 1, 'Testing correct number of incident Sources is rendered');
    assert.equal(this.$('.rsa-incident-detail-header__sources .rsa-content-label').text().trim(), 'ESA', 'Testing correct incident Sources is rendered');
    assert.equal(this.$('.rsa-incident-detail-header__assignee .ember-power-select-selected-item').text().trim(), 'User 1', 'Testing correct incident Assignee is rendered');
    assert.equal(this.$('.rsa-incident-detail-header__status .ember-power-select-selected-item').text().trim(), 'New', 'Testing correct incident Status is rendered');

    assert.equal(this.$('.rsa-incident-detail-header__source-ip label').text(), '1.1.1.1', 'Testing correct incident Source-IP is rendered');
    assert.equal(this.$('.rsa-incident-detail-header__destination-ip label').text(), '2.2.2.2', 'Testing correct incident Destination-IP is rendered');
    done();
  });
});

test('The incident status, priority and assignee are saved', function(assert) {
  const done = assert.async(4);

  const incident = EmberObject.create({
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

  incident.save = function() { };

  this.set('incident', incident);

  this.render(hbs`{{rsa-respond/incident-detail/detail-header incident=incident users=users}}`);

  wait().then(() => {
    const statusVal = this.$('.rsa-incident-detail-header__status .ember-power-select-selected-item').text().trim();
    assert.equal(statusVal, 'New', 'Tile displays the current Incident status.');

    clickTrigger('.rsa-incident-detail-header__status');
    nativeMouseUp('.ember-power-select-option:eq(1)'); // setting status to ASSIGNED
    wait().then(() => {
      assert.equal(incident.statusSort, 1, 'After updating the Select, the incident status has the new value');
      done();
    });

    const priorityVal = this.$('.rsa-incident-detail-header__priority .ember-power-select-selected-item').text().trim();
    assert.equal(priorityVal, 'Low', 'Tile displays the current Incident priority.');

    clickTrigger('.rsa-incident-detail-header__priority');
    nativeMouseUp('.ember-power-select-option:eq(1)'); // setting priority to HIGH
    wait().then(() => {
      assert.equal(incident.prioritySort, 2, 'After updating the Select, the incident priority has the new value');
      done();
    });

    const assigneeVal = this.$('.rsa-incident-detail-header__assignee .ember-power-select-selected-item').text().trim();
    assert.equal(assigneeVal, 'User 1', 'Tile displays the current Incident priority.');

    clickTrigger('.rsa-incident-detail-header__assignee');
    nativeMouseUp('.ember-power-select-option:eq(2)'); // 3rd element(index:2): {id: 2, name: User 2}
    wait().then(() => {
      assert.equal(incident.assignee.id, '2', 'After updating the Select, the incident assignee has the new value');

      clickTrigger('.rsa-incident-detail-header__assignee');
      nativeMouseUp('.ember-power-select-option:eq(0)'); // 1st element(index:0): unassigned.
      wait().then(() => {
        assert.notOk(incident.assignee, 'After updating the Select, the incident assignee is undefined');
        done();
      });
      done();
    });
  });
});


test('Manually changing the state of an incident to Closed disables editable fields', function(assert) {
  const done = assert.async();
  const incident = EmberObject.create({
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

  incident.save = function() { };

  this.set('incident', incident);

  this.render(hbs`{{rsa-respond/incident-detail/detail-header incident=incident users=users}}`);

  clickTrigger('.rsa-incident-detail-header__status');
  nativeMouseUp('.ember-power-select-option:eq(5)');
  wait().then(() => {
    assert.ok(this.$('.rsa-incident-detail-header__name').hasClass('is-read-only'), 'When Incident is in Closed state, Name input is disabled');
    assert.ok(this.$('.rsa-incident-detail-header__priority .ember-power-select-trigger').attr('aria-disabled'), 'When Incident is in Closed state, Priority dropdown is disabled');
    assert.ok(this.$('.rsa-incident-detail-header__assignee .ember-power-select-trigger').attr('aria-disabled'), 'When Incident is in Closed state, Assignee dropdown is disabled');
    done();
  });
});

test('Incident priority order check (Critical -> Low)', function(assert) {

  const incident = EmberObject.create({
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

  this.set('incident', incident);

  this.render(hbs`{{rsa-respond/incident-detail/detail-header model=incident users=users}}`);

  clickTrigger('.rsa-incident-detail-header__priority');
  const priorityOptionList = $(selectors.pages.respond.details.header.prioritySelectOption);
  assert.equal(priorityOptionList.eq(0).text().trim(), 'Critical', 'First priority is Critical');
  assert.equal(priorityOptionList.eq(1).text().trim(), 'High', 'Second priority is High');
  assert.equal(priorityOptionList.eq(2).text().trim(), 'Medium', 'Third priority is Medium');
  assert.equal(priorityOptionList.eq(3).text().trim(), 'Low', 'Fourth priority is Low');
});

test('Alert and event count missing test', function(assert) {

  const incident = EmberObject.create({
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

  incident.save = function() { };

  this.set('incident', incident);

  this.render(hbs`{{rsa-respond/incident-detail/detail-header incident=incident users=users}}`);

  assert.equal(this.$('.rsa-incident-detail-header__alerts label').text(), '-', 'Missing alert count is shown');
  assert.equal(this.$('.rsa-incident-detail-header__events label').text(), '-', 'Missing event count is shown');
});

test('Testing source & destination IP values.', function(assert) {

  const incident = EmberObject.create({
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

  this.set('incident', incident);

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
