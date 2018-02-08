import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';
import engineResolverFor from '../../../../helpers/engine-resolver';

moduleForComponent('events-table-container/body-container', 'Integration | Component | events table body', {
  integration: true,
  resolver: engineResolverFor('investigate-events'),
  beforeEach() {
    // TODO figure out what to specifically inject into, rather than all components
    this.registry.injection('component', 'i18n', 'service:i18n');
  }
});

const now = +(new Date());

const logEventsWithLogData = [
  {
    sessionId: 1,
    time: now,
    medium: 32,
    log: 'foo'
  }
];

const logEventsWithoutLogData = [
  {
    sessionId: 2,
    time: now,
    medium: 32
  }
];

const logEvents = logEventsWithLogData.concat(logEventsWithoutLogData);

const nonLogEvents = [
  {
    sessionId: 3,
    time: now,
    medium: 1
  }
];

const mixedEvents = logEvents.concat(nonLogEvents);

const columnsConfig = [{
  field: 'sessionId'
}];

test('it renders', function(assert) {
  this.render(hbs`{{#rsa-data-table}}{{events-table-container/body-container}}{{/rsa-data-table}}`);

  assert.equal(this.$('.rsa-data-table-body').length, 1, 'Expected root DOM node');
});

test('it fires its loadEventsLogsAction when it finds log events without log data', function(assert) {
  assert.expect(2);

  this.setProperties({
    columnsConfig,
    logEventsWithoutLogData,
    loadLogsAction() {
      assert.ok(true, 'Fired loadLogsAction');
    }
  });

  this.render(hbs`{{#rsa-data-table columnsConfig=columnsConfig items=logEventsWithoutLogData loadLogsAction=loadLogsAction}}
    {{events-table-container/body-container}}
    {{/rsa-data-table}}`);

  return wait().then(() => {
    assert.ok(true, 'Waited enough time for async logic to be executed.');
  });
});

test('it doesn\'t fire its loadEventsLogsAction when it finds log events with log data', function(assert) {
  assert.expect(1);

  this.setProperties({
    columnsConfig,
    logEventsWithLogData,
    loadLogsAction() {
      assert.ok(true, 'Fired loadLogsAction but should not have');
    }
  });

  this.render(hbs`{{#rsa-data-table columnsConfig=columnsConfig items=logEventsWithLogData loadLogsAction=loadLogsAction}}
    {{events-table-container/body-container}}
    {{/rsa-data-table}}`);

  return wait().then(() => {
    assert.ok(true, 'Waited enough time for async logic to be executed.');
  });
});

test('it doesn\'t fire its loadEventsLogsAction when it finds non-log events', function(assert) {
  assert.expect(1);

  this.setProperties({
    columnsConfig,
    nonLogEvents,
    loadLogsAction() {
      assert.ok(true, 'Fired loadLogsAction but should not have');
    }
  });

  this.render(hbs`{{#rsa-data-table columnsConfig=columnsConfig items=nonLogEvents loadLogsAction=loadLogsAction}}
    {{events-table-container/body-container}}
    {{/rsa-data-table}}`);

  return wait().then(() => {
    assert.ok(true, 'Waited enough time for async logic to be executed.');
  });
});

test('it only sends log events without data to its loadEventsLogsAction when it finds a mix of events', function(assert) {
  assert.expect(2 + logEventsWithoutLogData.length);


  this.setProperties({
    columnsConfig,
    mixedEvents,
    loadLogsAction(events) {
      assert.equal(
        events.length,
        logEventsWithoutLogData.length,
        'Expected to submit the known number of log events without data'
      );
      events.forEach((evt) => {
        const found = logEventsWithoutLogData.findBy('sessionId', evt.sessionId);
        assert.ok(found, 'Expected to find the submitted event in the list of known log events without data');
      });
    }
  });

  this.render(hbs`{{#rsa-data-table columnsConfig=columnsConfig items=mixedEvents loadLogsAction=loadLogsAction}}
    {{events-table-container/body-container}}
    {{/rsa-data-table}}`);

  return wait().then(() => {
    assert.ok(true, 'Waited enough time for async logic to be executed.');
  });
});
