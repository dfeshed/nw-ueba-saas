import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('range-slider', 'Integration | Component | range-slider', {
  integration: true
});

test('it renders', function(assert) {
  this.set('start', [25, 75]);
  this.set('steps', 1);
  this.render(hbs `{{range-slider start=start step=steps}}`);
  assert.equal(this.$('.noUi-target').length, 1, 'Could not find the component root DOM element');
});

test('it includes proper classes', function(assert) {
  this.set('start', [25, 75]);
  this.set('steps', 1);
  this.render(hbs `{{range-slider start=start step=steps}}`);
  assert.equal(this.$('.noUi-base').length, 1, 'Testing to see if the .noUi-base class exists');
  assert.equal(this.$('.noUi-handle-lower').length, 1, 'Testing to see if the .noUi-handle-lower class exists');
  assert.equal(this.$('.noUi-handle-upper').length, 1, 'Testing to see if the .noUi-handle-upper class exists');
  assert.equal(this.$('.noUi-tooltip').length, 2, 'Testing to see if the .noUi-tooltip class exists for all handles');
});

test('it includes the proper classes when isReadOnly is true', function(assert) {
  this.set('start', [25, 75]);
  this.set('steps', 1);
  /* There is no isReadOnly attr in the range-slider addon, whenever using isReadOnly, make sure to set
  disabled flag to true as well */
  this.set('isReadOnly', true);
  this.set('disabled', true);
  this.render(hbs `{{range-slider start=start step=steps isReadOnly=isReadOnly disabled=disabled}}`);
  assert.equal(this.$('.is-read-only').length, 1, 'Testing to see if the .is-read-only class exists');
});

test('it includes the proper classes when disabled is true', function(assert) {
  this.set('start', [25, 75]);
  this.set('steps', 1);
  this.set('disabled', true);
  this.render(hbs `{{range-slider start=start step=steps disabled=disabled}}`);
  assert.equal(this.$('.is-disabled').length, 1, 'Testing to see if the .is-disabled class exists');
});

test('it includes the proper classes when isError is true', function(assert) {
  this.set('start', [25, 75]);
  this.set('steps', 1);
  this.set('isError', true);
  this.render(hbs `{{range-slider start=start step=steps isError=isError}}`);
  assert.equal(this.$('.is-error').length, 1, 'Testing to see if the .is-error class exists');
});

test('it sets the handle positions correctly based on default values in the start array', function(assert) {
  this.set('start', [25, 75]);
  this.set('steps', 1);
  this.render(hbs `{{range-slider start=start step=steps}}`);
  assert.equal(this.$('.noUi-handle-lower').text().trim(), this.get('start')[0], 'Lower handle default value is incorrect');
  assert.equal(this.$('.noUi-handle-upper').text().trim(), this.get('start')[1], 'Upper handle default value is incorrect');
});