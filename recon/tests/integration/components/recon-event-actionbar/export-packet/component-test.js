import { module, test } from 'qunit';
import { run } from '@ember/runloop';
import Service from '@ember/service';
import hbs from 'htmlbars-inline-precompile';
import { set, computed } from '@ember/object';
import { setupRenderingTest } from 'ember-qunit';
import DataHelper from '../../../../helpers/data-helper';
import { click, render, find, findAll, settled } from '@ember/test-helpers';

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

  test('The caption properly updates when locale is changed', async function(assert) {
    assert.expect(2);

    const redux = this.owner.lookup('service:redux');
    new DataHelper(redux).initializeData();

    await render(hbs`{{recon-event-actionbar/export-packet}}`);

    const downloadPCAP = 'PCAPのダウンロード';
    const i18n = this.owner.lookup('service:i18n');
    run(i18n, 'addTranslations', 'ja-jp', { 'recon.packetView.downloadPCAP': downloadPCAP });

    const selector = 'button';
    assert.equal(trim(find(selector).textContent), 'Download PCAP');

    set(i18n, 'locale', 'ja-jp');

    return settled().then(async () => {
      assert.equal(trim(find(selector).textContent), downloadPCAP);
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

    const selector = '.recon-button-menu';
    const buttonMenu = find(selector);
    assert.ok(buttonMenu.classList.contains('expanded'));

    assert.equal(trim(find(`${selector} li:nth-of-type(1)`).textContent), 'Download PCAP');
    assert.equal(trim(find(`${selector} li:nth-of-type(2)`).textContent), 'Download All Payloads');
    assert.equal(trim(find(`${selector} li:nth-of-type(3)`).textContent), 'Download Request Payload');
    assert.equal(trim(find(`${selector} li:nth-of-type(4)`).textContent), 'Download Response Payload');

    set(i18n, 'locale', 'ja-jp');

    return settled().then(async () => {
      assert.equal(trim(find(`${selector} li:nth-of-type(1)`).textContent), downloadPCAP);
      assert.equal(trim(find(`${selector} li:nth-of-type(2)`).textContent), downloadPayload);
      assert.equal(trim(find(`${selector} li:nth-of-type(3)`).textContent), downloadPayload1);
      assert.equal(trim(find(`${selector} li:nth-of-type(4)`).textContent), downloadPayload2);
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

    return settled().then(async () => {
      assert.equal(findAll('.export-packet-button').length, 0);
    });
  });

  test('it renders proper label when export pcap data', async function(assert) {
    assert.expect(2);

    const redux = this.owner.lookup('service:redux');
    new DataHelper(redux).initializeData(data).startDownloadingData();

    await render(hbs`{{recon-event-actionbar/export-packet}}`);

    const isDownloading = 'ダウンロードしています...';
    const i18n = this.owner.lookup('service:i18n');
    run(i18n, 'addTranslations', 'ja-jp', { 'recon.packetView.isDownloading': isDownloading });

    const selector = 'button';
    assert.equal(trim(find(selector).textContent), 'Downloading...');

    set(i18n, 'locale', 'ja-jp');

    return settled().then(async () => {
      assert.equal(trim(find(selector).textContent), isDownloading);
    });
  });

  test('Recon should pick default Packet format set by user', async function(assert) {
    assert.expect(2);

    const redux = this.owner.lookup('service:redux');
    new DataHelper(redux).initializeData().setDownloadFormatToPayload();

    await render(hbs`{{recon-event-actionbar/export-packet}}`);

    const downloadPayload = 'すべてのペイロードのダウンロード';
    const i18n = this.owner.lookup('service:i18n');
    run(i18n, 'addTranslations', 'ja-jp', { 'recon.packetView.downloadPayload': downloadPayload });

    const selector = 'button';
    assert.equal(trim(find(selector).textContent), 'Download All Payloads');

    set(i18n, 'locale', 'ja-jp');

    return settled().then(async () => {
      assert.equal(trim(find(selector).textContent), downloadPayload);
    });
  });

  test('the extracted file must be downloaded automatically', async function(assert) {
    assert.expect(2);

    const fileLink = 'http://extracted-file-download-link/';

    const redux = this.owner.lookup('service:redux');
    new DataHelper(redux)
        .initializeData()
        .setAutoDownloadPreference(true)
        .setExtractedFileLink(fileLink);

    await render(hbs`{{recon-event-actionbar/export-packet}}`);

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
