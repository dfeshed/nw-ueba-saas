import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('rsa-form-slider', 'Integration | Component | rsa-form-slider', {
  integration: true
});

test('it renders', function(assert) {
  this.set('start', [25,75]);
  this.set('range', {
    'min': [0],
    'max': [100]
  });
  this.set('steps', 1);
  this.render(hbs `{{rsa-form-slider start=start range=range step=steps}}`);
  assert.equal(this.$('.rsa-form-slider').length, 1, 'Could not find the component root DOM element');
});

test('it includes proper classes', function(assert) {
  this.set('start', [25,75]);
  this.set('range', {
    'min': [0],
    'max': [100]
  });
  this.set('steps', 1);
  this.render(hbs `{{rsa-form-slider start=start range=range step=steps}}`);
  assert.equal(this.$('.noUi-base').length, 1, 'Testing to see if the .noUi-base class exists');
  assert.equal(this.$('.noUi-handle-lower').length, 1, 'Testing to see if the .noUi-handle-lower class exists');
  assert.equal(this.$('.noUi-handle-upper').length, 1, 'Testing to see if the .noUi-handle-upper class exists');
  assert.equal(this.$('.noUi-tooltip').length, 2, 'Testing to see if the .noUi-tooltip class exists for all handles');
});

test('it includes the proper classes when isReadOnly is true', function(assert) {
  this.set('start', [25,75]);
  this.set('range', {
    'min': [0],
    'max': [100]
  });
  this.set('steps', 1);
  /* There is no isReadOnly attr in the range-slider addon, whenever using isReadOnly, make sure to set
  isDisabled flag to true as well */
  this.set('isReadOnly', true);
  this.set('isDisabled', true);
  this.render(hbs `{{rsa-form-slider start=start range=range step=steps isReadOnly=isReadOnly isDisabled=isDisabled}}`);
  assert.equal(this.$('.is-read-only').length, 1, 'Testing to see if the .is-read-only class exists');
});

test('it includes the proper classes when isDisabled is true', function(assert) {
  this.set('start', [25,75]);
  this.set('range', {
    'min': [0],
    'max': [100]
  });
  this.set('steps', 1);
  this.set('isDisabled', true);
  this.render(hbs `{{rsa-form-slider start=start range=range step=steps isDisabled=isDisabled}}`);
  assert.equal(this.$('.is-disabled').length, 1, 'Testing to see if the .is-disabled class exists');
});

test('it includes the proper classes when isError is true', function(assert) {
  this.set('start', [25,75]);
  this.set('range', {
    'min': [0],
    'max': [100]
  });
  this.set('steps', 1);
  this.set('isError', true);
  this.render(hbs `{{rsa-form-slider start=start range=range step=steps isError=isError}}`);
  assert.equal(this.$('.is-error').length, 1, 'Testing to see if the .is-error class exists');
});

test('it sets the handle positions correctly based on default values in the start array', function(assert) {
  this.set('start', [25,75]);
  this.set('range', {
    'min': [0],
    'max': [100]
  });
  this.set('steps', 1);
  this.render(hbs `{{rsa-form-slider start=start range=range step=steps}}`);
  assert.equal(this.$('.noUi-handle-lower').text().trim(), this.get('start')[0], 'Lower handle default value is incorrect');
  assert.equal(this.$('.noUi-handle-upper').text().trim(), this.get('start')[1], 'Upper handle default value is incorrect');
});