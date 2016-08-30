import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('/rsa-form-select', 'Integration | Component | rsa-form-select', {
  integration: true
});

test('it renders', function(assert) {
  this.render(hbs `{{rsa-form-select label='Foo'}}`);
  assert.equal(this.$().find('select').length, 1);
});

test('it has a label', function(assert) {
  this.render(hbs `{{rsa-form-select label='Foo'}}`);
  let label = this.$().find('.rsa-form-label').text();
  assert.equal(label, 'Foo');
});

test('it includes the proper classes', function(assert) {
  this.render(hbs `{{rsa-form-select label='Foo'}}`);
  assert.equal(this.$().find('.rsa-form-select').length, 1);
});

test('it includes the proper classes when isSmall is true', function(assert) {
  this.render(hbs `{{rsa-form-select label='Foo' isSmall=true}}`);
  assert.ok(this.$().find('.rsa-form-select').hasClass('is-small'));
});

test('it includes the proper classes when isInline is true', function(assert) {
  this.render(hbs `{{rsa-form-select label='Foo' isInline=true}}`);
  assert.ok(this.$().find('.rsa-form-select').hasClass('is-inline'));
});

test('it includes the proper classes when isDisabled is true', function(assert) {
  this.render(hbs `{{rsa-form-select label='Foo' isDisabled=true}}`);
  assert.ok(this.$().find('.rsa-form-select').hasClass('is-disabled'));
});

test('it includes the proper classes when isReadOnly is true', function(assert) {
  this.render(hbs `{{rsa-form-select label='Foo' isReadOnly=true}}`);
  assert.ok(this.$().find('.rsa-form-select').hasClass('is-read-only'));
});

test('it includes the proper classes when isError is true', function(assert) {
  this.render(hbs `{{rsa-form-select label='Foo' isError=true}}`);
  assert.ok(this.$().find('.rsa-form-select').hasClass('is-error'));
});

test('it includes the proper classes when optionsCollapsed is true', function(assert) {
  this.render(hbs `{{rsa-form-select label='Foo' optionsCollapsed=true}}`);
  assert.ok(this.$().find('.rsa-form-select').hasClass('options-collapsed'));
});

test('it renders the prompt when no values', function(assert) {
  this.render(hbs `{{rsa-form-select label='Foo' isError=true prompt='Foo Prompt'}}`);
  assert.ok(this.$().find('.prompt').text('Foo Prompt'));
});

test('it renders the values when there are values', function(assert) {
  this.set('testValues', ['2']);
  this.render(hbs `{{#rsa-form-select label='Foo' prompt='Foo Prompt' values=testValues}}<option value="1">1</option><option value="2">2</option><option value="3">3</option>{{/rsa-form-select}}`);
  assert.ok(this.$().find('.prompt').text('2'));
});

test('it included the proper class when there are multiple values', function(assert) {
  this.set('testValues', ['2', '3']);
  this.render(hbs `{{#rsa-form-select label='Foo' prompt='Foo Prompt' values=testValues}}<option value="1">1</option><option value="2">2</option><option value="3">3</option>{{/rsa-form-select}}`);
  assert.ok(this.$().find('.rsa-form-select').hasClass('has-multiple-values'));
});

test('it included the proper class when there is only one value', function(assert) {
  this.set('testValues', ['2']);
  this.render(hbs `{{#rsa-form-select label='Foo' prompt='Foo Prompt' values=testValues}}<option value="1">1</option><option value="2">2</option><option value="3">3</option>{{/rsa-form-select}}`);
  assert.ok(this.$().find('.rsa-form-select').hasClass('has-single-value'));
});

test('it renders the option tags when there are multiple values', function(assert) {
  this.set('testValues', ['2', '3']);
  this.render(hbs `{{#rsa-form-select label='Foo' prompt='Foo Prompt' values=testValues}}<option value="1">1</option><option value="2">2</option><option value="3">3</option>{{/rsa-form-select}}`);
  assert.equal(this.$().find('.option-tag').length, 2);
});

test('it selects the correct option with a single value', function(assert) {
  this.set('testValues', ['2']);
  this.render(hbs `{{#rsa-form-select label='Foo' prompt='Foo Prompt' values=testValues}}<option value="1">1</option><option value="2">2</option><option value="3">3</option>{{/rsa-form-select}}`);
  assert.equal(this.$().find('option:checked').text(), '2');
});

test('it selects all options with multiple values', function(assert) {
  this.set('testValues', ['2', '3']);
  this.render(hbs `{{#rsa-form-select label='Foo' prompt='Foo Prompt' values=testValues}}<option value="1">1</option><option value="2">2</option><option value="3">3</option>{{/rsa-form-select}}`);
  assert.equal(this.$().find('option:checked').length, 2);
});

test('it updates when the value changes', function(assert) {
  this.set('testValues', ['2']);
  this.render(hbs `{{#rsa-form-select label='Foo' prompt='Foo Prompt' values=testValues}}<option value="1">1</option><option value="2">2</option><option value="3">3</option>{{/rsa-form-select}}`);
  assert.equal(this.$().find('option:checked').val(), '2');
  this.set('testValues', ['1']);
  assert.equal(this.$().find('option:checked').val(), '1');
  assert.equal(this.$().find('select').val(), '1');
});

test('it update the bound value when the selected value changes to a single value', function(assert) {
  this.set('testValues', ['2']);
  this.render(hbs `{{#rsa-form-select label='Foo' prompt='Foo Prompt' values=testValues}}<option value="1">1</option><option value="2">2</option><option value="3">3</option>{{/rsa-form-select}}`);
  this.$().find('.prompt').click();
  this.$().find('select').val('1').trigger('change');
  assert.equal(this.get('testValues.firstObject'), '1');
});

test('it update the bound value when the selected value changes to multiple values', function(assert) {
  this.set('testValues', ['2']);
  this.render(hbs `{{#rsa-form-select label='Foo' prompt='Foo Prompt' values=testValues}}<option value="1">1</option><option value="2">2</option><option value="3">3</option>{{/rsa-form-select}}`);
  this.$().find('.prompt').click();
  this.$().find('select').val(['1', '3']).trigger('change');
  assert.equal(this.get('testValues.length'), '2');
  assert.equal(this.get('testValues.firstObject'), '1');
  assert.equal(this.get('testValues.lastObject'), '3');
});

test('it removes values when clicking the icon on the option tag', function(assert) {
  this.set('testValues', ['1','2','3']);
  this.render(hbs `{{#rsa-form-select label='Foo' prompt='Foo Prompt' values=testValues}}<option value="1">1</option><option value="2">2</option><option value="3">3</option>{{/rsa-form-select}}`);
  this.$().find('.prompt').click();
  this.$().find('.option-tag:first-of-type .remove-tag').click();
  assert.equal(this.get('testValues.length'), '2');
  assert.equal(this.get('testValues.firstObject'), '2');
});

test('it allows to select only one value when multiple is disabled', function(assert) {
  this.set('testValues', ['2']);
  this.render(hbs `{{#rsa-form-select multiple=false label='Foo' prompt='Foo Prompt' values=testValues}}<option value="1">1</option><option value="2">2</option><option value="3">3</option>{{/rsa-form-select}}`);
  this.$().find('.prompt').click();
  this.$().find('select').val(['1', '3']).trigger('change');
  assert.equal(this.get('testValues.length'), '1');
  assert.equal(this.get('testValues.firstObject'), '1');
});