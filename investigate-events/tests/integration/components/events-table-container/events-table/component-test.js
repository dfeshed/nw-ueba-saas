import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import EventColumnGroups from '../../../../data/subscriptions/investigate-columns/data';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { find, findAll, render, click } from '@ember/test-helpers';

let setState;

module('Integration | Component | events-table', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  test('it renders with Context menu trigger', async function(assert) {
    new ReduxDataHelper(setState)
      .eventsPreferencesConfig()
      .build();

    await render(hbs`
      {{events-table-container/events-table
        contextItems=contextItems
      }}
    `);
    assert.equal(findAll('.content-context-menu').length, 1, 'Context menu trigger rendered');
  });

  test('it shows context menu on right click', async function(assert) {
    new ReduxDataHelper(setState)
      .eventsPreferencesConfig()
      .build();

    await render(hbs`
      {{events-table-container/events-table
        metaName=metaName
        metaValue=metaValue
      }}
    `);
    await find('.js-move-handle').setAttribute('metaname', 'ip.src');
    await find('.js-move-handle').setAttribute('metavalue', '1.1.1.1');
    this.$('.js-move-handle:first').trigger({
      type: 'contextmenu',
      clientX: 100,
      clientY: 100
    });

    assert.equal(this.get('metaName'), 'ip.src', 'meta name extracted from event and set');
    assert.equal(this.get('metaValue'), '1.1.1.1', 'meta value extracted from event and set');

  });

  test('context menu is deactivated on right clicking outside the target', async function(assert) {
    assert.expect(0);
    const done = assert.async();
    const contextMenuService = {
      isActive: true,
      deactivate: () => {
        done();
      }
    };
    this.set('contextMenuService', contextMenuService);
    new ReduxDataHelper(setState)
      .eventsPreferencesConfig()
      .build();

    await render(hbs`
      {{events-table-container/events-table
        metaName=metaName
        metaValue=metaValue
        contextMenuService=contextMenuService
      }}
    `);

    await find('.js-move-handle').setAttribute('metaname', 'ip.src');
    await find('.js-move-handle').setAttribute('metavalue', '1.1.1.1');
    this.$('.js-move-handle:first').trigger({
      type: 'contextmenu',
      clientX: 100,
      clientY: 100
    });

    // rt-click elsewhere
    this.$('.rsa-data-table-header').contextmenu();
  });

  test('if events are streaming, a spinner is displayed with appropriate message', async function(assert) {
    new ReduxDataHelper(setState)
      .eventResultsStatus('between-streams')
      .eventsPreferencesConfig()
      .build();

    await render(hbs`{{events-table-container/events-table}}`);
    assert.ok(find('.rsa-loader'), 'spinner present');
    assert.equal(find('.rsa-loader').textContent.trim(), 'Loading', 'Displays the correct message on the spinner');
  });

  test('if events are streaming and the query was re-executed by changing column group, a spinner is displayed with following message', async function(assert) {
    new ReduxDataHelper(setState)
      .isQueryExecutedByColumnGroup()
      .eventResultsStatus('between-streams')
      .eventsPreferencesConfig()
      .build();

    await render(hbs`{{events-table-container/events-table}}`);
    assert.equal(find('.rsa-loader').textContent.trim(), 'Query is being re-executed to fetch different columns', 'Displaying the correct message');
  });

  test('if events are complete, but hit the limit, a message is displayed', async function(assert) {
    new ReduxDataHelper(setState)
      .eventResultsStatus('complete')
      .eventCount(1)
      .streamLimit(1)
      .eventResults([{ sessionId: 'foo', time: 123 }])
      .eventsPreferencesConfig()
      .build();

    await render(hbs`{{events-table-container/events-table}}`);
    assert.notOk(find('.rsa-loader'), 'spinner present');
    assert.equal(
      find('.rsa-data-table-load-more').textContent.trim().length > 0,
      true,
      'a message is displayed when we get the max events'
    );
    assert.equal(find('.rsa-data-table-load-more').textContent.trim(), 'Reached the 1 event limit. Consider refining your query.', 'Footer message when limit reached');
  });

  test('if events are complete, and there are results, and did not hit the limit, a message is displayed', async function(assert) {
    new ReduxDataHelper(setState)
      .eventResultsStatus('complete')
      .eventCount(1)
      .streamLimit(100)
      .eventResults([{ sessionId: 'foo', time: 123 }])
      .eventsPreferencesConfig()
      .build();

    await render(hbs`{{events-table-container/events-table}}`);
    assert.notOk(find('.rsa-loader'), 'spinner present');
    assert.equal(
      find('.rsa-data-table-load-more').textContent.trim().length > 0,
      true,
      'a message is displayed when the entire event result is fetched'
    );
    assert.equal(find('.rsa-data-table-load-more').textContent.trim(), 'All results loaded', 'Footer message when the entire event result is fetched');
  });

  test('if events are complete, and there are no results, a message is not displayed', async function(assert) {
    new ReduxDataHelper(setState)
      .eventResultsStatus('complete')
      .eventCount(1)
      .streamLimit(100)
      .eventResults([])
      .eventsPreferencesConfig()
      .build();

    await render(hbs`{{events-table-container/events-table}}`);
    assert.notOk(find('.rsa-loader'), 'spinner present');
    assert.equal(
      find('.rsa-data-table-load-more').textContent.trim().length > 0,
      false,
      'a message is not displayed when the entire event result is fetched and there are no results'
    );
  });

  test('if events are canceled before any results are returned, a message is displayed', async function(assert) {
    new ReduxDataHelper(setState)
      .eventResultsStatus('canceled')
      .eventCount(0)
      .streamLimit(100)
      .eventResults([])
      .eventsPreferencesConfig()
      .build();

    await render(hbs`{{events-table-container/events-table}}`);
    assert.notOk(find('.rsa-loader'), 'spinner should not be present');
    assert.equal(find('.no-results-message').textContent.trim(),
      'Query canceled before any results were returned.',
      'correct cancellation message'
    );
  });

  test('if events are canceled, but some results have returned, a message is displayed', async function(assert) {
    new ReduxDataHelper(setState)
      .eventResultsStatus('canceled')
      .eventCount(2)
      .streamLimit(100)
      .eventsPreferencesConfig()
      .eventResults([{ sessionId: 'foo', time: 123 }])
      .build();

    await render(hbs`{{events-table-container/events-table}}`);
    assert.notOk(find('.rsa-loader'), 'spinner should not be present');
    assert.equal(findAll('.rsa-investigate-events-table-row').length, 1,
      'correct number of rows'
    );
    assert.equal(find('.rsa-data-table-load-more').textContent.trim(),
      'Retrieved 1 of 2 events prior to query cancellation.',
      'correct cancellation message when partial results returned'
    );
  });

  test('renders event selection checkboxes if only download permissions are present', async function(assert) {
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('hasInvestigateContentExportAccess', true);
    accessControl.set('respondCanManageIncidents', false);
    new ReduxDataHelper(setState)
      .getColumns('SUMMARY', EventColumnGroups)
      .eventResults([])
      .eventsPreferencesConfig()
      .build();

    await render(hbs`{{events-table-container/events-table}}`);

    assert.equal(findAll('.rsa-form-checkbox-label').length, 1, 'Renders event selection checkboxes when permission is present');
  });

  test('when a row is clicked selectEvent fired', async function(assert) {
    assert.expect(2);
    new ReduxDataHelper(setState)
      .eventCount(1)
      .eventsPreferencesConfig()
      .eventResults([{ sessionId: 'foo', time: 123 }])
      .build();

    this.set('selectEvent', () => {
      assert.ok(true);
    });

    await render(hbs`{{events-table-container/events-table selectEvent=selectEvent}}`);
    assert.equal(findAll('.rsa-data-table-body-row').length > 0, true, 'a row is rendered');
    await click('.rsa-data-table-body-row');
  });

  test('when a group label is clicked selectEvent is not fired', async function(assert) {
    assert.expect(1);
    new ReduxDataHelper(setState)
      .eventCount(2)
      .eventsPreferencesConfig()
      .eventResults([{ sessionId: 'foo', time: 123 }, { sessionId: 'bar', time: 123 }])
      .build();

    this.set('selectEvent', () => {
      assert.ok(false);
    });

    await render(hbs`{{events-table-container/events-table selectEvent=selectEvent enableGrouping=true groupingSize=1}}`);
    assert.equal(findAll('.rsa-data-table-body-row').length > 0, true, 'a row is rendered');
    await click('.group-label');
    await click('.group-label-copy');
  });

  test('event table is displayed with expected column group\'s default header values', async function(assert) {
    new ReduxDataHelper(setState)
      .columnGroup('SUMMARY')
      .eventThreshold(100000)
      .eventsPreferencesConfig()
      .columnGroups(EventColumnGroups)
      .eventCount(100000)
      .build();

    await render(hbs`{{events-table-container/events-table}}`);
    const headerColumns = findAll('.rsa-data-table-header-row div > h2');
    assert.equal('COLLECTION TIME', headerColumns[0].innerText, 'Expected 1st column');
    assert.equal('TYPE', headerColumns[1].innerText, 'Expected 2nd column');

  });

  test('renders event selection checkboxes if only manage incident permissions are present', async function(assert) {
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('respondCanManageIncidents', true);
    accessControl.set('hasInvestigateContentExportAccess', false);
    new ReduxDataHelper(setState)
      .getColumns('SUMMARY', EventColumnGroups)
      .eventResults([])
      .eventsPreferencesConfig()
      .build();

    await render(hbs`{{events-table-container/events-table}}`);
    assert.equal(findAll('.rsa-form-checkbox-label').length, 1, 'Renders event selection checkboxes when  manage incident permission is present');
  });

  test('renders event selection checkboxes if both incident/download permissions are present', async function(assert) {
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('hasInvestigateContentExportAccess', true);
    accessControl.set('respondCanManageIncidents', true);
    new ReduxDataHelper(setState)
      .getColumns('SUMMARY', EventColumnGroups)
      .eventResults([])
      .eventsPreferencesConfig()
      .build();

    await render(hbs`{{events-table-container/events-table}}`);
    assert.equal(findAll('.rsa-form-checkbox-label').length, 1, 'Renders event selection checkboxes when both permission are present');
  });

  test('does not render event selection checkboxes if permissions are not present', async function(assert) {
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('hasInvestigateContentExportAccess', false);
    accessControl.set('respondCanManageIncidents', false);
    new ReduxDataHelper(setState)
      .getColumns('SUMMARY', EventColumnGroups)
      .eventsPreferencesConfig()
      .eventResults([])
      .build();

    await render(hbs`{{events-table-container/events-table}}`);

    assert.equal(findAll('.rsa-form-checkbox-label').length, 0, 'Does not render event selection checkboxes when permission are not present');
  });

});
