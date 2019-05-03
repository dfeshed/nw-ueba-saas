import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { find, findAll, render } from '@ember/test-helpers';
import { clickTrigger } from 'ember-power-select/test-support/helpers';
import EventColumnGroups from '../../../../data/subscriptions/investigate-columns/data';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';

let setState;

const eventResultsData = [
  { medium: 32, time: +(new Date()), size: 13191, custom: { 'meta-summary': 'bar' }, 'has.alias': 'raw-value' }
];

const columnSelector = '.rsa-investigate-events-table__header__columnGroup .ember-power-select-selected-item';
const createIncidentSelector = '.create-incident-button';
const addToIncidentSelector = '.add-to-incident-button';

const renderDefaultHeaderContainer = async(assert) => {
  new ReduxDataHelper(setState).columnGroup('SUMMARY').reconSize('max').eventTimeSortOrder().eventsPreferencesConfig().columnGroups(EventColumnGroups).eventCount(55).build();
  await render(hbs`{{events-table-container/header-container}}`);
  assert.equal(findAll('.ember-power-select-trigger').length, 2, 'columnGroup, downloadEvents');
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
    accessControl.set('respondCanManageIncidents', true);
  });

  test('render the events header with data at threshold', async function(assert) {
    new ReduxDataHelper(setState).columnGroup('SUMMARY').reconSize('max').eventThreshold(100000).eventTimeSortOrder().eventsPreferencesConfig().columnGroups(EventColumnGroups).eventCount(100000).build();
    await render(hbs`{{events-table-container/header-container}}`);
    assert.equal(find('.rsa-investigate-events-table__header__eventLabel').textContent.trim().replace(/\s+/g, ''), 'oldest100,000Events(Asc)', 'rendered event header title');
    assert.ok(find('.rsa-investigate-events-table__header__container .at-threshold'), 'at threshold icon is present');
    const tooltip = find('.rsa-investigate-events-table__header__container .at-threshold').getAttribute('title').trim().split(' ');
    assert.ok(tooltip.includes('100,000'));
    assert.ok(tooltip.includes('oldest'));
  });

  test('render the events header with actualCount when canceled', async function(assert) {
    new ReduxDataHelper(setState).columnGroup('SUMMARY').reconSize('max').isCanceled().eventThreshold(100000).eventTimeSortOrder().eventsPreferencesConfig().selectedEventIds([]).columnGroups(EventColumnGroups).eventResults(eventResultsData).eventCount(10).build();
    await render(hbs`{{events-table-container/header-container}}`);
    assert.equal(find('.rsa-investigate-events-table__header__eventLabel').textContent.trim().replace(/\s+/g, ''), '1Events(Asc)', 'rendered event header title');
  });

  test('render the events header with required fields ', async function(assert) {
    await renderDefaultHeaderContainer(assert);
    assert.ok(find('.rsa-investigate-events-table__header__container'), 'render event header container');
    assert.equal(find('.rsa-investigate-events-table__header__container').childElementCount, 2, 'rendered with two elements - header__content, events-table-control');
    assert.equal(find('.rsa-investigate-events-table__header__content').childElementCount, 4, 'rendered with 4 elements - eventLabel, columnGroup, downloadEvents, manageIncident');
    assert.equal(find('.rsa-investigate-events-table__header__eventLabel').textContent.trim().replace(/\s+/g, ''), '55Events(Asc)', 'rendered event header title');
    assert.equal(find('.rsa-investigate-events-table__header__columnGroup span').textContent.trim(), 'Summary List', 'rendered event header title');
    assert.equal(find('.rsa-investigate-events-table__header__downloadEvents span').textContent.trim(), 'Download', 'rendered event header title');
    assert.ok(find('.rsa-data-table-header__search-selector'), 'rendered event header text search');
  });

  test('it provides option to select column groups', async function(assert) {
    await renderDefaultHeaderContainer(assert);
    assert.equal(find(columnSelector).textContent.trim(), 'Summary List', 'Default Column group is Summary List.');
    await clickTrigger();
    const options = findAll('.ember-power-select-option').map((d) => d.textContent.trim());
    assert.equal(options.join('').replace(/\s+/g, ''), 'Custom1Custom2SummaryListSummaryListSummaryListSummaryListEmailAnalysisMalwareAnalysisThreatAnalysisWebAnalysisEndpointAnalysis');
    assert.equal(findAll('.ember-power-select-group').length, 2, 'render two column groups');
    assert.equal(findAll('.ember-power-select-group-name')[0].textContent.trim(), 'Custom Column Groups', 'render custom column group');
    assert.equal(findAll('.ember-power-select-group-name')[1].textContent.trim(), 'Default Column Groups', 'render default column group');
    assert.equal(find('.ember-power-select-group-name').getAttribute('title'), 'Manage Custom Column Groups in Events List');
  });

  test('it provides option for search filter', async function(assert) {
    await renderDefaultHeaderContainer(assert);
    await clickTrigger();
    assert.ok(find('.ember-power-select-search'), 'Show search filter option in drop down');
  });

  test('persisted column group is preselected in the drop down', async function(assert) {
    new ReduxDataHelper(setState).columnGroup('MALWARE').eventsPreferencesConfig().eventTimeSortOrder().columnGroups(EventColumnGroups).build();
    await render(hbs`{{events-table-container/header-container}}`);
    // return waitFor(columnSelector).then(() => {
    assert.equal(find(columnSelector).textContent.trim(), 'Malware Analysis', 'Expected Malware Analysis to be selected');
    // });
  });

  test('Create Incident/Add to Incident buttons should be disabled if none of the rows are checked', async function(assert) {
    new ReduxDataHelper(setState)
      .allEventsSelected(false)
      .build();
    await render(hbs`{{incident-toolbar}}`);
    assert.ok(findAll(`${createIncidentSelector}.is-disabled`), 'Create Incident button is disabled');
    assert.ok(findAll(`${addToIncidentSelector}.is-disabled`), 'Add to Incident button is disabled');
  });

  test('Create Incident/Add to Incident buttons should be enabled if selectAll is checked', async function(assert) {
    new ReduxDataHelper(setState)
      .allEventsSelected(true)
      .build();
    await render(hbs`{{incident-toolbar}}`);
    assert.notOk(find(`${createIncidentSelector}.is-disabled`), 'Create Incident button is enabled');
    assert.notOk(find(`${addToIncidentSelector}.is-disabled`), 'Add to Incident button is enabled');
  });

  test('Create Incident/Add to Incident buttons should be enabled if all or 1+ events are selected ', async function(assert) {
    new ReduxDataHelper(setState)
      .allEventsSelected(false)
      .withSelectedEventIds()
      .build();
    await render(hbs`{{incident-toolbar}}`);
    assert.notOk(find(`${createIncidentSelector}.is-disabled`), 'Create Incident is enabled');
    assert.notOk(find(`${addToIncidentSelector}.is-disabled`), 'Add to Incident is enabled');
  });

  test('Create Incident/Add to Incident buttons should be visible if user has permissions', async function(assert) {
    new ReduxDataHelper(setState)
      .allEventsSelected(false)
      .withSelectedEventIds()
      .build();
    await render(hbs`{{incident-toolbar}}`);
    assert.ok(find(createIncidentSelector), 'Create Incident button is visible');
    assert.ok(find(addToIncidentSelector), 'Add to Incident button is visible');
  });

  test('Create Incident/Add to Incident buttons should be hidden if missing permissions', async function(assert) {
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('respondCanManageIncidents', false);
    new ReduxDataHelper(setState)
      .allEventsSelected(false)
      .withSelectedEventIds()
      .build();
    assert.notOk(find(createIncidentSelector), 'Create Incident button is not visible');
    assert.notOk(find(addToIncidentSelector), 'Add to Incident button is not visible');
  });

});
