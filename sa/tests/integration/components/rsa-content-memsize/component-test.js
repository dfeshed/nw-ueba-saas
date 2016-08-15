import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import Ember from 'ember';

const { Object: EmberObject } = Ember;

const i18n = EmberObject.extend({
  t() {
    return 'bytes';
  }
}).create();

moduleForComponent('rsa-content-memsize', 'Integration | Component | rsa content memsize', {
  integration: true
});

test('it renders with the correct class', function(assert) {

  this.set('i18n', i18n);
  this.render(hbs`{{rsa-content-memsize i18n=i18n}}`);

  assert.equal(this.$('.rsa-content-memsize').length, 1);

});

test('it renders less than 1024 bytes in bytes', function(assert) {

  this.set('i18n', i18n);
  this.render(hbs`{{rsa-content-memsize i18n=i18n size=10}}`);

  assert.equal(this.$('.size').text().trim(), 10);
  assert.equal(this.$('.rsa-content-memsize').attr('title').trim(), '10 bytes');

});

test('it renders 1024 bytes or more in KBs', function(assert) {

  this.set('i18n', i18n);
  this.set('size', 1024);
  this.render(hbs`{{rsa-content-memsize i18n=i18n size=size}}`);

  assert.equal(this.$('.size').text().trim().indexOf('1.'), 0);
  assert.equal(this.$('.rsa-content-memsize').attr('title').trim(), '1024 bytes');

  this.set('size', 2048);

  assert.equal(this.$('.size').text().trim().indexOf('2.'), 0);
  assert.equal(this.$('.rsa-content-memsize').attr('title').trim(), '2048 bytes');

});

