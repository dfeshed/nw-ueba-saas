import { module, test, skip } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { throwSocket } from '../../../helpers/patch-socket';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import hbs from 'htmlbars-inline-precompile';
import { click, find, findAll, render, waitUntil } from '@ember/test-helpers';
import { clickTrigger, selectChoose } from 'ember-power-select/test-support/helpers';
import preferencesConfig from '../../../data/config';

const assertForPreferencesPanelSelectedOptions = async function(assert, child, index, selectedOption) {
  await selectChoose(`.rsa-preferences-field-content:nth-child(${child})`, selectedOption);
  const str = findAll('.ember-power-select-selected-item')[index].textContent.trim();
  assert.equal(str, selectedOption, 'Value needs to default to the user selected option');
};

const assertForPreferencesInfoIcon = async function(assert, index, resultExpected, assertionMessage) {
  const radioGroupChildren = findAll('.rsa-form-radio-group div')[index].childNodes;
  const arr = [...radioGroupChildren];
  const infoIcon = arr.find((e) => e.nodeName === 'I');
  const resultFound = typeof infoIcon !== 'undefined';
  assert.equal(resultFound, resultExpected, assertionMessage);
};

const renderApplicationContent = async function(ctx, assert) {
  ctx.set('preferencesConfig', preferencesConfig);
  await render(hbs`
    {{#rsa-application-content}}
      <grid responsive>
        <box class="col-xs-3">
          <aside>
            {{preferences-panel-trigger preferencesConfig=preferencesConfig}}
            <div class='testDiv'>
              Panel Content
            </div>
          </aside>
        </box>
        <box class="col-xs-9">
          <page>
            {{preferences-panel}}
          </page>
        </box>
      </grid>
    {{/rsa-application-content}}
  `);
  await click('.rsa-icon-settings-1-filled');
  await waitUntil(() => findAll('.rsa-preferences-field-content').length === 7, { timeout: 3000 });
  assert.ok(find('.is-expanded'), 'Preference Panel opened.');
};

const getTextFromDOMArray = (arr) => {
  return arr.reduce((a, c) => a + c.textContent.trim().replace(/\s+/g, ''), '');
};

module('Integration | Component | Preferences Details', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('Preferences panel opens correctly with all user selected preferences', async function(assert) {
    await renderApplicationContent(this, assert);
    await waitUntil(() => findAll('.ember-power-select-selected-item').length === 4, { timeout: 3000 });
    const selectedItems = findAll('.ember-power-select-selected-item');
    let str = selectedItems[0].textContent.trim();
    assert.equal(str, 'Packet Analysis');
    str = selectedItems[1].textContent.trim();
    assert.equal(str, 'Download Log');
    str = selectedItems[2].textContent.trim();
    assert.equal(str, 'Download PCAP');
    str = selectedItems[3].textContent.trim();
    assert.equal(str, 'Download Text');
    assert.ok(find('.rsa-form-radio-label.DB.checked'));
    assert.ok(find('.rsa-form-checkbox-label.checked'));

    // NewestFirst code commented out
    // assert.ok(find('.rsa-form-radio-label.Ascending.checked'));
  });

  test('Preferences panel should render title for DOWNLOAD EXTRACTED FILES AUTOMATICALLY', async function(assert) {
    await renderApplicationContent(this, assert);
    assert.equal(findAll('.rsa-form-checkbox-label')[0].getAttribute('title'), 'If selected, the browser downloads files to the download folder and if a file type is mapped to a default program, automatically open it.');
  });

  test('Preferences panel should render title for Update time window automatically', async function(assert) {
    await renderApplicationContent(this, assert);
    assert.equal(findAll('.rsa-form-checkbox-label')[1].getAttribute('title'), 'Enables continuous update of the relative time window for the query. The time window updates when new data is stored (database time) or when a minute elapses (current time). In both cases the query icon activates, allowing you to refresh stale results.');
  });

  test('Preferences panel should show Time format Settings', async function(assert) {
    await renderApplicationContent(this, assert);
    assert.ok(find('.rsa-form-radio-label.DB.checked'));
    assert.equal(find('.rsa-form-radio-label.DB').getAttribute('title'), 'Default selection. When selected, the end time is the last time data was stored in the service being queried.');
    assert.equal(find('.rsa-form-radio-label.WALL').getAttribute('title'), 'When selected, the end time is the current time of day, with the preferred time zone, no matter when the most recent data was stored.');
  });

  test('Preferences panel comes with valid options for Analysis', async function(assert) {
    await renderApplicationContent(this, assert);
    await clickTrigger('.rsa-preferences-field-content:nth-child(1)');
    const options = findAll('.ember-power-select-option');
    assert.equal(options.length, 3);
    assert.equal(getTextFromDOMArray(options), 'TextAnalysisPacketAnalysisFileAnalysis');
  });

  test('Preferences panel comes with valid options for log format', async function(assert) {
    await renderApplicationContent(this, assert);
    await clickTrigger('.rsa-preferences-field-content:nth-child(2)');
    const options = findAll('.ember-power-select-option');
    assert.equal(options.length, 4);
    assert.equal(getTextFromDOMArray(options), 'DownloadLogDownloadCSVDownloadXMLDownloadJSON');
  });

  test('Preferences panel comes with valid options for packet format', async function(assert) {
    await renderApplicationContent(this, assert);
    await clickTrigger('.rsa-preferences-field-content:nth-child(3)');
    const options = findAll('.ember-power-select-option');
    assert.equal(options.length, 4);
    assert.equal(getTextFromDOMArray(options), 'DownloadPCAPDownloadAllPayloadsDownloadRequestPayloadDownloadResponsePayload');
  });

  test('Preferences panel comes with valid options for meta format', async function(assert) {
    await renderApplicationContent(this, assert);
    await clickTrigger('.rsa-preferences-field-content:nth-child(4)');
    const options = findAll('.ember-power-select-option');
    assert.equal(options.length, 4);
    assert.equal(getTextFromDOMArray(options), 'DownloadTextDownloadCSVDownloadTSVDownloadJSON');
  });

  test('Preferences panel saves new Analysis on change', async function(assert) {
    await renderApplicationContent(this, assert);
    await selectChoose('.rsa-preferences-field-content:nth-child(1)', 'Text Analysis');
    await waitUntil(() => findAll('.ember-power-select-selected-item')[0].textContent.trim() === 'Text Analysis', { timeout: 3000 });
  });

  test('Preferences panel defaults the Analysis View to the user selected value', async function(assert) {
    await renderApplicationContent(this, assert);
    await assertForPreferencesPanelSelectedOptions(assert, 1, 0, 'Packet Analysis');
  });

  test('Preferences panel defaults the dowloadLogFormat to the user selected value', async function(assert) {
    await renderApplicationContent(this, assert);
    await assertForPreferencesPanelSelectedOptions(assert, 2, 1, 'Download Log');
  });

  test('Preferences panel defaults the dowloadPacketFormat to the user selected value', async function(assert) {
    await renderApplicationContent(this, assert);
    await assertForPreferencesPanelSelectedOptions(assert, 3, 2, 'Download PCAP');
  });

  test('Preferences panel defaults the dowloadMetaFormat to the user selected value', async function(assert) {
    await renderApplicationContent(this, assert);
    await assertForPreferencesPanelSelectedOptions(assert, 4, 3, 'Download Text');
  });

  test('Preferences panel should change Time format Settings on click', async function(assert) {
    await renderApplicationContent(this, assert);
    assert.ok(find('.rsa-form-radio-label.DB.checked'));
    assert.notOk(find('.rsa-form-radio-label.WALL.checked'));
    await click('.rsa-form-radio-label.WALL');
    await waitUntil(() => find('.rsa-form-radio-label.WALL.checked'), { timeout: 3000 });
    assert.notOk(find('.rsa-form-radio-label.DB.checked'));
  });

  // NewestFirst code commented out
  skip('Preferences panel should change Sort Event Settings on click', async function(assert) {
    await renderApplicationContent(this, assert);
    assert.ok(find('.rsa-form-radio-label.Ascending.checked'));
    assert.notOk(find('.rsa-form-radio-label.Descending.checked'));
    await click('.rsa-form-radio-label.Descending');
    await waitUntil(() => find('.rsa-form-radio-label.Descending.checked'), { timeout: 3000 });
    assert.notOk(find('.rsa-form-radio-label.Ascending.checked'));
  });

  test('Preferences panel should uncheck the Download automatically checkbox on click', async function(assert) {
    await renderApplicationContent(this, assert);
    await click('.rsa-form-checkbox-label');
    await waitUntil(() => !find('.rsa-form-checkbox-label.checked'), { timeout: 3000 });
  });

  test('Preferences should pick the defaultData in case of no response', async function(assert) {
    throwSocket();
    await renderApplicationContent(this, assert);
    assert.equal(findAll('.ember-power-select-selected-item')[0].textContent.trim(), 'Text Analysis');
    assert.equal(findAll('.x-toggle-container-checked').length, 0, 'Found the Relative Time Window toggle disabled by default');
  });

  test('checkbox to update time window should repond on click', async function(assert) {
    await renderApplicationContent(this, assert);
    const [, checkboxForTimeWindow] = findAll('.rsa-form-checkbox-label');
    assert.equal(checkboxForTimeWindow.textContent.trim(), 'Update time window automatically', 'Found the correct checkbox');
    assert.equal(checkboxForTimeWindow.className.trim(), 'rsa-form-checkbox-label', 'The checkbox by default is unchecked');
    await checkboxForTimeWindow.click();
    // clicking on the Time Window checkbox should mark it checked.
    await waitUntil(() => checkboxForTimeWindow.className.trim() === 'rsa-form-checkbox-label checked', { timeout: 3000 });
  });

  // NewestFirst code commented out
  skip('renders info icon where information is needed', async function(assert) {
    await renderApplicationContent(this, assert);
    await assertForPreferencesInfoIcon(assert, 0, false, 'No Info icon for Date Time Format');
    await assertForPreferencesInfoIcon(assert, 1, true, 'Event Sort Order has info icon');
  });
});
