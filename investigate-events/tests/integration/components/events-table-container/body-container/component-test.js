import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { findAll, render, settled } from '@ember/test-helpers';

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
const columnsConfig = [{ field: 'sessionId' }];

module('Integration | Component | Events Table Body', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  test('it renders', async function(assert) {
    await render(hbs`
      {{#rsa-data-table}}
        {{events-table-container/body-container}}
      {{/rsa-data-table}}
    `);

    assert.equal(findAll('.rsa-data-table-body').length, 1, 'Expected root DOM node');
  });

  test('it fires its loadEventsLogsAction when it finds log events without log data', async function(assert) {
    const done = assert.async();

    this.setProperties({
      columnsConfig,
      logEventsWithoutLogData,
      loadLogsAction: () => {
        assert.ok(true, 'Fired loadLogsAction');
        done();
      }
    });

    await render(hbs`
      {{#rsa-data-table
        columnsConfig=columnsConfig
        items=logEventsWithoutLogData
        loadLogsAction=(action loadLogsAction)
      }}
        {{events-table-container/body-container}}
      {{/rsa-data-table}}
    `);
  });

  test('it doesn\'t fire its loadEventsLogsAction when it finds log events with log data', async function(assert) {
    assert.expect(0);
    const done = assert.async();

    this.setProperties({
      columnsConfig,
      logEventsWithLogData,
      loadLogsAction: () => {
        assert.notOk(true, 'Fired loadLogsAction but should not have');
      }
    });

    await render(hbs`
      {{#rsa-data-table
        columnsConfig=columnsConfig
        items=logEventsWithLogData
        loadLogsAction=(action loadLogsAction)
      }}
        {{events-table-container/body-container}}
      {{/rsa-data-table}}
    `);

    await settled().then(() => {
      done();
    });
  });

  test('it doesn\'t fire its loadEventsLogsAction when it finds non-log events', async function(assert) {
    assert.expect(0);
    const done = assert.async();

    this.setProperties({
      columnsConfig,
      nonLogEvents,
      loadLogsAction: () => {
        assert.notOk(true, 'Fired loadLogsAction but should not have');
      }
    });

    await render(hbs`
      {{#rsa-data-table
        columnsConfig=columnsConfig
        items=nonLogEvents
        loadLogsAction=(action loadLogsAction)
      }}
        {{events-table-container/body-container}}
      {{/rsa-data-table}}`);

    await settled().then(() => {
      done();
    });
  });

  test('it only sends log events without data to its loadEventsLogsAction when it finds a mix of events', async function(assert) {
    assert.expect(2);

    this.setProperties({
      columnsConfig,
      mixedEvents,
      loadLogsAction: (events) => {
        assert.equal(events.length, logEventsWithoutLogData.length, 'Expected to submit the known number of log events without data');
        events.forEach((evt) => {
          const found = logEventsWithoutLogData.findBy('sessionId', evt.sessionId);
          assert.ok(found, 'Expected to find the submitted event in the list of known log events without data');
        });
      }
    });

    await render(hbs`
      {{#rsa-data-table
        columnsConfig=columnsConfig
        items=mixedEvents
        loadLogsAction=(action loadLogsAction)
      }}
        {{events-table-container/body-container}}
      {{/rsa-data-table}}`);
  });
});