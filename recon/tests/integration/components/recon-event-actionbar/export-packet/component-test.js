import { moduleForComponent, test } from 'ember-qunit';
import wait from 'ember-test-helpers/wait';
import hbs from 'htmlbars-inline-precompile';
import DataHelper from '../../../../helpers/data-helper';

const data = {
  eventType: { name: 'NETWORK' }
};

moduleForComponent('recon-event-actionbar/export-packet', 'Integration | Component | recon event actionbar/export packet', {
  integration: true,
  beforeEach() {
    this.registry.injection('component:recon-event-actionbar/export-packet', 'i18n', 'service:i18n');
    this.inject.service('redux');
  }
});

test('it renders', function(assert) {
  new DataHelper(this.get('redux')).initializeData();

  this.render(hbs`{{recon-event-actionbar/export-packet}}`);

  return wait().then(() => {
    const str = this.$()[0].innerText.trim();
    assert.equal(str, 'Download PCAP');
  });
});

test('the menu renders properly and has the correct labels for export pcap menu', function(assert) {
  const _data = {
    ...data,
    fileExtractStatus: 'idle'
  };
  new DataHelper(this.get('redux')).initializeData(_data);

  this.render(hbs`{{recon-event-actionbar/export-packet}}`);
  this.$('.rsa-split-dropdown').find('.rsa-form-button').click();

  return wait().then(() => {
    const ulEl = this.$('.rsa-split-dropdown').next();
    const ulElChildren = ulEl.children();

    assert.equal(ulEl.hasClass('expanded'), true);
    assert.equal(ulElChildren[0].innerText.trim(), 'Download PCAP');
    assert.equal(ulElChildren[1].innerText.trim(), 'Download All Payloads');
    assert.equal(ulElChildren[2].innerText.trim(), 'Download Request Payload');
    assert.equal(ulElChildren[3].innerText.trim(), 'Download Response Payload');
  });
});

test('it renders proper label when export pcap data', function(assert) {
  new DataHelper(this.get('redux'))
    .initializeData(data)
    .startDownloadingData();

  this.render(hbs`{{recon-event-actionbar/export-packet}}`);

  return wait().then(() => {
    const str = this.$()[0].innerText.trim();
    assert.equal(str, 'Downloading...');
  });
});