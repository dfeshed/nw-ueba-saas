import wait from 'ember-test-helpers/wait';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import DataHelper from '../../../helpers/data-helper';

moduleForComponent('recon-event-header', 'Integration | Component | recon event header', {
  integration: true,
  beforeEach() {
    this.registry.injection('component:recon-event-actionbar/export-packet', 'i18n', 'service:i18n');
    this.inject.service('redux');
    initialize(this);
  }
});

test('headerItems render correctly', function(assert) {
  new DataHelper(this.get('redux'))
    .setViewToText()
    .initializeData();
  this.render(hbs`{{recon-event-header}}`);
  return wait().then(() => {
    assert.equal(this.$('.header-item').length, 10);
    assert.equal(this.$('.header-item .name').first().text().trim(), 'NW Service');
    assert.equal(this.$('.header-item .value').first().text().trim(), 'concentrator');
  });
});

test('headerItems with value of zero render correctly', function(assert) {
  new DataHelper(this.get('redux'))
    .setViewToText()
    .initializeData();
  this.render(hbs`{{recon-event-header}}`);
  return wait().then(() => {
    assert.equal(this.$('.header-item:nth-child(5) .name').text().trim(), 'Service');
    assert.equal(this.$('.header-item:nth-child(5) .value').text().trim(), '0');
  });
});

test('isHeaderOpen can toggle header visibility', function(assert) {
  const dataHelper = new DataHelper(this.get('redux'))
    .setViewToText()
    .initializeData()
    .toggleHeader();
  this.render(hbs`{{recon-event-header}}`);
  return wait().then(() => {
    assert.equal(this.$('.header-item').length, 0);
    dataHelper.toggleHeader();
    return wait().then(() => {
      assert.equal(this.$('.header-item').length, 10);
    });
  });
});
