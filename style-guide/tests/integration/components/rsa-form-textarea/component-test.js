import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('/rsa-form-textarea', 'Integration | Component | rsa-form-textarea', {
  integration: true
});

test('it renders', function(assert) {
  this.render(hbs `{{rsa-form-textarea}}`);
  assert.equal(this.$().find('textarea').length, 1);
});

test('it is a label', function(assert) {
  this.render(hbs `{{rsa-form-textarea}}`);
  const labelCount = this.$().find('label').length === 1;
  assert.equal(labelCount, 1);
});

test('it includes the proper classes', function(assert) {
  this.render(hbs `{{rsa-form-textarea}}`);
  const label = this.$().find('label').first();
  assert.ok(label.hasClass('rsa-form-textarea'));
});

test('sets the value', function(assert) {
  this.render(hbs `{{rsa-form-textarea value='foo'}}`);
  const input = this.$().find('textarea').first();
  assert.ok(input.val('foo'));
});

test('sets the label', function(assert) {
  this.render(hbs `{{rsa-form-textarea label='foo'}}`);
  const label = this.$().find('.rsa-form-label').first();
  assert.equal(label.text(), 'foo');
});

test('it includes the proper classes when isDisabled is true', function(assert) {
  this.render(hbs `{{rsa-form-textarea isDisabled=true}}`);
  const label = this.$().find('label').first();
  assert.ok(label.hasClass('is-disabled'));
});

test('it includes the proper classes when isReadOnly is true', function(assert) {
  this.render(hbs `{{rsa-form-textarea isReadOnly=true}}`);
  const label = this.$().find('label').first();
  assert.ok(label.hasClass('is-read-only'));
});

test('it includes the proper classes when isError is true', function(assert) {
  this.render(hbs `{{rsa-form-textarea isError=true}}`);
  const label = this.$().find('label').first();
  assert.ok(label.hasClass('is-error'));
});

test('it includes the proper classes when isSuccess is true', function(assert) {
  this.render(hbs `{{rsa-form-textarea isSuccess=true}}`);
  const label = this.$().find('label').first();
  assert.ok(label.hasClass('is-success'));
});

test('it renders a placeholder', function(assert) {
  this.render(hbs `{{rsa-form-textarea placeholder='foo'}}`);
  const textarea = this.$().find('textarea').first();
  assert.equal(textarea.attr('placeholder'), 'foo');
});

test('it can be disabled', function(assert) {
  this.render(hbs `{{rsa-form-textarea isDisabled=true}}`);
  const disabledCount = this.$().find('textarea[disabled]').length === 1;
  assert.equal(disabledCount, 1);
});

test('it is disabled when isReadOnly', function(assert) {
  this.render(hbs `{{rsa-form-textarea isReadOnly=true}}`);
  const disabledCount = this.$().find('textarea[disabled]').length === 1;
  assert.equal(disabledCount, 1);
});

test('it calls closure action focusIn', function(assert) {
  assert.expect(1);
  this.on('focus', () => {
    assert.ok(true);
  });
  this.render(hbs `{{rsa-form-textarea focusIn=(action 'focus')}}`);
  this.$('textarea').focus();
});

test('it calls closure action focusOut', function(assert) {
  assert.expect(1);
  this.on('blur', () => {
    assert.ok(true);
  });
  this.render(hbs `{{rsa-form-textarea focusOut=(action 'blur')}}`);
  this.$('textarea').blur();
});
