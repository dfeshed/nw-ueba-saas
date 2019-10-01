import { module, skip, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import EventColumnGroups from '../../../../data/subscriptions/column-group/findAll/data';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { find, findAll, render, click, triggerEvent } from '@ember/test-helpers';

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
      .language()
      .getColumns('SUMMARY', EventColumnGroups)
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
      .language()
      .getColumns('SUMMARY', EventColumnGroups)
      .build();

    await render(hbs`
      {{events-table-container/events-table
        metaName=metaName
        metaValue=metaValue
      }}
    `);
    await find('.js-move-handle').setAttribute('metaname', 'ip.src');
    await find('.js-move-handle').setAttribute('metavalue', '1.1.1.1');
    await triggerEvent(find('.js-move-handle'), 'contextmenu', { clientX: 100, clientY: 100 });

    assert.equal(this.get('metaName'), 'ip.src', 'meta name extracted from event and set');
    assert.equal(this.get('metaValue'), '1.1.1.1', 'meta value extracted from event and set');

  });

  test('it removes aliases from metaValues if present', async function(assert) {
    new ReduxDataHelper(setState)
      .eventsPreferencesConfig()
      .language()
      .getColumns('SUMMARY', EventColumnGroups)
      .build();

    await render(hbs`
      {{events-table-container/events-table
        metaName=metaName
        metaValue=metaValue
      }}
    `);
    await find('.js-move-handle').setAttribute('metaname', 'ip.src');
    await find('.js-move-handle').setAttribute('metavalue', '1.1.1.1 [FOOBAR]');
    await triggerEvent(find('.js-move-handle'), 'contextmenu', { clientX: 100, clientY: 100 });

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
      .language()
      .getColumns('SUMMARY', EventColumnGroups)
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
    await triggerEvent(find('.js-move-handle'), 'contextmenu', { clientX: 100, clientY: 100 });

    // rt-click elsewhere
    await triggerEvent(find('.rsa-data-table-header'), 'contextmenu');
  });

  test('if events are streaming, a spinner is displayed with appropriate message', async function(assert) {
    new ReduxDataHelper(setState)
      .eventResultsStatus('between-streams')
      .eventsPreferencesConfig()
      .getColumns('SUMMARY', EventColumnGroups)
      .language()
      .build();

    await render(hbs`{{events-table-container/events-table}}`);
    assert.ok(find('.rsa-loader'), 'spinner present');
    assert.equal(find('.rsa-loader').textContent.trim(), 'Loading', 'Displays the correct message on the spinner');
  });

  test('if events are streaming and the query was re-executed by changing column group, a spinner is displayed with following message', async function(assert) {
    new ReduxDataHelper(setState)
      .isQueryExecutedByColumnGroup()
      .eventResultsStatus('between-streams')
      .getColumns('SUMMARY', EventColumnGroups)
      .language()
      .eventsPreferencesConfig()
      .build();

    await render(hbs`{{events-table-container/events-table}}`);
    assert.equal(find('.rsa-loader').textContent.trim(), 'Query is being re-executed to fetch new data.', 'Displaying the correct message');
  });

  test('if events are streaming and the query was re-executed by changing sort, a spinner is displayed with following message', async function(assert) {
    new ReduxDataHelper(setState)
      .isQueryExecutedBySort()
      .eventResultsStatus('between-streams')
      .getColumns('SUMMARY', EventColumnGroups)
      .language()
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
      .language()
      .getColumns('SUMMARY', EventColumnGroups)
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
      .getColumns('SUMMARY', EventColumnGroups)
      .eventCount(2)
      .streamLimit(100)
      .eventsPreferencesConfig()
      .language()
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
    assert.equal(findAll('.rsa-data-table-header .rsa-form-checkbox-label.disabled').length, 0, 'Status - cancelled: Render enabled selectAll checkbox label wrapper');
    assert.equal(findAll('.rsa-data-table-header .rsa-form-checkbox.disabled').length, 0, 'Status - cancelled: selectAll checkbox is enabled when all results are loaded in cancelling a query in between');
    assert.equal(findAll('.rsa-data-table-body .rsa-form-checkbox-label').length, 1, 'Individual row selection checkbox available for the 1 event loaded');
  });

  test('if an error is received, but some results have returned, a message is displayed', async function(assert) {
    new ReduxDataHelper(setState)
      .isEventResultsError(true, 'error')
      .eventCount(2)
      .streamLimit(100)
      .eventsPreferencesConfig()
      .getColumns('SUMMARY', EventColumnGroups)
      .language()
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

  skip('when a row is clicked selectEvent fired', async function(assert) {
    assert.expect(2);
    new ReduxDataHelper(setState)
      .eventCount(1)
      .eventsPreferencesConfig()
      .getColumns('SUMMARY', EventColumnGroups)
      .language()
      .eventResults([{ sessionId: 'foo', time: 123 }])
      .build();

    this.set('selectEvent', () => {
      assert.ok(true);
    });

    await render(hbs`{{events-table-container/events-table selectEvent=selectEvent}}`);
    assert.ok(findAll('.rsa-data-table-body-row').length > 0, 'a row is rendered');
    await click('.rsa-data-table-body-row');
  });

  test('when a group label is clicked selectEvent is not fired', async function(assert) {
    assert.expect(1);
    new ReduxDataHelper(setState)
      .eventCount(2)
      .eventsPreferencesConfig()
      .getColumns('SUMMARY', EventColumnGroups)
      .language()
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
      .selectedColumnGroup('SUMMARY')
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
    assert.notOk(find('h2[title=\'Collection Time\'] .js-move-handle')); // Collection time is not draggable
    assert.equal(findAll('.rsa-data-table-header-row div > h2 .sort-indicator .rsa-icon-arrow-up-7-filled').length, 2);
    assert.equal(findAll('.rsa-data-table-header-row div > h2 .sort-indicator.active .rsa-icon-arrow-up-7-filled').length, 1);
    assert.equal(findAll('.rsa-data-table-header-row div > h2 .sort-indicator:not(.active) .rsa-icon-arrow-up-7-filled').length, 1);
  });

  test('event table is displayed with expected ascending sort controls', async function(assert) {
    new ReduxDataHelper(setState)
      .selectedColumnGroup('SUMMARY')
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
    assert.ok(find('h2[title=\'Summary\'] .disabled-sort')); // Summary should always be disabled
    assert.ok(find('.rsa-data-table-header-row div > h2 .sort-indicator.active .rsa-icon-arrow-up-7-filled'));
  });

  test('event table header has sort disabled while streaming', async function(assert) {
    new ReduxDataHelper(setState)
      .selectedColumnGroup('SUMMARY')
      .hasRequiredValuesToQuery(true)
      .eventThreshold(100000)
      .eventsPreferencesConfig()
      .columnGroups(EventColumnGroups)
      .sortableColumns()
      .eventsQuerySort('time', 'Ascending')
      .language([{ format: 'TimeT', metaName: 'time', flags: -2147482605 }])
      .eventCount(100000)
      .eventResultsStatus('streaming')
      .build();

    await render(hbs`{{events-table-container/events-table}}`);
    assert.ok(find('h2[title=\'Collection Time\'] .disabled-sort'));
  });

  test('event table is displayed with expected descending sort controls', async function(assert) {
    new ReduxDataHelper(setState)
      .selectedColumnGroup('SUMMARY')
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
      .selectedColumnGroup('SUMMARY')
      .hasRequiredValuesToQuery(true)
      .language()
      .eventThreshold(100000)
      .eventsPreferencesConfig()
      .columnGroups(EventColumnGroups)
      .sortableColumns()
      .eventsQuerySort('time', 'Ascending')
      .eventCount(100000)
      .language([{ format: 'TimeT', metaName: 'time', flags: -2147482605 }])
      .eventResults()
      .eventResultsStatus('complete')
      .build();

    this.set('_toggleSort', (field, dir) => {
      assert.equal(field, 'time');
      assert.equal(dir, 'Descending');
    });

    await render(hbs`{{events-table-container/events-table _toggleSort=_toggleSort}}`);

    click('.rsa-data-table-header-row div > h2 .sort-indicator.active');
  });

  test('event table sort controls doesnt call _toggleSort if already sorting', async function(assert) {
    assert.expect(0);
    new ReduxDataHelper(setState)
      .selectedColumnGroup('SUMMARY')
      .hasRequiredValuesToQuery(true)
      .language()
      .eventThreshold(100000)
      .eventsPreferencesConfig()
      .columnGroups(EventColumnGroups)
      .sortableColumns()
      .eventsQuerySort('time', 'Ascending')
      .eventCount(100000)
      .language([{ format: 'TimeT', metaName: 'time', flags: -2147482605 }])
      .eventResults()
      .eventResultsStatus('sorting')
      .build();

    this.set('_toggleSort', () => {
      assert.ok(false);
    });

    await render(hbs`{{events-table-container/events-table _toggleSort=_toggleSort}}`);

    click('.rsa-data-table-header-row div > h2 .sort-indicator.active');
  });

  test('event table is displayed with expected column group\'s default header values', async function(assert) {
    new ReduxDataHelper(setState)
      .selectedColumnGroup('SUMMARY')
      .eventThreshold(100000)
      .language([{ metaName: 'time', format: 'TimeT' }, { metaName: 'medium', format: 'UInt8' }, { metaName: 'custom.meta-summary', format: 'Text' }])
      .eventsPreferencesConfig()
      .columnGroups(EventColumnGroups)
      .eventCount(100000)
      .build();

    await render(hbs`{{events-table-container/events-table}}`);
    const headerColumns = findAll('.rsa-data-table-header-row div > h2');
    assert.equal('COLLECTION TIME', headerColumns[0].innerText.trim(), 'Expected 1st column');
    assert.equal('TYPE', headerColumns[1].innerText.trim(), 'Expected 2nd column');
  });

  test('renders event selection checkboxes if only download permissions are present', async function(assert) {
    const accessControl = this.owner.lookup('service:accessControl');
    const origRoles = [...accessControl.roles];
    accessControl.set('roles', ['investigate-server.content.export']);
    new ReduxDataHelper(setState)
      .getColumns('SUMMARY', EventColumnGroups)
      .eventResults([{ sessionId: 'foo', time: 123 }, { sessionId: 'bar', time: 123 }])
      .language()
      .eventsPreferencesConfig()
      .build();

    await render(hbs`{{events-table-container/events-table}}`);

    assert.equal(findAll('.rsa-form-checkbox-label').length, 3, 'Renders event selection checkboxes when permission is present');
    // reset roles
    accessControl.set('roles', origRoles);
  });

  test('renders event selection checkboxes if manage incident permissions are present on respond and investigate both', async function(assert) {
    const accessControl = this.owner.lookup('service:accessControl');
    const origRoles = [...accessControl.roles];
    accessControl.set('roles', [
      'respond-server.incident.manage',
      'investigate-server.incident.manage'
    ]);
    new ReduxDataHelper(setState)
      .getColumns('SUMMARY', EventColumnGroups)
      .eventResults([{ sessionId: 'foo', time: 123 }, { sessionId: 'bar', time: 123 }])
      .language()
      .eventsPreferencesConfig()
      .build();

    await render(hbs`{{events-table-container/events-table}}`);
    assert.equal(findAll('.rsa-form-checkbox-label').length, 3, 'Renders event selection checkboxes when manage incident permission is present');
    // reset roles
    accessControl.set('roles', origRoles);
  });

  test('renders event selection checkboxes if manage incident permissions are present only on respond', async function(assert) {
    const accessControl = this.owner.lookup('service:accessControl');
    const origRoles = [...accessControl.roles];
    accessControl.set('roles', ['respond-server.incident.manage']);
    new ReduxDataHelper(setState)
      .getColumns('SUMMARY', EventColumnGroups)
      .eventResults([{ sessionId: 'foo', time: 123 }, { sessionId: 'bar', time: 123 }])
      .language()
      .eventsPreferencesConfig()
      .build();

    await render(hbs`{{events-table-container/events-table}}`);
    assert.equal(findAll('.rsa-form-checkbox-label').length, 0, 'Do not renders event selection checkboxes when manage incident permission is not present on investigate and respond both');
    // reset roles
    accessControl.set('roles', origRoles);
  });

  test('renders event selection checkboxes if both incident/download permissions are present', async function(assert) {
    new ReduxDataHelper(setState)
      .getColumns('SUMMARY', EventColumnGroups)
      .eventResults([{ sessionId: 'foo', time: 123 }, { sessionId: 'bar', time: 123 }])
      .language()
      .eventsPreferencesConfig()
      .build();

    await render(hbs`{{events-table-container/events-table}}`);
    assert.equal(findAll('.rsa-form-checkbox-label').length, 3, 'Renders event selection checkboxes when both permission are present');
    assert.equal(findAll('.rsa-data-table-header .rsa-form-checkbox-label').length, 1, 'Status - complete: Renders selectAll checkbox when all expected data loaded');
    assert.equal(findAll('.rsa-data-table-header .rsa-form-checkbox-label.disabled').length, 0, 'Status - complete: Render enabled selectAll checkbox label wrapper');
    assert.equal(findAll('.rsa-data-table-header .rsa-form-checkbox.disabled').length, 0, 'Status - complete: selectAll checkbox is enabled when all results are loaded in cancelling a query in between');
  });

  test('disables event selection checkboxes if no results found, even though permissions are present', async function(assert) {
    new ReduxDataHelper(setState)
      .getColumns('SUMMARY', EventColumnGroups)
      .eventResults([])
      .language()
      .eventsPreferencesConfig()
      .build();

    await render(hbs`{{events-table-container/events-table}}`);
    assert.equal(findAll('.rsa-data-table-header .rsa-form-checkbox-label.disabled').length, 1, 'Render disabled selectAll checkbox label wrapper');
    assert.equal(findAll('.rsa-form-checkbox.disabled').length, 1, 'Render disabled selectAll checkbox when both permission are present but there are 0 results');
  });

  test('does not render event selection checkboxes if permissions are not present', async function(assert) {
    const accessControl = this.owner.lookup('service:accessControl');
    const origRoles = [...accessControl.roles];
    accessControl.set('roles', []);
    new ReduxDataHelper(setState)
      .getColumns('SUMMARY', EventColumnGroups)
      .eventsPreferencesConfig()
      .language()
      .eventResults([{ sessionId: 'foo', time: 123 }, { sessionId: 'bar', time: 123 }])
      .build();

    await render(hbs`{{events-table-container/events-table}}`);

    assert.equal(findAll('.rsa-form-checkbox-label').length, 0, 'Does not render event selection checkboxes when neither permission is present');
    // reset roles
    accessControl.set('roles', origRoles);
  });

  test('If the search is still under way, the "Select All" checkbox is disabled', async function(assert) {
    new ReduxDataHelper(setState)
      .getColumns('SUMMARY', EventColumnGroups)
      .eventsPreferencesConfig()
      .eventResultsStatus('between-streams')
      .language()
      .eventResults([{ sessionId: 'foo', time: 123 }, { sessionId: 'bar', time: 123 }])
      .build();

    await render(hbs`{{events-table-container/events-table}}`);

    assert.equal(findAll('.rsa-data-table-header .rsa-form-checkbox-label.disabled').length, 1, 'Status - streaming/between-streams: Render disabled selectAll checkbox label wrapper');
    assert.equal(findAll('.rsa-data-table-header .rsa-form-checkbox.disabled').length, 1, 'Status - streaming/between-streams: Render disabled  selectAll checkbox');
    assert.equal(findAll('.rsa-data-table-body .rsa-form-checkbox-label').length, 2, 'Individual row selection checkboxes available for the 2 events loaded');
  });

  test('if no results are returned and there is a text filter, a message is displayed', async function(assert) {
    const textFilter = { type: 'text', searchTerm: 'limited' };
    new ReduxDataHelper(setState)
      .withPreviousQuery([textFilter])
      .getColumns('SUMMARY', EventColumnGroups)
      .eventCount(0)
      .streamLimit(100)
      .eventResults([])
      .language()
      .eventsPreferencesConfig()
      .build();

    await render(hbs`{{events-table-container/events-table}}`);
    assert.notOk(find('.rsa-loader'), 'spinner should not be present');
    assert.equal(find('.no-results-message').textContent.trim(),
      'Your filter criteria did not match any records. Results may be limited by a text filter, which matches only indexed meta keys.',
      'incorrect message'
    );
  });
});
