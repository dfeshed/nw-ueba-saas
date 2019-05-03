import { module, test } from 'qunit';
import { run } from '@ember/runloop';
import Service from '@ember/service';
import hbs from 'htmlbars-inline-precompile';
import { set, computed } from '@ember/object';
import { setupRenderingTest } from 'ember-qunit';
import DataHelper from '../../../../helpers/data-helper';
import { click, render, find, findAll, settled, triggerKeyEvent } from '@ember/test-helpers';
import sinon from 'sinon';

import * as InteractionCreators from 'recon/actions/interaction-creators';

const didDownloadCreatorsStub = sinon.stub(InteractionCreators, 'didDownloadFiles');

const ARROW_ENTER_KEY = 13;

const downloadButtonSelector = '.export-packet-button .rsa-form-button';
const dropdownArrowSelector = '.rsa-split-dropdown .rsa-form-button';
const disabledButtonSelector = 'button:disabled';
const buttonMenuSelector = '.recon-button-menu';

const data = {
  eventType: { name: 'NETWORK' }
};

const trim = (text) => text && text.replace(/\s\s+/g, ' ').trim();

module('Integration | Component | recon event actionbar/export packet', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    this.owner.register('service:accessControl', Service.extend({
      hasInvestigateContentExportAccess: computed(function() {
        return true;
      })
    }));
  });

  hooks.afterEach(function() {
    didDownloadCreatorsStub.resetHistory();
  });

  hooks.after(function() {
    didDownloadCreatorsStub.restore();
  });

  test('The caption properly updates when locale is changed', async function(assert) {
    assert.expect(2);

    const redux = this.owner.lookup('service:redux');
    new DataHelper(redux).initializeData();

    await render(hbs`{{recon-event-actionbar/export-packet}}`);

    const downloadPCAP = 'PCAPのダウンロード';
    const i18n = this.owner.lookup('service:i18n');
    run(i18n, 'addTranslations', 'ja-jp', { 'recon.packetView.downloadPCAP': downloadPCAP });

    assert.equal(trim(find(downloadButtonSelector).textContent), 'Download PCAP');

    set(i18n, 'locale', 'ja-jp');

    return settled().then(async() => {
      assert.equal(trim(find(downloadButtonSelector).textContent), downloadPCAP);
    });
  });

  test('the menu renders properly and has the correct labels for export pcap menu', async function(assert) {
    assert.expect(9);

    const _data = {
      ...data,
      fileExtractStatus: 'idle'
    };

    const redux = this.owner.lookup('service:redux');
    new DataHelper(redux).initializeData(_data);

    await render(hbs`{{recon-event-actionbar/export-packet}}`);

    const downloadPCAP = 'PCAPのダウンロード';
    const downloadPayload1 = 'リクエスト ペイロードのダウンロード';
    const downloadPayload2 = 'レスポンス ペイロードのダウンロード';
    const downloadPayload = 'すべてのペイロードのダウンロード';
    const i18n = this.owner.lookup('service:i18n');
    run(i18n, 'addTranslations', 'ja-jp', {
      'recon.packetView.downloadPCAP': downloadPCAP,
      'recon.packetView.downloadPayload1': downloadPayload1,
      'recon.packetView.downloadPayload2': downloadPayload2,
      'recon.packetView.downloadPayload': downloadPayload
    });

    await click('.rsa-split-dropdown .rsa-form-button');

    const buttonMenu = find(buttonMenuSelector);
    assert.ok(buttonMenu.classList.contains('expanded'));

    assert.equal(trim(find(`${buttonMenuSelector} li:nth-of-type(1)`).textContent), 'Download PCAP');
    assert.equal(trim(find(`${buttonMenuSelector} li:nth-of-type(2)`).textContent), 'Download Payloads');
    assert.equal(trim(find(`${buttonMenuSelector} li:nth-of-type(3)`).textContent), 'Download Request Payload');
    assert.equal(trim(find(`${buttonMenuSelector} li:nth-of-type(4)`).textContent), 'Download Response Payload');

    set(i18n, 'locale', 'ja-jp');

    return settled().then(async() => {
      assert.equal(trim(find(`${buttonMenuSelector} li:nth-of-type(1)`).textContent), downloadPCAP);
      assert.equal(trim(find(`${buttonMenuSelector} li:nth-of-type(2)`).textContent), downloadPayload);
      assert.equal(trim(find(`${buttonMenuSelector} li:nth-of-type(3)`).textContent), downloadPayload1);
      assert.equal(trim(find(`${buttonMenuSelector} li:nth-of-type(4)`).textContent), downloadPayload2);
    });
  });

  test('the button is hidden when accessControl.hasInvestigateContentExportAccess is false', async function(assert) {
    const _data = {
      ...data,
      fileExtractStatus: 'idle'
    };

    this.owner.register('service:accessControl', Service.extend({
      hasInvestigateContentExportAccess: computed(function() {
        return false;
      })
    }));

    const redux = this.owner.lookup('service:redux');
    new DataHelper(redux).initializeData(_data);

    await render(hbs`{{recon-event-actionbar/export-packet}}`);

    return settled().then(async() => {
      assert.equal(findAll('.export-packet-button').length, 0);
    });
  });

  test('it renders proper label when export pcap data', async function(assert) {
    assert.expect(3);

    const redux = this.owner.lookup('service:redux');
    new DataHelper(redux).initializeData(data).startDownloadingData();

    await render(hbs`{{recon-event-actionbar/export-packet}}`);

    const isDownloading = 'ダウンロードしています...';
    const i18n = this.owner.lookup('service:i18n');
    run(i18n, 'addTranslations', 'ja-jp', { 'recon.packetView.isDownloading': isDownloading });

    assert.equal(findAll(disabledButtonSelector).length, 2, 'Both caption and toggle buttons are disabled when download in progress');
    assert.equal(trim(findAll(disabledButtonSelector)[0].textContent), 'Downloading...');

    set(i18n, 'locale', 'ja-jp');

    return settled().then(async() => {
      assert.equal(trim(find(disabledButtonSelector).textContent), isDownloading);
    });
  });

  /*
  *checks if download is triggered when option is clicked
  */
  test('option click will trigger download', async function(assert) {
    assert.expect(2);

    const redux = this.owner.lookup('service:redux');
    new DataHelper(redux)
      .initializeData()
      .setAutoDownloadPreference(false);

    await render(hbs`{{recon-event-actionbar/export-packet}}`);


    assert.equal(find(downloadButtonSelector).outerText.trim(), 'Download PCAP', 'Download menu button label for Packets when not downloading');

    await click(dropdownArrowSelector);

    const buttonMenuItemSelector = '.recon-button-menu li:nth-child(1) a';
    await click(buttonMenuItemSelector);

    return settled().then(async() => {
      assert.equal(find(downloadButtonSelector).outerText.trim(), 'Downloading...', 'Download menu button label for Packets when downloading');
    });
  });

  /*
  *checks if download is triggered when enter is hit
  */
  test('keydown enter will trigger download', async function(assert) {
    assert.expect(2);

    const redux = this.owner.lookup('service:redux');
    new DataHelper(redux)
      .initializeData()
      .setAutoDownloadPreference(false);

    await render(hbs`{{recon-event-actionbar/export-packet}}`);

    assert.equal(find(downloadButtonSelector).outerText.trim(), 'Download PCAP', 'Download menu button label for Packets when not downloading');

    await click(dropdownArrowSelector);

    const buttonMenuItemSelector = '.recon-button-menu li:nth-child(1)';
    await triggerKeyEvent(buttonMenuItemSelector, 'keydown', ARROW_ENTER_KEY);

    return settled().then(async() => {
      assert.equal(find(downloadButtonSelector).outerText.trim(), 'Downloading...', 'Download menu button label for Packets when downloading');
    });
  });

  test('Recon should pick default Packet format set by user', async function(assert) {
    assert.expect(2);

    const redux = this.owner.lookup('service:redux');
    new DataHelper(redux).initializeData().setDefaultNetworkDownloadFormat('PAYLOAD');

    await render(hbs`{{recon-event-actionbar/export-packet}}`);

    const downloadPayload = 'すべてのペイロードのダウンロード';
    const i18n = this.owner.lookup('service:i18n');
    run(i18n, 'addTranslations', 'ja-jp', { 'recon.packetView.downloadPayload': downloadPayload });

    assert.equal(trim(find(downloadButtonSelector).textContent), 'Download Payloads');

    set(i18n, 'locale', 'ja-jp');

    return settled().then(async() => {
      assert.equal(trim(find(downloadButtonSelector).textContent), downloadPayload);
    });
  });

  test('Recon should disable default Packet format set by user along with dropdown toggle if no packet (header & payload) available', async function(assert) {

    const redux = this.owner.lookup('service:redux');
    new DataHelper(redux).noPackets();

    await render(hbs`{{recon-event-actionbar/export-packet}}`);
    assert.equal(findAll(disabledButtonSelector).length, 2, 'Both caption and toggle disabled');

  });

  test('the menu renders properly and has the valid options/components enabled', async function(assert) {

    const redux = this.owner.lookup('service:redux');
    new DataHelper(redux).initializeData().setDefaultNetworkDownloadFormat('PAYLOAD2');

    await render(hbs`{{recon-event-actionbar/export-packet}}`);

    assert.equal(trim(find(disabledButtonSelector).textContent), 'Download Response Payload');
    assert.equal(findAll(disabledButtonSelector).length, 1, 'Only caption is disabled');

    await click(dropdownArrowSelector);

    assert.equal(findAll(`${buttonMenuSelector} li:nth-of-type(3) a.disabled`).length, 0, 'Enabled Download Request Payload');
    assert.equal(findAll(`${buttonMenuSelector} li:nth-of-type(4) a.disabled`).length, 1, 'Disabled Download Response Payload');

  });

  test('PCAP download option available for packets without payloads', async function(assert) {

    const redux = this.owner.lookup('service:redux');
    new DataHelper(redux).initializeDataWithoutPayloads();

    await render(hbs`{{recon-event-actionbar/export-packet}}`);

    await click(dropdownArrowSelector);

    assert.equal(findAll(`${buttonMenuSelector} a.disabled`).length, 3, 'Download Payload options are disabled, only PCAP download available');

  });

  test('the extracted file must be downloaded automatically', async function(assert) {
    assert.expect(3);

    const fileLink = 'http://extracted-file-download-link/';

    const redux = this.owner.lookup('service:redux');
    new DataHelper(redux)
      .initializeData()
      .setAutoDownloadPreference(true)
      .setExtractedFileLink(fileLink);
    await render(hbs`{{recon-event-actionbar/export-packet}}`);

    assert.equal(didDownloadCreatorsStub.callCount, 1, 'didDownload interaction creator called one time');
    const selector = '.js-export-packet-iframe';
    const iframe = findAll(selector);
    assert.equal(iframe.length, 1);
    assert.equal(find(selector).src, fileLink);
  });

  test('the extracted file must not be downloaded automatically', async function(assert) {
    assert.expect(2);

    const fileLink = 'http://extracted-file-download-link/';

    const redux = this.owner.lookup('service:redux');
    new DataHelper(redux)
      .initializeData()
      .setAutoDownloadPreference(false)
      .setExtractedFileLink(fileLink);

    await render(hbs`{{recon-event-actionbar/export-packet}}`);

    const selector = '.js-export-packet-iframe';
    const iframe = findAll(selector);
    assert.equal(iframe.length, 1);
    assert.equal(find(selector).src, '');
  });
});
