import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('rsa-round-decimal', 'Integration | Component | rsa-round-decimal', {
  integration: true,
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
  }
});

test('should display 199 as 199.00', function(assert) {
  this.set('value', 199);
  this.render(hbs`{{rsa-round-decimal value=value}}`);
  assert.equal(this.$().text().trim(), '199.00');
});

test('should display 100.53345 as 100.533', function(assert) {
  this.set('value', 100.533456);
  this.set('digits', 3);
  this.render(hbs`{{rsa-round-decimal value=value digits=digits}}`);
  assert.equal(this.$().text().trim(), '100.533');
});

test('should display 50.2356 as 50.236', function(assert) {
  this.set('value', 50.2356);
  this.set('digits', 3);
  this.render(hbs`{{rsa-round-decimal value=value digits=digits}}`);
  assert.equal(this.$().text().trim(), '50.236');
});

