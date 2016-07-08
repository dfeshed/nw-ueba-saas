import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('/rsa-grid-row', 'Integration | Component | rsa-grid-row', {
  integration: true
});

test('it renders', function(assert) {
  this.render(hbs `{{#rsa-grid-row}}foo{{/rsa-grid-row}}`);
  assert.equal(this.$().text().trim(), 'foo');
});

test('it includes the proper classes', function(assert) {
  this.render(hbs `{{#rsa-grid-row}}{{/rsa-grid-row}}`);
  let rowCount = this.$().find('.rsa-grid-row').length;
  assert.equal(rowCount, 1);
});

test('it renders a full width column when fullWidth is true', function(assert) {
  this.render(hbs `{{#rsa-grid-row fullWidth=true}}{{/rsa-grid-row}}`);
  let columnCount = this.$().find('.rsa-grid-column-span-12').length;
  assert.equal(columnCount, 1);
});

test('it includes the proper class for collapseHeight', function(assert) {
  this.render(hbs `{{#rsa-grid-row collapseHeight=true}}{{/rsa-grid-row}}`);
  let rowCount = this.$().find('.collapse-height').length;
  assert.equal(rowCount, 1);
});
