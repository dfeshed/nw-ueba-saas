import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('recon-event-header', 'Integration | Component | recon event header', {
  integration: true
});

test('headerItems render correctly', function(assert) {
  this.set('headerItems', [{ name: 'foo', value: 'bar' }, { name: 'bar', value: 'baz' }]);
  this.render(hbs`{{recon-event-header headerItems=headerItems}}`);

  assert.equal(this.$('.header-item').length, 2);
  assert.equal(this.$('.header-item .name').first().text().trim(), 'foo');
  assert.equal(this.$('.header-item .value').first().text().trim(), 'bar');
});

test('showHeaderData true shows header items', function(assert) {
  this.set('headerItems', [{ name: 'foo', value: 'bar' }, { name: 'bar', value: 'baz' }]);
  this.set('showHeaderData', true);
  this.render(hbs`{{recon-event-header headerItems=headerItems showHeaderData=showHeaderData}}`);

  assert.equal(this.$('.header-item').length, 2);
  assert.equal(this.$('.header-item .name').first().text().trim(), 'foo');
  assert.equal(this.$('.header-item .value').first().text().trim(), 'bar');
});

test('showHeaderData false hides header items', function(assert) {
  this.set('headerItems', [{ name: 'foo', value: 'bar' }, { name: 'bar', value: 'baz' }]);
  this.set('showHeaderData', false);
  this.render(hbs`{{recon-event-header headerItems=headerItems showHeaderData=showHeaderData}}`);

  assert.equal(this.$('.header-item').length, 0);
});
