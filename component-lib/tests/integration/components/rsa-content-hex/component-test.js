import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('rsa-content-hex', 'Integration | Component | rsa content hex', {
  integration: true,
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
  }
});

test('should display blank', function(assert) {
  this.set('value', '');
  this.render(hbs`{{rsa-content-hex value=value}}`);
  assert.equal(this.$().text().trim(), '');
});

test('should display 0', function(assert) {
  this.set('value', 0);
  this.render(hbs`{{rsa-content-hex value=value}}`);
  assert.equal(this.$().text().trim(), 0);
});

test('should convert into hex for positive decimal', function(assert) {
  this.set('value', 35176284735872);
  this.render(hbs`{{rsa-content-hex value=value}}`);
  assert.equal(this.$().text().trim(), '0x1FFE1DF4C980');
});

test('should convert into hex for negative decimal', function(assert) {
  this.set('value', -35176284735872);
  this.render(hbs`{{rsa-content-hex value=value}}`);
  assert.equal(this.$().text().trim(), '-0x1FFE1DF4C980');
});
