import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { find, findAll, render } from '@ember/test-helpers';
import { clickTrigger } from 'ember-power-select/test-support/helpers';

const downloadSelector = '.rsa-investigate-events-table__header__downloadEvents';
const downloadTitle = `${downloadSelector} span span`;
const downloadOptions = '.ember-power-select-options';

let setState;

const eventResultsData = [
  { sessionId: 1, medium: 1 },
  { sessionId: 2, medium: 1 },
  { sessionId: 3, medium: 32 }
];

const assertForDownloadOptions = async function(assert, options, index, value, count) {
  assert.equal(options[index].children[0].textContent.trim(), value);
  assert.equal(options[index].children[1].textContent.trim(), count);
};

module('Integration | Component | Download Dropdown', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('hasInvestigateContentExportAccess', true);
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  test('download option should be visible if user has permissions', async function(assert) {
    new ReduxDataHelper(setState).allEventsSelected(false).withSelectedEventIds().build();
    await render(hbs`{{events-table-container/header-container/download-dropdown}}`);
    assert.ok(find(downloadSelector), 'Download option present');
  });

  test('download dropdown should be hidden if missing permissions', async function(assert) {
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('hasInvestigateContentExportAccess', false);
    new ReduxDataHelper(setState)
      .allEventsSelected(false)
      .withSelectedEventIds()
      .build();
    await render(hbs`{{events-table-container/header-container/download-dropdown}}`);
    assert.notOk(find(`${downloadSelector} .ember-power-select`), 'Download option not present');
  });

  test('download dropdown should read Download All if selectAll is checked', async function(assert) {
    new ReduxDataHelper(setState)
      .allEventsSelected(true)
      .build();
    await render(hbs`{{events-table-container/header-container/download-dropdown}}`);
    assert.equal(findAll(downloadTitle)[0].textContent.trim(), 'Download All', 'Download dropdown should read `Download All` if selectAll is checked');
  });

  test('download dropdown should read Download if selectAll is not checked', async function(assert) {
    new ReduxDataHelper(setState)
      .allEventsSelected(false)
      .withSelectedEventIds()
      .build();
    await render(hbs`{{events-table-container/header-container/download-dropdown}}`);
    assert.equal(findAll(downloadTitle)[0].textContent.trim(), 'Download', 'Download dropdown should read `Download` if selectAll is not checked');
  });

  test('download dropdown should be enabled if all or 1+ events are selected ', async function(assert) {
    new ReduxDataHelper(setState)
      .allEventsSelected(false)
      .withSelectedEventIds()
      .build();
    await render(hbs`{{events-table-container/header-container/download-dropdown}}`);
    assert.notOk(find(`${downloadSelector}.is-disabled`), 'Download is enabled');
  });

  test('download dropdown should be disabled if no events are selected ', async function(assert) {
    new ReduxDataHelper(setState)
      .allEventsSelected(false)
      .build();
    await render(hbs`{{events-table-container/header-container/download-dropdown}}`);
    assert.ok(find(`${downloadSelector}.is-disabled`), 'Download is disabled');
  });

  test('download dropdown should show valid options and no counts for selectAllEvents', async function(assert) {
    new ReduxDataHelper(setState)
      .allEventsSelected(true)
      .isEventResultsError(false)
      .eventsPreferencesConfig()
      .setEventAnalysisPreferencesForDownload('CSV', 'PAYLOAD1', 'TSV')
      .eventResults(eventResultsData)
      .build();
    await render(hbs`{{events-table-container/header-container/download-dropdown}}`);
    await clickTrigger();
    const options = findAll(`${downloadOptions} li`);

    assert.equal(options.length, 9, '9 options found, only 1 of 4 Network download options available');

    // Log download option in prefered section of download displays user preference
    await assertForDownloadOptions(assert, options, 0, 'Logs as CSV', '');
    // Network download option in prefered section of download does not update to user preference, remains equal to PCAP always
    await assertForDownloadOptions(assert, options, 1, 'Network as PCAP', '');
    // Meta  download option in prefered section of download displays user preference
    await assertForDownloadOptions(assert, options, 2, 'Visible Meta as TSV', '');
  });

  test('download dropdown should show appropriate options if log and Network events are selected ', async function(assert) {
    new ReduxDataHelper(setState)
      .allEventsSelected(false)
      .isEventResultsError(false)
      .eventsPreferencesConfig()
      .defaultEventAnalysisPreferences()
      .eventResults(eventResultsData)
      .selectedEventIds([1, 3])
      .build();
    await render(hbs`{{events-table-container/header-container/download-dropdown}}`);
    await clickTrigger();
    const options = findAll(`${downloadOptions} li`);
    await assertForDownloadOptions(assert, options, 0, 'Logs as Text', '1/2');
    await assertForDownloadOptions(assert, options, 1, 'Network as PCAP', '1/2');
    await assertForDownloadOptions(assert, options, 2, 'Visible Meta as Text', '2/2');
  });

  test('download dropdown should show appropriate options if only Network events are selected ', async function(assert) {
    new ReduxDataHelper(setState)
      .allEventsSelected(false)
      .isEventResultsError(false)
      .eventsPreferencesConfig()
      .defaultEventAnalysisPreferences()
      .eventResults(eventResultsData)
      .selectedEventIds([1, 2])
      .build();
    await render(hbs`{{events-table-container/header-container/download-dropdown}}`);
    await clickTrigger();
    const options = findAll(`${downloadOptions} li`);
    await assertForDownloadOptions(assert, options, 0, 'Logs as Text', '0/2');
    await assertForDownloadOptions(assert, options, 1, 'Network as PCAP', '2/2');
    await assertForDownloadOptions(assert, options, 2, 'Visible Meta as Text', '2/2');
  });

});
