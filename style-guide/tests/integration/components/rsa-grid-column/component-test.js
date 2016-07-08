import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('/rsa-grid-column', 'Integration | Component | rsa-grid-column', {
  integration: true
});

test('it renders', function(assert) {
  this.render(hbs `{{#rsa-grid-column}}foo{{/rsa-grid-column}}`);
  assert.equal(this.$().text().trim(), 'foo');
});

test('it includes the proper classes', function(assert) {
  this.render(hbs `{{#rsa-grid-column}}{{/rsa-grid-column}}`);
  let rowCount = this.$().find('.rsa-grid-column').length;
  assert.equal(rowCount, 1);
});

test('it renders a scroll-box when isScrollable is true', function(assert) {
  this.render(hbs `{{#rsa-grid-column isScrollable=true}}{{/rsa-grid-column}}`);
  let columnCount = this.$().find('.rsa-grid-scroll-box').length;
  assert.equal(columnCount, 1);
});

test('it includes the proper classes when isCollapsed is true', function(assert) {
  this.render(hbs `{{#rsa-grid-column isCollapsed=true}}{{/rsa-grid-column}}`);
  let column = this.$().find('.rsa-grid-column').first();
  assert.ok(column.hasClass('is-collapsed'));
});

test('it includes the proper classes when isNestedGrid is true', function(assert) {
  this.render(hbs `{{#rsa-grid-column isNestedGrid=true}}{{/rsa-grid-column}}`);
  let column = this.$().find('.rsa-grid-column').first();
  assert.ok(column.hasClass('is-nested-grid'));
});

test('it includes the proper class for span=1', function(assert) {
  this.render(hbs `{{#rsa-grid-column span=1}}{{/rsa-grid-column}}`);
  let columnCount = this.$().find('.rsa-grid-column-span-1').length;
  assert.equal(columnCount, 1);
});

test('it includes the proper class for span=2', function(assert) {
  this.render(hbs `{{#rsa-grid-column span=2}}{{/rsa-grid-column}}`);
  let columnCount = this.$().find('.rsa-grid-column-span-2').length;
  assert.equal(columnCount, 1);
});

test('it includes the proper class for span=3', function(assert) {
  this.render(hbs `{{#rsa-grid-column span=3}}{{/rsa-grid-column}}`);
  let columnCount = this.$().find('.rsa-grid-column-span-3').length;
  assert.equal(columnCount, 1);
});

test('it includes the proper class for span=4', function(assert) {
  this.render(hbs `{{#rsa-grid-column span=4}}{{/rsa-grid-column}}`);
  let columnCount = this.$().find('.rsa-grid-column-span-4').length;
  assert.equal(columnCount, 1);
});

test('it includes the proper class for span=5', function(assert) {
  this.render(hbs `{{#rsa-grid-column span=5}}{{/rsa-grid-column}}`);
  let columnCount = this.$().find('.rsa-grid-column-span-5').length;
  assert.equal(columnCount, 1);
});

test('it includes the proper class for span=6', function(assert) {
  this.render(hbs `{{#rsa-grid-column span=6}}{{/rsa-grid-column}}`);
  let columnCount = this.$().find('.rsa-grid-column-span-6').length;
  assert.equal(columnCount, 1);
});

test('it includes the proper class for span=7', function(assert) {
  this.render(hbs `{{#rsa-grid-column span=7}}{{/rsa-grid-column}}`);
  let columnCount = this.$().find('.rsa-grid-column-span-7').length;
  assert.equal(columnCount, 1);
});

test('it includes the proper class for span=8', function(assert) {
  this.render(hbs `{{#rsa-grid-column span=8}}{{/rsa-grid-column}}`);
  let columnCount = this.$().find('.rsa-grid-column-span-8').length;
  assert.equal(columnCount, 1);
});

test('it includes the proper class for span=9', function(assert) {
  this.render(hbs `{{#rsa-grid-column span=9}}{{/rsa-grid-column}}`);
  let columnCount = this.$().find('.rsa-grid-column-span-9').length;
  assert.equal(columnCount, 1);
});

test('it includes the proper class for span=10', function(assert) {
  this.render(hbs `{{#rsa-grid-column span=10}}{{/rsa-grid-column}}`);
  let columnCount = this.$().find('.rsa-grid-column-span-10').length;
  assert.equal(columnCount, 1);
});

test('it includes the proper class for span=11', function(assert) {
  this.render(hbs `{{#rsa-grid-column span=11}}{{/rsa-grid-column}}`);
  let columnCount = this.$().find('.rsa-grid-column-span-11').length;
  assert.equal(columnCount, 1);
});

test('it includes the proper class for span=12', function(assert) {
  this.render(hbs `{{#rsa-grid-column span=12}}{{/rsa-grid-column}}`);
  let columnCount = this.$().find('.rsa-grid-column-span-12').length;
  assert.equal(columnCount, 1);
});

