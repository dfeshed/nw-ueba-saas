import wait from 'ember-test-helpers/wait';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

import DataHelper from '../../../helpers/data-helper';

moduleForComponent('recon-event-header', 'Integration | Component | recon event header', {
  integration: true,
  setup() {
    this.inject.service('redux');
  }
});

test('headerItems render correctly', function(assert) {
  new DataHelper(this.get('redux')).initializeData();
  this.render(hbs`{{recon-event-header}}`);
  return wait().then(() => {
    assert.equal(this.$('.header-item').length, 12);
    assert.equal(this.$('.header-item .name').first().text().trim(), 'device');
    assert.equal(this.$('.header-item .value').first().text().trim(), 'devicename');
  });
});

test('isHeaderOpen can toggle header visibility', function(assert) {
  const dataHelper = new DataHelper(this.get('redux'))
    .initializeData()
    .toggleHeader();

  this.render(hbs`{{recon-event-header}}`);
  return wait().then(() => {
    assert.equal(this.$('.header-item').length, 0);
    dataHelper.toggleHeader();
    return wait().then(() => {
      assert.equal(this.$('.header-item').length, 12);
    });
  });
});