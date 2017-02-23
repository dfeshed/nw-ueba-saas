import { moduleForComponent, test } from 'ember-qunit';
import wait from 'ember-test-helpers/wait';
import hbs from 'htmlbars-inline-precompile';
import DataHelper from '../../../../helpers/data-helper';

const data = {
  eventType: { name: 'LOG' }
};

moduleForComponent('recon-event-actionbar/export-logs', 'Integration | Component | recon event actionbar/export logs', {
  integration: true,
  beforeEach() {
    this.inject.service('redux');
  }
});

test('it renders', function(assert) {
  new DataHelper(this.get('redux')).initializeData();

  this.render(hbs`{{recon-event-actionbar/export-logs}}`);

  return wait().then(() => {
    const str = this.$().text().trim();
    assert.equal(str, '');
  });
});

test('it renders proper label for log data', function(assert) {
  const _data = {
    ...data,
    fileExtractStatus: 'idle'
  };
  new DataHelper(this.get('redux')).initializeData(_data);

  this.render(hbs`{{recon-event-actionbar/export-logs}}`);

  return wait().then(() => {
    const str = this.$().text().trim();
    assert.equal(str, 'Download Logs');
  });
});

test('it renders proper label when downloading log data', function(assert) {
  const _data = {
    ...data,
    fileExtractStatus: 'wait'
  };
  new DataHelper(this.get('redux')).initializeData(_data);

  this.render(hbs`{{recon-event-actionbar/export-logs}}`);

  return wait().then(() => {
    const str = this.$().text().trim();
    assert.equal(str, 'Downloading...');
  });
});
