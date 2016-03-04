import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('/rsa-grid', 'Integration | Component | rsa-grid', {
  integration: true
});

test('it renders', function(assert) {
  this.render(hbs `{{#rsa-grid}}foo{{/rsa-grid}}`);
  assert.equal(this.$().text().trim(), 'foo');
});

test('it includes the proper classes', function(assert) {
  this.render(hbs `{{#rsa-grid}}{{/rsa-grid}}`);
  let gridCount = this.$().find('.rsa-grid').length;
  assert.equal(gridCount, 1);
});

test('it includes the proper classes when pageView is true', function(assert) {
  this.render(hbs `{{#rsa-grid isPageView=true}}{{/rsa-grid}}`);
  let grid = this.$().find('.rsa-grid').first();
  assert.ok(grid.hasClass('is-page-view'));
});
