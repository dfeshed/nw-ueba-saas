import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { find, render } from '@ember/test-helpers';
import EventColumnGroups from '../../../../data/subscriptions/investigate-columns/data';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';

let setState;

const eventResultsData = [
  { medium: 32, time: +(new Date()), size: 13191, custom: { 'meta-summary': 'bar' }, 'has.alias': 'raw-value' }
];

const headerContainerSelector = '.rsa-investigate-events-table__header__container';
const headerContentSelector = '.rsa-investigate-events-table__header__content';
const eventLabelSelector = '.rsa-investigate-events-table__header__eventLabel';
const columnGroupManager = '.rsa-investigate-events-table__header__columnGroups';
const downloadSelector = '.rsa-investigate-events-table__header__downloadEvents';
const createIncidentSelector = '.create-incident-button .rsa-form-button-wrapper';
const addToIncidentSelector = '.add-to-incident-button .rsa-form-button-wrapper';
const textSearchSelector = '.rsa-data-table-header__search-selector';

const renderDefaultHeaderContainer = async(assert) => {
  new ReduxDataHelper(setState).columnGroup('SUMMARY').reconSize('max').eventTimeSortOrder().eventsPreferencesConfig().eventsQuerySort('time', 'Ascending').hasRequiredValuesToQuery(true).columnGroups(EventColumnGroups).eventCount(55).build();
  await render(hbs`{{events-table-container/header-container}}`);
  assert.ok(find(headerContainerSelector), 'Header container rendered');
};

module('Integration | Component | header-container', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('hasInvestigateContentExportAccess', true);
    // both respondCanManageIncidents && investigateCanManageIncidents need to be true to enable incident management
    accessControl.set('respondCanManageIncidents', true);
    accessControl.set('investigateCanManageIncidents', true);
  });

  test('render the events header with data at threshold', async function(assert) {
    new ReduxDataHelper(setState).columnGroup('SUMMARY').reconSize('max').eventThreshold(100000).eventTimeSortOrder().eventsPreferencesConfig().eventsQuerySort('time', 'Ascending').columnGroups(EventColumnGroups).hasRequiredValuesToQuery(true).eventCount(100000).build();
    await render(hbs`{{events-table-container/header-container}}`);
    assert.equal(find(eventLabelSelector).textContent.trim().replace(/\s+/g, ''), 'oldest100,000Events(Asc)', 'rendered event header title');
    const thresholdIconSelector = `${headerContainerSelector} .at-threshold`;
    assert.ok(find(thresholdIconSelector), 'at threshold icon is present');
    const tooltip = find(thresholdIconSelector).getAttribute('title').trim().split(' ');
    assert.ok(tooltip.includes('100,000'));
    assert.ok(tooltip.includes('oldest'));
  });

  test('render the events header with actualCount when canceled', async function(assert) {
    new ReduxDataHelper(setState).columnGroup('SUMMARY').reconSize('max').isCanceled().eventThreshold(100000).eventTimeSortOrder().eventsPreferencesConfig().eventsQuerySort('time', 'Ascending').selectedEventIds([]).columnGroups(EventColumnGroups).eventResults(eventResultsData).hasRequiredValuesToQuery(true).eventCount(10).build();
    await render(hbs`{{events-table-container/header-container}}`);
    assert.equal(find(eventLabelSelector).textContent.trim().replace(/\s+/g, ''), '1Events(Asc)', 'rendered event header title');
  });

  test('render the events header with eventTimeSortOrderPreferenceWhenQueried when cannot sort', async function(assert) {
    new ReduxDataHelper(setState).columnGroup('SUMMARY').reconSize('max').isCanceled().eventThreshold(100000).eventTimeSortOrder().eventsPreferencesConfig().eventsQuerySort('time', 'Ascending').selectedEventIds([]).columnGroups(EventColumnGroups).eventResults(eventResultsData).hasRequiredValuesToQuery(false).eventTimeSortOrderPreferenceWhenQueried().eventCount(10).build();
    await render(hbs`{{events-table-container/header-container}}`);
    assert.equal(find(eventLabelSelector).textContent.trim().replace(/\s+/g, ''), '1Events(Asc)', 'rendered event header title');
  });

  test('render the events header with required fields ', async function(assert) {
    await renderDefaultHeaderContainer(assert);
    assert.equal(find(headerContainerSelector).childElementCount, 2, 'rendered with two elements - header__content, events-table-control');
    assert.equal(find(headerContentSelector).childElementCount, 4, 'rendered with 4 elements - eventLabel, columnGroups, downloadEvents, manageIncidents');
    assert.equal(find(eventLabelSelector).textContent.trim().replace(/\s+/g, ''), '55Events(Asc)', 'rendered event header title');
    assert.equal(find(`${columnGroupManager} .list-caption`).textContent.trim(), 'Column Group: Summary List', 'rendered ColumnGroup title');
    assert.equal(find(`${downloadSelector} .ember-power-select-trigger span[title='Download']`).textContent.trim(), 'Download', 'rendered event header title');
    assert.equal(find(`${headerContentSelector} .create-incident-button`).textContent.trim(), 'Create Incident', 'rendered Create Incident title');
    assert.equal(find(`${headerContentSelector} .add-to-incident-button`).textContent.trim(), 'Add to Incident', 'rendered Create Incident title');
    assert.ok(find(`${textSearchSelector}.disabled`), 'rendered event header text search');
  });

  test('enables the search selector when loading is complete', async function(assert) {
    new ReduxDataHelper(setState).columnGroup('SUMMARY').eventTimeSortOrder().eventsPreferencesConfig().eventsQuerySort('time', 'Ascending').columnGroups(EventColumnGroups).eventCount(55).queryStatsIsComplete().eventResultsStatus('complete').build();
    await render(hbs`{{events-table-container/header-container}}`);
    assert.ok(find(`${textSearchSelector}`), 'rendered event header text search');
    assert.notOk(find(`${textSearchSelector}.disabled`), 'enabled text search');
  });

  test('disables the search selector when custom.meta-summary is present', async function(assert) {
    const columnGroups = [{
      id: 'SUMMARY',
      name: 'Summary',
      columns: [{
        field: 'custom.meta-summary',
        title: 'Summary'
      }]
    }];

    new ReduxDataHelper(setState).columnGroup('SUMMARY').eventTimeSortOrder().eventsPreferencesConfig().eventsQuerySort('time', 'Ascending').columnGroups(columnGroups).eventCount(55).queryStatsIsComplete().eventResultsStatus('complete').visibleColumns([{ field: 'custom.meta-summary' }]).build();
    await render(hbs`{{events-table-container/header-container}}`);
    assert.ok(find(`${textSearchSelector}.disabled`), 'disabled text search');
  });

  test('Create Incident/Add to Incident buttons should be disabled if there are no selected events', async function(assert) {
    new ReduxDataHelper(setState)
      .selectedEventIds({})
      .build();

    await render(hbs`{{events-table-container/header-container}}`);

    assert.ok(find(`${createIncidentSelector}.is-disabled`), 'Create Incident button is disabled');
    assert.ok(find(`${addToIncidentSelector}.is-disabled`), 'Add to Incident button is disabled');
  });

  test('Create Incident/Add to Incident buttons should be enabled if 1+ events are checked', async function(assert) {
    new ReduxDataHelper(setState)
      .withSelectedEventIds()
      .build();

    await render(hbs`{{events-table-container/header-container}}`);

    assert.ok(find(`${createIncidentSelector}`), 'Create Incident button is present');
    assert.notOk(find(`${createIncidentSelector}.is-disabled`), 'Create Incident button is enabled');

    assert.ok(find(`${addToIncidentSelector}`), 'Add to Incident button is present');
    assert.notOk(find(`${addToIncidentSelector}.is-disabled`), 'Add to Incident button is enabled');
  });

  test('Create Incident/Add to Incident buttons should be visible if user has permissions', async function(assert) {
    new ReduxDataHelper(setState)
      .withSelectedEventIds()
      .build();

    await render(hbs`{{events-table-container/header-container}}`);

    assert.equal(find(headerContentSelector).childElementCount, 4, '__eventLabel, __columngroup, _downloadEvents, __manageIncident visible.');

    assert.ok(find(createIncidentSelector), 'Create Incident button is visible');
    assert.ok(find(addToIncidentSelector), 'Add to Incident button is visible');
  });

  test('Create Incident/Add to Incident buttons should not be displayed, if user does not have manage incident permissions on respond and investigate both', async function(assert) {
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('respondCanManageIncidents', false);
    accessControl.set('investigateCanManageIncidents', false);

    new ReduxDataHelper(setState)
      .withSelectedEventIds()
      .build();

    await render(hbs`{{events-table-container/header-container}}`);

    assert.equal(find(headerContentSelector).childElementCount, 3, 'only __eventLabel, __columngroup, _downloadEvents visible.');

    assert.notOk(find(createIncidentSelector), 'Create Incident button is not displayed');
    assert.notOk(find(addToIncidentSelector), 'Add to Incident button is not displayed');
  });

  test('Create Incident/Add to Incident buttons should not be displayed, if user does not have manage incident permissions only on investigate or respond', async function(assert) {
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('respondCanManageIncidents', true);
    accessControl.set('investigateCanManageIncidents', false);
    new ReduxDataHelper(setState)
      .withSelectedEventIds()
      .build();

    await render(hbs`{{events-table-container/header-container}}`);

    assert.equal(find(headerContentSelector).childElementCount, 3, 'only __eventLabel, __columngroup, _downloadEvents visible.');

    assert.notOk(find(createIncidentSelector), 'Create Incident button is not displayed');
    assert.notOk(find(addToIncidentSelector), 'Add to Incident button is not displayed');
  });

});
