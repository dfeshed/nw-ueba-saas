import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import { click, find, findAll, render, triggerKeyEvent } from '@ember/test-helpers';
import { selectChoose, clickTrigger } from 'ember-power-select/test-support/helpers';
import EventColumnGroups from '../../../data/subscriptions/investigate-columns/data';
import KEY_MAP from 'investigate-events/util/keys';

let setState;

const ARROW_DOWN_KEY = KEY_MAP.arrowDown.code;

const columnGroupSelector = '.rsa-investigate-events-table__header__columnGroup';
const PS_TRIGGER = '.rsa-investigate-events-table__header__columnGroup .ember-power-select-trigger';
const PS_SELECTED_ITEM = '.rsa-investigate-events-table__header__columnGroup .ember-power-select-selected-item';

const assertForInvestigateColumnAndColumnSelector = async function(assert, headerCount, count, selectedOption, isNotEmptyRow) {
  await selectChoose(PS_TRIGGER, selectedOption);
  assert.equal(findAll('.rsa-data-table-header-cell').length, headerCount * (isNotEmptyRow ? 1 : 2), `Should show columns for ${selectedOption}.`);
  assert.equal(find(PS_SELECTED_ITEM).textContent.trim(), selectedOption, `Selected column group should be ${selectedOption}.`);
  await click('.rsa-icon-cog-filled');
  assert.equal(findAll('li .rsa-form-checkbox-label').length, count, `Should show all columns for column selector for ${selectedOption}.`);
};

const renderDefaultEventTable = async function() {
  new ReduxDataHelper(setState)
    .columnGroup('SUMMARY')
    .reconSize('max')
    .eventsPreferencesConfig()
    .columnGroups(EventColumnGroups)
    .build();

  await render(hbs`{{events-table-container}}`);
};

const eventResultsData = [
  { medium: 32, time: +(new Date()), size: 13191, custom: { 'meta-summary': 'bar' }, 'has.alias': 'raw-value' }
];

module('Integration | Component | events-table-container', function(hooks) {
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
  });

  test('it renders default table', async function(assert) {
    await renderDefaultEventTable();
    assert.equal(findAll('.rsa-investigate-events-table').length, 1);
    assert.equal(findAll('.rsa-icon-cog-filled').length, 1, 'There should be a column selector icon.');
  });

  test('it should show columns for Email Analysis', async function(assert) {
    await renderDefaultEventTable();
    // TODO bring download back. 16 with checkbox
    await assertForInvestigateColumnAndColumnSelector(assert, 15, 41, 'Email Analysis');
  });

  test('it should show columns for Malware Analysis', async function(assert) {
    await renderDefaultEventTable();
    // TODO bring download back. 16 with checkbox
    await assertForInvestigateColumnAndColumnSelector(assert, 15, 27, 'Malware Analysis');
  });

  test('it should show columns for Threat Analysis', async function(assert) {
    await renderDefaultEventTable();
    // TODO bring download back. 16 with checkbox
    await assertForInvestigateColumnAndColumnSelector(assert, 15, 57, 'Threat Analysis');
  });

  test('it should show columns for Web Analysis', async function(assert) {
    await renderDefaultEventTable();
    // TODO bring download back. 16 with checkbox
    await assertForInvestigateColumnAndColumnSelector(assert, 15, 53, 'Web Analysis');
  });

  test('it should show columns for Endpoint Analysis', async function(assert) {
    await renderDefaultEventTable();
    // TODO bring download back. 16 with checkbox
    await assertForInvestigateColumnAndColumnSelector(assert, 15, 32, 'Endpoint Analysis');
  });

  test('it should show "no results" message only if there are zero results', async function(assert) {
    new ReduxDataHelper(setState)
      .columnGroup('SUMMARY')
      .columnGroups(EventColumnGroups)
      .eventsPreferencesConfig()
      .eventResults([])
      .build();

    await render(hbs`{{events-table-container}}`);

    assert.ok(findAll('.rsa-panel-message .message'), 'Message shown');
    assert.equal(find('.rsa-panel-message .message').textContent.trim(), 'Your filter criteria did not match any records.');
  });

  test('it should not show "no results" message if there are results', async function(assert) {
    new ReduxDataHelper(setState)
      .columnGroup('SUMMARY')
      .columnGroups(EventColumnGroups)
      .eventsPreferencesConfig()
      .eventResults(['something'])
      .build();

    await render(hbs`{{events-table-container}}`);

    assert.notOk(find('.rsa-panel-message .message'), 'Message not shown');
  });

  test('keyDown will trigger event selection if dropdown not in view', async function(assert) {

    new ReduxDataHelper(setState)
      .allEventsSelected(true)
      .getColumns('SUMMARY', EventColumnGroups)
      .isEventResultsError(false)
      .eventsPreferencesConfig()
      .defaultEventAnalysisPreferences()
      .eventResults(eventResultsData)
      .build();

    let eventSelected = false;
    this.set('handleSelectEvent', () => {
      eventSelected = true;
    });

    await render(hbs` {{events-table-container selectEvent=handleSelectEvent}}`);
    await triggerKeyEvent('.rsa-data-table', 'keyup', ARROW_DOWN_KEY);
    assert.ok(eventSelected, 'Keystroke triggers event selection when dropdown in view');
  });

  test('keyDown will not trigger event selection if dropdown in view', async function(assert) {

    new ReduxDataHelper(setState)
      .allEventsSelected(true)
      .getColumns('SUMMARY', EventColumnGroups)
      .isEventResultsError(false)
      .eventsPreferencesConfig()
      .defaultEventAnalysisPreferences()
      .eventResults(eventResultsData)
      .build();

    let eventSelected = false;
    this.set('handleSelectEvent', () => {
      eventSelected = true;
    });

    await render(hbs` {{events-table-container selectEvent=handleSelectEvent}}`);
    await clickTrigger(columnGroupSelector);
    await triggerKeyEvent(PS_TRIGGER, 'keyup', ARROW_DOWN_KEY);
    assert.notOk(eventSelected, 'Keystroke does not trigger event selection when dropdown in view');
  });

  test('if events have begun streaming, the progress bar is present', async function(assert) {
    new ReduxDataHelper(setState)
      .columnGroup('SUMMARY')
      .columnGroups(EventColumnGroups)
      .eventsPreferencesConfig()
      .eventResultsStatus('streaming')
      .eventResults([])
      .build();

    await render(hbs`{{events-table-container}}`);
    assert.ok(find('.rsa-progress-bar'), 'progress bar is present');
  });

  test('if events are between streams, the progress bar is present', async function(assert) {
    new ReduxDataHelper(setState)
      .columnGroup('SUMMARY')
      .columnGroups(EventColumnGroups)
      .eventsPreferencesConfig()
      .eventResultsStatus('between-streams')
      .eventResults([])
      .build();

    await render(hbs`{{events-table-container}}`);
    assert.ok(find('.rsa-progress-bar'), 'progress bar is present');
  });

  test('if event streaming has completed, the progress bar is not present', async function(assert) {
    new ReduxDataHelper(setState)
      .columnGroup('SUMMARY')
      .columnGroups(EventColumnGroups)
      .eventsPreferencesConfig()
      .eventResultsStatus('complete')
      .eventResults([])
      .build();

    await render(hbs`{{events-table-container}}`);
    assert.notOk(find('.rsa-progress-bar'), 'progress bar is not present');
  });

});

