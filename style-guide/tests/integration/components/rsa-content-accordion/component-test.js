import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('/rsa-content-accordion', 'Integration | Component | rsa-content-accordion', {
  integration: true
});

test('it includes the proper classes', function(assert) {
  this.render(hbs `{{#rsa-content-accordion}}foo{{/rsa-content-accordion}}`);
  let accordianCount = this.$().find('.rsa-content-accordion').length;
  assert.equal(accordianCount, 1);
});

test('it sets the label', function(assert) {
  this.render(hbs `{{#rsa-content-accordion label="bar"}}foo{{/rsa-content-accordion}}`);
  let label = this.$().find('h3').text();
  assert.notEqual(label.indexOf('bar'), -1);
});

test('it includes the proper classes when isCollapsed is true', function(assert) {
  this.render(hbs `{{#rsa-content-accordion isCollapsed=true}}foo{{/rsa-content-accordion}}`);
  let accordian = this.$().find('.rsa-content-accordion').first();
  assert.ok(accordian.hasClass('is-collapsed'));
});

test('it toggles content visibility when clicked', function(assert) {
  this.render(hbs `{{#rsa-content-accordion isCollapsed=true}}foo{{/rsa-content-accordion}}`);
  let accordianCount = this.$().find('.is-collapsed').length;
  assert.equal(accordianCount, 1);
  this.$().find('h3').click();
  let accordian = this.$().find('.rsa-content-accordion');
  assert.notOk(accordian.hasClass('is-collapsed'));
});
