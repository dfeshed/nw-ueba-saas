import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('/rsa-nav-tab', 'Integration | Component | rsa-nav-tab', {
  integration: true
});

test('it includes the proper classes', function(assert) {
  this.render(hbs `{{rsa-nav-tab}}`);
  const tabCount = this.$().find('.rsa-nav-tab').length;
  assert.equal(tabCount, 1);
});

test('it includes the proper classes', function(assert) {
  this.render(hbs `{{rsa-nav-tab isActive=true}}`);
  const tabCount = this.$().find('.rsa-nav-tab.is-active').length;
  assert.equal(tabCount, 1);
});

test('it includes the proper classes when isCenterAlignedPrimary', function(assert) {
  this.render(hbs `{{rsa-nav-tab align='center'}}`);
  const tabCount = this.$().find('.rsa-nav-tab.is-center-aligned-primary').length;
  assert.equal(tabCount, 1);
});

test('it includes the proper classes when isLeftAlignedPrimary', function(assert) {
  this.render(hbs `{{rsa-nav-tab align='left'}}`);
  const tabCount = this.$().find('.rsa-nav-tab.is-left-aligned-primary').length;
  assert.equal(tabCount, 1);
});

test('it includes the proper classes when isCenterAlignedSecondary', function(assert) {
  this.render(hbs `{{rsa-nav-tab align='center' compact=true}}`);
  const tabCount = this.$().find('.rsa-nav-tab.is-center-aligned-secondary').length;
  assert.equal(tabCount, 1);
});

test('it includes the proper classes when isLeftAlignedSecondary', function(assert) {
  this.render(hbs `{{rsa-nav-tab align='left' compact=true}}`);
  const tabCount = this.$().find('.rsa-nav-tab.is-left-aligned-secondary').length;
  assert.equal(tabCount, 1);
});
