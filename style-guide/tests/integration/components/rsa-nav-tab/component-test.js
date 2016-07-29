import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('/rsa-nav-tab', 'Integration | Component | rsa-nav-tab', {
  integration: true
});

test('it includes the proper classes', function(assert) {
  this.render(hbs `{{rsa-nav-tab}}`);
  let tabCount = this.$().find('.rsa-nav-tab').length;
  assert.equal(tabCount, 1);
});

test('it includes the proper classes', function(assert) {
  this.render(hbs `{{rsa-nav-tab isActive=true}}`);
  let tabCount = this.$().find('.rsa-nav-tab.is-active').length;
  assert.equal(tabCount, 1);
});
