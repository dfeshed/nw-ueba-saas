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
      .sortableColumns()
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
    assert.equal(find('.rsa-loader').textContent.trim(), 'Query is being re-executed to fetch new data.', 'Displaying the correct message');
  });

  test('if events are streaming and the query was re-executed by changing sort, a spinner is displayed with following message', async function(assert) {
    new ReduxDataHelper(setState)
      .isQueryExecutedBySort()
      .eventResultsStatus('between-streams')
      .eventsPreferencesConfig()
      .build();

    await render(hbs`{{events-table-container/events-table}}`);
    assert.equal(find('.rsa-loader').textContent.trim(), 'Query is being re-executed to fetch new data.', 'Displaying the correct message');
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
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('hasInvestigateContentExportAccess', true);
    new ReduxDataHelper(setState)
      .eventResultsStatus('canceled')
      .getColumns('SUMMARY', EventColumnGroups)
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
    assert.ok(
      find('.rsa-data-table-load-more').textContent.trim().includes('1 of 2'),
      'correct message when partial results returned'
    );
    assert.ok(
      find('.rsa-data-table-load-more').textContent.trim().includes('cancellation'),
      'correct message when partial results returned'
    );
    assert.equal(findAll('.rsa-data-table-header .rsa-form-checkbox-label').length, 1, 'Status - cancelled: Renders selectAll checkbox when all results are loaded in cancelling a query in between');
    assert.equal(findAll('.rsa-data-table-header .rsa-form-checkbox.disabled').length, 0, 'Status - cancelled: selectAll checkbox is enabled when all results are loaded in cancelling a query in between');
    assert.equal(findAll('.rsa-data-table-body .rsa-form-checkbox-label').length, 1, 'Individual row selection checkbox available for the 1 event loaded');
  });

  test('if an error is received, but some results have returned, a message is displayed', async function(assert) {
    new ReduxDataHelper(setState)
      .isEventResultsError(true, 'error')
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
    assert.ok(
      find('.rsa-data-table-load-more').textContent.trim().includes('1 of 2'),
      'correct message when partial results returned'
    );
    assert.ok(
      find('.rsa-data-table-load-more').textContent.trim().includes('error'),
      'correct message when partial results returned'
    );
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

  test('event table is displayed with expected default column sort controls', async function(assert) {
    new ReduxDataHelper(setState)
      .columnGroup('SUMMARY')
      .hasRequiredValuesToQuery(true)
      .eventThreshold(100000)
      .eventsPreferencesConfig()
      .columnGroups(EventColumnGroups)
      .eventsQuerySort('time', 'Ascending')
      .sortableColumns(['time', 'size'])
      .language([{ format: 'TimeT', metaName: 'time', flags: -2147482605 }, { format: 'Int', metaName: 'size', flags: -2147482605 }])
      .eventCount(100000)
      .build();

    await render(hbs`{{events-table-container/events-table}}`);
    assert.equal(findAll('.rsa-data-table-header-row div > h2 .sort-indicator .rsa-icon-arrow-up-7-filled').length, 2);
    assert.equal(findAll('.rsa-data-table-header-row div > h2 .sort-indicator.active .rsa-icon-arrow-up-7-filled').length, 1);
    assert.equal(findAll('.rsa-data-table-header-row div > h2 .sort-indicator:not(.active) .rsa-icon-arrow-up-7-filled').length, 1);
  });

  test('event table is displayed with expected ascending sort controls', async function(assert) {
    new ReduxDataHelper(setState)
      .columnGroup('SUMMARY')
      .hasRequiredValuesToQuery(true)
      .eventThreshold(100000)
      .eventsPreferencesConfig()
      .columnGroups(EventColumnGroups)
      .sortableColumns()
      .eventsQuerySort('time', 'Ascending')
      .language([{ format: 'TimeT', metaName: 'time', flags: -2147482605 }])
      .eventCount(100000)
      .build();

    await render(hbs`{{events-table-container/events-table}}`);
    assert.ok(find('.rsa-data-table-header-row div > h2 .sort-indicator.active .rsa-icon-arrow-up-7-filled'));
  });

  test('event table is displayed with expected descending sort controls', async function(assert) {
    new ReduxDataHelper(setState)
      .columnGroup('SUMMARY')
      .hasRequiredValuesToQuery(true)
      .eventThreshold(100000)
      .eventsPreferencesConfig()
      .columnGroups(EventColumnGroups)
      .sortableColumns()
      .eventsQuerySort('time', 'Descending')
      .language([{ format: 'TimeT', metaName: 'time', flags: -2147482605 }])
      .eventCount(100000)
      .build();

    await render(hbs`{{events-table-container/events-table}}`);
    assert.ok(find('.rsa-data-table-header-row div > h2 .sort-indicator.active .rsa-icon-arrow-down-7-filled'));
  });

  test('event table sort controls calls _toggleSort', async function(assert) {
    assert.expect(2);
    new ReduxDataHelper(setState)
      .columnGroup('SUMMARY')
      .hasRequiredValuesToQuery(true)
      .eventThreshold(100000)
      .eventsPreferencesConfig()
      .columnGroups(EventColumnGroups)
      .sortableColumns()
      .eventsQuerySort('time', 'Ascending')
      .eventCount(100000)
      .language([{ format: 'TimeT', metaName: 'time', flags: -2147482605 }])
      .build();

    this.set('_toggleSort', (field, dir) => {
      assert.equal(field, 'time');
      assert.equal(dir, 'Descending');
    });

    await render(hbs`{{events-table-container/events-table _toggleSort=_toggleSort}}`);

    click('.rsa-data-table-header-row div > h2 .sort-indicator.active');
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

  test('renders event selection checkboxes if only download permissions are present', async function(assert) {
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('hasInvestigateContentExportAccess', true);
    accessControl.set('respondCanManageIncidents', false);
    new ReduxDataHelper(setState)
      .getColumns('SUMMARY', EventColumnGroups)
      .eventResults([{ sessionId: 'foo', time: 123 }, { sessionId: 'bar', time: 123 }])
      .eventsPreferencesConfig()
      .build();

    await render(hbs`{{events-table-container/events-table}}`);

    assert.equal(findAll('.rsa-form-checkbox-label').length, 3, 'Renders event selection checkboxes when permission is present');
  });


  test('renders event selection checkboxes if only manage incident permissions are present', async function(assert) {
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('respondCanManageIncidents', true);
    accessControl.set('hasInvestigateContentExportAccess', false);
    new ReduxDataHelper(setState)
      .getColumns('SUMMARY', EventColumnGroups)
      .eventResults([{ sessionId: 'foo', time: 123 }, { sessionId: 'bar', time: 123 }])
      .eventsPreferencesConfig()
      .build();

    await render(hbs`{{events-table-container/events-table}}`);
    assert.equal(findAll('.rsa-form-checkbox-label').length, 3, 'Renders event selection checkboxes when manage incident permission is present');
  });

  test('renders event selection checkboxes if both incident/download permissions are present', async function(assert) {
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('hasInvestigateContentExportAccess', true);
    accessControl.set('respondCanManageIncidents', true);
    new ReduxDataHelper(setState)
      .getColumns('SUMMARY', EventColumnGroups)
      .eventResults([{ sessionId: 'foo', time: 123 }, { sessionId: 'bar', time: 123 }])
      .eventsPreferencesConfig()
      .build();

    await render(hbs`{{events-table-container/events-table}}`);
    assert.equal(findAll('.rsa-form-checkbox-label').length, 3, 'Renders event selection checkboxes when both permission are present');
    assert.equal(findAll('.rsa-data-table-header .rsa-form-checkbox-label').length, 1, 'Status - complete: Renders selectAll checkbox when all expected data loaded');
    assert.equal(findAll('.rsa-data-table-header .rsa-form-checkbox.disabled').length, 0, 'Status - complete: selectAll checkbox is enabled when all results are loaded in cancelling a query in between');
  });


  test('disables event selection checkboxes if no results found, even though permissions are present', async function(assert) {
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('hasInvestigateContentExportAccess', true);
    accessControl.set('respondCanManageIncidents', true);
    new ReduxDataHelper(setState)
      .getColumns('SUMMARY', EventColumnGroups)
      .eventResults([])
      .eventsPreferencesConfig()
      .build();

    await render(hbs`{{events-table-container/events-table}}`);
    assert.equal(findAll('.rsa-form-checkbox.disabled').length, 1, 'Render disabled selectAll checkbox when both permission are present but there are 0 results');
  });

  test('does not render event selection checkboxes if permissions are not present', async function(assert) {
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('hasInvestigateContentExportAccess', false);
    accessControl.set('respondCanManageIncidents', false);
    new ReduxDataHelper(setState)
      .getColumns('SUMMARY', EventColumnGroups)
      .eventsPreferencesConfig()
      .eventResults([{ sessionId: 'foo', time: 123 }, { sessionId: 'bar', time: 123 }])
      .build();

    await render(hbs`{{events-table-container/events-table}}`);

    assert.equal(findAll('.rsa-form-checkbox-label').length, 0, 'Does not render event selection checkboxes when neither permission is present');
  });

  test('If the search is still under way, the "Select All" checkbox is disabled', async function(assert) {
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('hasInvestigateContentExportAccess', true);
    new ReduxDataHelper(setState)
      .getColumns('SUMMARY', EventColumnGroups)
      .eventsPreferencesConfig()
      .eventResultsStatus('between-streams')
      .eventResults([{ sessionId: 'foo', time: 123 }, { sessionId: 'bar', time: 123 }])
      .build();

    await render(hbs`{{events-table-container/events-table}}`);

    assert.equal(findAll('.rsa-data-table-header .rsa-form-checkbox.disabled').length, 1, 'Status - streaming/between-streams: Render disabled  selectAll checkbox');
    assert.equal(findAll('.rsa-data-table-body .rsa-form-checkbox-label').length, 2, 'Individual row selection checkboxes available for the 2 events loaded');
  });
});
