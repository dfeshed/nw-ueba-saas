import { moduleForComponent, test } from 'ember-qunit';
import wait from 'ember-test-helpers/wait';
import hbs from 'htmlbars-inline-precompile';
import DataHelper from '../../../../helpers/data-helper';
import { patchFlash } from '../../../../helpers/patch-flash';
import { getOwner } from '@ember/application';
import EmberObject from '@ember/object';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

const data = {
  eventType: { name: 'LOG' }
};

const ARROW_ENTER_KEY = 13;

const downloadButtonSelector = '.export-logs-button .rsa-form-button';
const dropdownArrowSelector = '.rsa-split-dropdown .rsa-form-button';

moduleForComponent('recon-event-actionbar/export-logs', 'Integration | Component | recon event actionbar/export logs', {
  integration: true,
  beforeEach() {
    this.set('accessControl', EmberObject.create({}));
    this.set('accessControl.hasInvestigateContentExportAccess', true);
    this.registry.injection('component:recon-event-actionbar/export-logs', 'i18n', 'service:i18n');
    this.inject.service('redux');
    this.inject.service('flash-messages');
    this.inject.service('preferences');
    initialize(this);
  }
});

test('it renders', function(assert) {
  new DataHelper(this.get('redux')).initializeData();

  this.render(hbs`{{recon-event-actionbar/export-logs accessControl=accessControl}}`);

  return wait().then(() => {
    const str = this.$().text().trim().replace(/\s+/g, '');
    assert.equal(str, 'DownloadLogDownloadLogDownloadCSVDownloadXMLDownloadJSON');
  });
});

test('it renders proper label for log data', function(assert) {
  const _data = {
    ...data,
    fileExtractStatus: 'idle'
  };
  new DataHelper(this.get('redux')).initializeData(_data);

  this.render(hbs`{{recon-event-actionbar/export-logs accessControl=accessControl}}`);

  return wait().then(() => {
    const str = this.$()[0].innerText.trim();
    assert.equal(str, 'Download Log');
  });
});

test('the menu renders properly and has the correct labels for down logs menu', function(assert) {
  const _data = {
    ...data,
    fileExtractStatus: 'idle'
  };
  new DataHelper(this.get('redux')).initializeData(_data);

  this.render(hbs`{{recon-event-actionbar/export-logs accessControl=accessControl}}`);

  this.$('.rsa-split-dropdown').find('.rsa-form-button').click();

  return wait().then(() => {
    const ulEl = this.$('.rsa-split-dropdown').next();
    const ulElChildren = ulEl.children();

    assert.equal(ulEl.hasClass('expanded'), true);
    assert.equal(ulElChildren[0].innerText.trim(), 'Download Log');
    assert.equal(ulElChildren[1].innerText.trim(), 'Download CSV');
    assert.equal(ulElChildren[2].innerText.trim(), 'Download XML');
    assert.equal(ulElChildren[3].innerText.trim(), 'Download JSON');
  });
});

test('the button is hidden when accessControl.hasInvestigateContentExportAccess is false', function(assert) {
  const _data = {
    ...data,
    fileExtractStatus: 'idle'
  };
  new DataHelper(this.get('redux')).initializeData(_data);

  this.set('accessControl.hasInvestigateContentExportAccess', false);

  this.render(hbs`{{recon-event-actionbar/export-logs accessControl=accessControl}}`);

  return wait().then(() => {
    assert.equal(this.$('.export-logs-button').length, 0);
  });
});

test('it renders proper label when downloading log data', function(assert) {
  const _data = {
    ...data,
    fileExtractStatus: 'wait'
  };
  new DataHelper(this.get('redux'))
    .initializeData(_data)
    .startDownloadingData();

  this.render(hbs`{{recon-event-actionbar/export-logs accessControl=accessControl}}`);

  return wait().then(() => {
    const str = this.$()[0].innerText.trim();
    assert.equal(str, 'Downloading...');
  });
});

/*
 *checks if download is triggered when option is clicked
*/
test('option click will trigger download', function(assert) {

  new DataHelper(this.get('redux'))
    .initializeData()
    .setAutoDownloadPreference(false);

  this.render(hbs `{{recon-event-actionbar/export-logs accessControl=accessControl}}`);

  assert.equal(this.$(downloadButtonSelector)[0].outerText.trim(), 'Download Log', 'Download menu button label for Logs when not downloading');

  this.$(dropdownArrowSelector).click();

  const buttonMenuSelector = '.recon-button-menu li:nth-child(1) a';
  this.$(buttonMenuSelector).click();

  const that = this;
  return wait().then(function() {
    assert.equal(that.$(downloadButtonSelector)[0].outerText.trim(), 'Downloading...', 'Download menu button label for Logs when downloading');
  });
});

/*
 *checks if download is triggered when enter is hit
*/
test('keydown enter will trigger download', function(assert) {

  new DataHelper(this.get('redux'))
    .initializeData()
    .setAutoDownloadPreference(false);

  this.render(hbs `{{recon-event-actionbar/export-logs accessControl=accessControl}}`);

  assert.equal(this.$(downloadButtonSelector)[0].outerText.trim(), 'Download Log', 'Download menu button label for Logs when not downloading');

  this.$(dropdownArrowSelector).click();

  // this.$ does not have "Event" on it, use window
  // eslint-disable-next-line new-cap
  const e = window.$.Event('keydown');
  e.keyCode = ARROW_ENTER_KEY;

  const buttonMenuSelector = '.recon-button-menu li:nth-child(1)';
  this.$(buttonMenuSelector).trigger(e);

  assert.equal(this.$(downloadButtonSelector)[0].outerText.trim(), 'Downloading...', 'Download menu button label for Logs when downloading');
});

/*
 *checks if serviceCall for getPreferences is happening successfully
 * if default download preference is changed to eg XML, then corresponding caption should change and reflect the same
*/
test('Recon should pick default Log format set by the user', function(assert) {
  new DataHelper(this.get('redux')).initializeData().setDownloadFormatToXml();
  this.render(hbs`{{recon-event-actionbar/export-logs accessControl=accessControl}}`);
  return wait().then(() => {
    const str = this.$()[0].innerText.trim();
    assert.equal(str, 'Download XML');
  });
});

test('the extracted file must be downloaded automatically', function(assert) {
  const fileLink = 'http://extracted-file-download-link/';
  new DataHelper(this.get('redux'))
    .initializeData()
    .setAutoDownloadPreference(true)
    .setExtractedFileLink(fileLink);
  this.render(hbs`{{recon-event-actionbar/export-logs accessControl=accessControl}}`);

  const iframe = this.$('.js-export-logs-iframe');
  assert.equal(iframe.length, 1);
  assert.equal(iframe[0].src, fileLink);
});

test('the extracted file must not be downloaded automatically', function(assert) {
  const fileLink = 'http://extracted-file-download-link/';
  new DataHelper(this.get('redux'))
    .initializeData()
    .setAutoDownloadPreference(false)
    .setExtractedFileLink(fileLink);

  patchFlash((flash) => {
    const translation = getOwner(this).lookup('service:i18n');
    const expectedMsg = translation.t('recon.extractedFileReady');
    assert.equal(flash.type, 'success');
    assert.equal(flash.message.string, expectedMsg);
  });

  this.render(hbs`{{recon-event-actionbar/export-logs accessControl=accessControl}}`);

  return wait().then(() => {
    const iframe = this.$('.js-export-logs-iframe');
    assert.equal(iframe.length, 1);
    assert.equal(iframe[0].src, '');
  });
});
