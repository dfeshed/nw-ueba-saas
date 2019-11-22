import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import { click, find, findAll, render, triggerKeyEvent } from '@ember/test-helpers';
import EventColumnGroups from '../../../data/subscriptions/column-group';
import KEY_MAP from 'investigate-events/util/keys';

let setState;

const ARROW_DOWN_KEY = KEY_MAP.arrowDown.code;

// selectors
const columnSelectorIcon = '.rsa-icon-cog';
const columnGroupSelector = '.rsa-investigate-events-table__header__columnGroups';
const panel = '.panel-content.list-manager-panel';
const columnGroupDropDownButton = `${columnGroupSelector} .rsa-button-group button`;
const panelTrigger = '.rsa-content-tethered-panel-trigger .list-menu-trigger button.rsa-form-button';
const listItem = '.list-view-body ul.rsa-item-list > li';

const assertForInvestigateColumnAndColumnSelector = async function(assert, headerCount, columnSelectorCount, selectedOptionName) {
  assert.ok(find(columnGroupDropDownButton), 'dropdown button shall be found');

  await click(panelTrigger); // open column group list
  assert.ok(find(panel), 'shall find column group list tethered panel');
  const optionToChoose = findAll(`${listItem} a`).find((d) => d.textContent.trim() === selectedOptionName);
  await click(optionToChoose); // this should close column group list

  assert.equal(findAll('.rsa-data-table-header-cell').length, headerCount,
    `Should show visible columns in table for ${selectedOptionName}.`);

  await click(panelTrigger); // open column group list
  assert.equal(find(`${listItem}.is-selected`).textContent.trim(), selectedOptionName,
    `Selected column group should be ${selectedOptionName}.`);
  await click(panelTrigger); // close column group list

  await click(columnSelectorIcon);
  assert.equal(findAll('.rsa-data-table-column-selector-panel li .rsa-form-checkbox-label').length, columnSelectorCount,
    `Should show all columns for column selector for ${selectedOptionName}.`);
};

const renderDefaultEventTable = async function() {
  new ReduxDataHelper(setState)
    .selectedColumnGroup('SUMMARY')
    .language([
      { format: 'Text', metaName: 'host.role' },
      { format: 'Text', metaName: 'timezone' },
      { format: 'Text', metaName: 'filename' },
      { format: 'Text', metaName: 'filename.size' },
      { format: 'Text', metaName: 'filename.src' },
      { format: 'Text', metaName: 'filename.dst' },
      { format: 'Text', metaName: 'file.vendor' },
      { format: 'Text', metaName: 'file.entropy' },
      { format: 'Text', metaName: 'checksum' },
      { format: 'Text', metaName: 'username' },
      { format: 'Text', metaName: 'task.name' },
      { format: 'Text', metaName: 'owner' },
      { format: 'Text', metaName: 'domain' },
      { format: 'Text', metaName: 'dn' },
      { format: 'Text', metaName: 'cert.subject' },
      { format: 'Text', metaName: 'cert.common' },
      { format: 'Text', metaName: 'cert.checksum' },
      { format: 'Text', metaName: 'cert.ca' },
      { format: 'Text', metaName: 'bytes.src' },
      { format: 'Text', metaName: 'rbytes' },
      { format: 'Text', metaName: 'referer' },
      { format: 'Text', metaName: 'directory' },
      { format: 'Text', metaName: 'directory.src' },
      { format: 'Text', metaName: 'directory.dst' },
      { format: 'Text', metaName: 'dir.path.src' },
      { format: 'Text', metaName: 'param.src' },
      { format: 'Text', metaName: 'custom.theme' },
      { format: 'Text', metaName: 'size' },
      { format: 'Text', metaName: 'custom.meta-summary' },
      { format: 'Text', metaName: 'password' },
      { format: 'Text', metaName: 'device.type' },
      { format: 'Text', metaName: 'device.ip' },
      { format: 'Text', metaName: 'device.ipv6' },
      { format: 'Text', metaName: 'device.host' },
      { format: 'Text', metaName: 'device.class' },
      { format: 'Text', metaName: 'paddr' },
      { format: 'Text', metaName: 'device.title' },
      { format: 'Text', metaName: 'event.source' },
      { format: 'Text', metaName: 'event.desc' },
      { format: 'Text', metaName: 'ec.subject' },
      { format: 'Text', metaName: 'ec.activity' },
      { format: 'Text', metaName: 'ec.theme' },
      { format: 'Text', metaName: 'ec.outcome' },
      { format: 'Text', metaName: 'event.cat.title' },
      { format: 'Text', metaName: 'device.group' },
      { format: 'Text', metaName: 'event.class' },
      { format: 'Text', metaName: 'sql' },
      { format: 'Text', metaName: 'category' },
      { format: 'Text', metaName: 'query' },
      { format: 'Text', metaName: 'OS' },
      { format: 'Text', metaName: 'browser' },
      { format: 'Text', metaName: 'version' },
      { format: 'Text', metaName: 'policy.title' },
      { format: 'Text', metaName: 'lc.cid' },
      { format: 'Text', metaName: 'time' },
      { format: 'Text', metaName: 'event.time' },
      { format: 'Text', metaName: 'medium' },
      { format: 'Text', metaName: 'service' },
      { format: 'Text', metaName: 'orig_ip' },
      { format: 'Text', metaName: 'ip.src' },
      { format: 'Text', metaName: 'ip.dst' },
      { format: 'Text', metaName: 'tcp.dstport' },
      { format: 'Text', metaName: 'ip.dstport' },
      { format: 'Text', metaName: 'forward.ip' },
      { format: 'Text', metaName: 'alias.ip' },
      { format: 'Text', metaName: 'alias.host' },
      { format: 'Text', metaName: 'country.src' },
      { format: 'Text', metaName: 'country.dst' },
      { format: 'Text', metaName: 'org.src' },
      { format: 'Text', metaName: 'org.dst' },
      { format: 'Text', metaName: 'subject' },
      { format: 'Text', metaName: 'email.src' },
      { format: 'Text', metaName: 'email.dst' },
      { format: 'Text', metaName: 'domain.dst' },
      { format: 'Text', metaName: 'client' },
      { format: 'Text', metaName: 'server' },
      { format: 'Text', metaName: 'content' },
      { format: 'Text', metaName: 'action' },
      { format: 'Text', metaName: 'attachment' },
      { format: 'Text', metaName: 'extension' },
      { format: 'Text', metaName: 'udp.srcport' },
      { format: 'Text', metaName: 'udp.dstport' },
      { format: 'Text', metaName: 'ip.proto' },
      { format: 'Text', metaName: 'eth.type' },
      { format: 'Text', metaName: 'eth.src' },
      { format: 'Text', metaName: 'eth.dst' },
      { format: 'Text', metaName: 'filetype' },
      { format: 'Text', metaName: 'filetitle' },
      { format: 'Text', metaName: 'usertitle' },
      { format: 'Text', metaName: 'user.src' },
      { format: 'Text', metaName: 'user.dst' },
      { format: 'Text', metaName: 'error' },
      { format: 'Text', metaName: 'crypto' },
      { format: 'Text', metaName: 'ssl.subject' },
      { format: 'Text', metaName: 'ssl.ca' },
      { format: 'Text', metaName: 'ioc' },
      { format: 'Text', metaName: 'boc' },
      { format: 'Text', metaName: 'eoc' },
      { format: 'Text', metaName: 'analysis.session' },
      { format: 'Text', metaName: 'analysis.service' },
      { format: 'Text', metaName: 'analysis.file' },
      // -- old? ---
      { format: 'Text', metaName: 'risk.info' },
      { format: 'Text', metaName: 'risk.suspicious' },
      { format: 'Text', metaName: 'risk.warning' },
      // -- end old? --
      { format: 'Text', metaName: 'threat.category' },
      { format: 'Text', metaName: 'threat.desc' },
      { format: 'Text', metaName: 'threat.source' },
      { format: 'Text', metaName: 'alert' },
      { format: 'Text', metaName: 'sourcefile' },
      { format: 'Text', metaName: 'did' }
    ])
    .reconSize('max')
    .eventsPreferencesConfig()
    .eventTimeSortOrder()
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
  });

  test('it renders default table', async function(assert) {
    await renderDefaultEventTable();
    assert.equal(findAll('.rsa-investigate-events-table').length, 1);
    assert.equal(findAll('.rsa-icon-cog').length, 1, 'There should be a column selector icon.');
  });

  // 16 columns including column for checkbox though checkbox itself might be hidden if no results are populated
  test('it should show columns for Email Analysis', async function(assert) {
    await renderDefaultEventTable();
    assert.equal(EventColumnGroups[0].name, 'RSA Email Analysis', 'RSA Email Analysis');
    await assertForInvestigateColumnAndColumnSelector(assert, 16, EventColumnGroups[0].columns.length, 'RSA Email Analysis');
  });

  test('it should show columns for Endpoint Analysis', async function(assert) {
    await renderDefaultEventTable();
    assert.equal(EventColumnGroups[1].name, 'RSA Endpoint Analysis');
    await assertForInvestigateColumnAndColumnSelector(assert, 16, EventColumnGroups[1].columns.length, 'RSA Endpoint Analysis');
  });

  test('it should show columns for Malware Analysis', async function(assert) {
    await renderDefaultEventTable();
    assert.equal(EventColumnGroups[2].name, 'RSA Malware Analysis');
    await assertForInvestigateColumnAndColumnSelector(assert, 16, EventColumnGroups[2].columns.length, 'RSA Malware Analysis');
  });

  test('it should show columns for Threat Analysis', async function(assert) {
    await renderDefaultEventTable();
    assert.equal(EventColumnGroups[3].name, 'RSA Threat Analysis');
    await assertForInvestigateColumnAndColumnSelector(assert, 16, EventColumnGroups[3].columns.length, 'RSA Threat Analysis');
  });

  test('it should show columns for Web Analysis', async function(assert) {
    await renderDefaultEventTable();
    assert.equal(EventColumnGroups[4].name, 'RSA Web Analysis');
    await assertForInvestigateColumnAndColumnSelector(assert, 16, EventColumnGroups[4].columns.length, 'RSA Web Analysis');
  });


  test('it should show "no results" message only if there are zero results', async function(assert) {
    new ReduxDataHelper(setState)
      .selectedColumnGroup('SUMMARY')
      .columnGroups(EventColumnGroups)
      .eventsPreferencesConfig()
      .eventTimeSortOrder()
      .selectedEventIds({})
      .language()
      .eventResults([])
      .build();

    await render(hbs`{{events-table-container}}`);

    assert.ok(findAll('.rsa-panel-message .message'), 'Message shown');
    assert.equal(find('.rsa-panel-message .message').textContent.trim(), 'Your filter criteria did not match any records.');
  });

  test('it should show "may take time" message when loading and at threshold', async function(assert) {
    new ReduxDataHelper(setState)
      .eventsPreferencesConfig()
      .hasRequiredValuesToQuery()
      .eventTimeSortOrder()
      .eventThreshold(1)
      .eventCount(1)
      .selectedEventIds({})
      .language()
      .eventResults()
      .build();

    await render(hbs`{{events-table-container}}`);
    assert.equal(find('.rsa-loader__text').textContent.trim(), 'Found more than 1 results. Busy converting bytes into pixels. Check the query console for more details.');
  });

  test('it should not show "may take time" message when loading and not at threshold', async function(assert) {
    new ReduxDataHelper(setState)
      .eventsPreferencesConfig()
      .hasRequiredValuesToQuery()
      .eventTimeSortOrder()
      .eventThreshold(2)
      .eventCount(1)
      .selectedEventIds({})
      .language()
      .eventResults()
      .build();

    await render(hbs`{{events-table-container}}`);
    assert.equal(find('.rsa-loader__text').textContent.trim(), 'Loading ...');
  });

  test('it should not show "no results" message if there are results', async function(assert) {
    new ReduxDataHelper(setState)
      .selectedColumnGroup('SUMMARY')
      .columnGroups(EventColumnGroups)
      .eventsPreferencesConfig()
      .eventTimeSortOrder()
      .selectedEventIds({})
      .language()
      .eventResults(['something'])
      .build();

    await render(hbs`{{events-table-container}}`);

    assert.notOk(find('.rsa-panel-message .message'), 'Message not shown');
  });

  test('keyDown will trigger event selection if dropdown not in view', async function(assert) {

    new ReduxDataHelper(setState)
      .getColumns('SUMMARY', EventColumnGroups)
      .isEventResultsError(false)
      .eventsPreferencesConfig()
      .language()
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
      .getColumns('SUMMARY', EventColumnGroups)
      .isEventResultsError(false)
      .eventsPreferencesConfig()
      .language()
      .defaultEventAnalysisPreferences()
      .eventResults(eventResultsData)
      .build();

    let eventSelected = false;
    this.set('handleSelectEvent', () => {
      eventSelected = true;
    });

    await render(hbs` {{events-table-container selectEvent=handleSelectEvent}}`);
    await click(columnGroupDropDownButton);
    await triggerKeyEvent(columnGroupSelector, 'keyup', ARROW_DOWN_KEY);
    assert.notOk(eventSelected, 'Keystroke does not trigger event selection when dropdown in view');
  });

  test('if events have begun streaming, the progress bar is set to 1', async function(assert) {
    new ReduxDataHelper(setState)
      .selectedColumnGroup('SUMMARY')
      .columnGroups(EventColumnGroups)
      .eventsPreferencesConfig()
      .eventTimeSortOrder()
      .selectedEventIds({})
      .language()
      .eventResultsStatus('streaming')
      .eventResults([])
      .build();

    await render(hbs`{{events-table-container}}`);
    assert.equal(find('.rsa-progress-bar').getAttribute('data-percent'), 1, 'progress bar is initialized');
  });

  test('if event streaming has completed, the progress bar full', async function(assert) {
    new ReduxDataHelper(setState)
      .selectedColumnGroup('SUMMARY')
      .columnGroups(EventColumnGroups)
      .eventsPreferencesConfig()
      .eventTimeSortOrder()
      .selectedEventIds({})
      .language()
      .eventResultsStatus('complete')
      .eventResults([])
      .build();

    await render(hbs`{{events-table-container}}`);
    assert.equal(find('.rsa-progress-bar').getAttribute('data-percent'), 100, 'progress bar is full');
  });

});
