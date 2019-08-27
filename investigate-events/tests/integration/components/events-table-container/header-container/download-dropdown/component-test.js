import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { patchFlash } from '../../../../../helpers/patch-flash';
import { find, findAll, render } from '@ember/test-helpers';
import { clickTrigger } from 'ember-power-select/test-support/helpers';
import * as notificationCreators from 'investigate-events/actions/notification-creators';
import sinon from 'sinon';

const downloadSelector = '.rsa-investigate-events-table__header__downloadEvents';
const downloadPowerSelect = `${downloadSelector} .power-select`;
const downloadTitle = `${downloadSelector} span span`;
const downloadLoader = `${downloadSelector} .rsa-loader`;
const downloadOptions = '.ember-power-select-options';

let setState;

const eventResultsData = [
  { sessionId: 101, medium: 1 },
  { sessionId: 102, medium: 1 },
  { sessionId: 103, medium: 32 }
];

const assertForDownloadOptions = async function(assert, options, index, value, count) {
  assert.equal(options[index].children[0].textContent.trim(), value);
  assert.equal(options[index].children[1].textContent.trim(), count);
};

const didDownloadCreatorsStub = sinon.stub(notificationCreators, 'didDownloadFiles');

module('Integration | Component | Download Dropdown', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {

    this.owner.inject('component', 'flashMessages', 'service:flashMessages', 'i18n', 'service:i18n');
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  hooks.afterEach(function() {
    didDownloadCreatorsStub.resetHistory();
  });

  hooks.after(function() {
    didDownloadCreatorsStub.restore();
  });

  test('download option should be visible if user has permissions', async function(assert) {
    new ReduxDataHelper(setState)
      .eventResults(eventResultsData)
      .selectedEventIds({ 0: 101 })
      .build();
    await render(hbs`{{events-table-container/header-container/download-dropdown}}`);
    assert.ok(find(downloadPowerSelect), 'Download option present');
  });

  test('download dropdown should be hidden if missing permissions', async function(assert) {
    const accessControl = this.owner.lookup('service:accessControl');
    const origRoles = [...accessControl.roles];
    accessControl.set('roles', []);
    new ReduxDataHelper(setState)
      .eventResults(eventResultsData)
      .selectedEventIds({ 0: 101 })
      .build();
    await render(hbs`{{events-table-container/header-container/download-dropdown}}`);
    assert.notOk(find(downloadPowerSelect), 'Download option not present');
    // reset roles
    accessControl.set('roles', origRoles);
  });

  test('download dropdown should be disabled & read Download if nothing is checked', async function(assert) {
    new ReduxDataHelper(setState)
      .eventResults(eventResultsData)
      .selectedEventIds({})
      .build();
    await render(hbs`{{events-table-container/header-container/download-dropdown}}`);
    assert.ok(find(`${downloadSelector}.is-disabled`), 'Download is disabled');
    assert.equal(findAll(downloadTitle)[0].textContent.trim(), 'Download', 'Download dropdown should read `Download` if selectAll is not checked');
  });

  test('download dropdown should be enabled & read Download if 1+ and not all events are selected ', async function(assert) {
    new ReduxDataHelper(setState)
      .eventResults(eventResultsData)
      .selectedEventIds({ 0: 101, 1: 102 })
      .build();
    await render(hbs`{{events-table-container/header-container/download-dropdown}}`);
    assert.notOk(find(`${downloadSelector}.is-disabled`), 'Download is enabled');
    assert.equal(findAll(downloadTitle)[0].textContent.trim(), 'Download', 'Download dropdown should read `Download` if selectAll is not checked');
  });

  test('download dropdown should be enabled & read Download All if all events are selected', async function(assert) {
    new ReduxDataHelper(setState)
      .eventResults(eventResultsData)
      .selectedEventIds({ 0: 101, 1: 102, 2: 103 })
      .build();
    await render(hbs`{{events-table-container/header-container/download-dropdown}}`);
    assert.notOk(find(`${downloadSelector}.is-disabled`), 'Download is enabled');
    assert.equal(findAll(downloadTitle)[0].textContent.trim(), 'Download All', 'Download dropdown should read `Download All` if selectAll is checked');
  });

  test('dropdown should be disabled & show correct label when downloading', async function(assert) {
    new ReduxDataHelper(setState)
      .eventResults(eventResultsData)
      .selectedEventIds({ 0: 101, 1: 102, 2: 103 })
      .setFileExtractStatus('wait')
      .build();
    await render(hbs`{{events-table-container/header-container/download-dropdown}}`);
    assert.ok(findAll(downloadLoader).length == 1, 'Loading icon present');
    assert.equal(findAll(downloadTitle)[0].textContent.trim(), 'Downloading...', 'Download dropdown should indicate Downloading when download in progress');
    assert.ok(find(`${downloadSelector}.is-disabled`), 'Download is disabled');
  });

  test('download dropdown should show valid options with counts for selectAllEvents', async function(assert) {
    new ReduxDataHelper(setState)
      .eventsPreferencesConfig()
      .setEventAnalysisPreferencesForDownload('CSV', 'PAYLOAD1', 'TSV')
      .eventResults(eventResultsData)
      .selectedEventIds({ 0: 101, 1: 102, 2: 103 })
      .build();
    await render(hbs`{{events-table-container/header-container/download-dropdown}}`);
    await clickTrigger();
    // actual options inside the groups
    const options = findAll(`${downloadOptions} li ul li`);

    assert.equal(options.length, 9, '9 options found, only 1 of 4 Network download options available');

    // Log download option in prefered section of download displays user preference
    await assertForDownloadOptions(assert, options, 0, 'Logs as CSV', '1/3');
    // Network download option in prefered section of download does not update to user preference, remains equal to PCAP always
    await assertForDownloadOptions(assert, options, 1, 'Network as PCAP', '2/3');
    // Meta  download option in prefered section of download displays user preference
    await assertForDownloadOptions(assert, options, 2, 'Visible Meta as TSV', '3/3');
  });

  test('download dropdown should show appropriate options if log and Network events are selected ', async function(assert) {
    new ReduxDataHelper(setState)
      .isEventResultsError(false)
      .eventsPreferencesConfig()
      .defaultEventAnalysisPreferences()
      .eventResults(eventResultsData)
      .selectedEventIds({ 0: 101, 2: 103 })
      .build();
    await render(hbs`{{events-table-container/header-container/download-dropdown}}`);
    await clickTrigger();
    const options = findAll(`${downloadOptions} li ul li`);

    await assertForDownloadOptions(assert, options, 0, 'Logs as Text', '1/2');
    await assertForDownloadOptions(assert, options, 1, 'Network as PCAP', '1/2');
    await assertForDownloadOptions(assert, options, 2, 'Visible Meta as Text', '2/2');
  });

  test('download dropdown should show appropriate options if only Network events are selected ', async function(assert) {
    new ReduxDataHelper(setState)
      .isEventResultsError(false)
      .eventsPreferencesConfig()
      .defaultEventAnalysisPreferences()
      .eventResults(eventResultsData)
      .selectedEventIds({ 0: 101, 1: 102 })
      .build();
    await render(hbs`{{events-table-container/header-container/download-dropdown}}`);
    await clickTrigger();
    const options = findAll(`${downloadOptions} li ul li`);

    await assertForDownloadOptions(assert, options, 0, 'Logs as Text', '0/2');
    await assertForDownloadOptions(assert, options, 1, 'Network as PCAP', '2/2');
    await assertForDownloadOptions(assert, options, 2, 'Visible Meta as Text', '2/2');
  });


  test('should download file automatically when autoDownload preference is set to true', async function(assert) {
    // Expects 3 assertions to run only. Flash message wont be shown when autoDownloadPreference is true
    assert.expect(3);

    const fileLink = 'http://extracted-file-download-link/';
    new ReduxDataHelper(setState)
      .defaultEventAnalysisPreferences()
      .setFileExtractLink(fileLink)
      .setAutoDownloadPreference(true)
      .build();

    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedMsg = translation.t('fileExtract.ready');
      assert.equal(flash.type, 'success');
      assert.equal(flash.message.string, expectedMsg);
    });

    await render(hbs`{{events-table-container/header-container/download-dropdown}}`);
    assert.equal(didDownloadCreatorsStub.callCount, 1, 'didDownload interaction creator called one time');
    const iframe = findAll('.export-events-iframe');
    assert.equal(iframe.length, 1, 'iframe found');
    assert.equal(iframe[0].src, fileLink);
  });

  test('should not download file automatically when autoDownload preference is set to false', async function(assert) {
    assert.expect(5);
    const fileLink = 'http://extracted-file-download-link/';
    new ReduxDataHelper(setState)
      .defaultEventAnalysisPreferences()
      .setFileExtractLink(fileLink)
      .setAutoDownloadPreference(false)
      .build();

    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedMsg = translation.t('fileExtract.ready');
      assert.equal(flash.type, 'success');
      assert.equal(flash.message.string, expectedMsg);
    });

    await render(hbs`{{events-table-container/header-container/download-dropdown}}`);
    assert.equal(didDownloadCreatorsStub.callCount, 1, 'didDownload interaction creator called one time');
    const iframe = findAll('.export-events-iframe');
    assert.equal(iframe.length, 1, 'iframe found');
    assert.equal(iframe[0].src, '');
  });
});
