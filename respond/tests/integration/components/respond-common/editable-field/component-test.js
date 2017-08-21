import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import wait from 'ember-test-helpers/wait';
import { typeOf } from 'ember-utils';

moduleForComponent('respond-common/editable-field', 'Integration | Component | Editable Field', {
  integration: true,
  resolver: engineResolverFor('respond')
});

test('The editable-field renders to the DOM', function(assert) {
  this.render(hbs`{{respond-common/editable-field}}`);
  assert.equal(this.$('.editable-field').length, 1, 'The {{editable-field}} element is found in the DOM');
});

test('Providing a value to the {{editable-field}} renders it as text to the DOM', function(assert) {
  this.set('value', 'Julius Caesar');
  this.render(hbs`{{respond-common/editable-field value=value}}`);
  assert.equal(this.$('.editable-field .editable-field__value').text().trim(), 'Julius Caesar', 'When not in editing mode, the value is output to the DOM');
});

test('Providing a placeholder to the {{editable-field}} renders that text to the DOM when no value is provided', function(assert) {
  this.render(hbs`{{respond-common/editable-field placeholder="Dummy Text"}}`);
  assert.equal(this.$('.editable-field .editable-field__value').text().trim(), 'Dummy Text', 'The placeholder is output to the DOM when there is no value');
});

test('Clicking on the {{editable-field}} places the component into edit mode and transforms the field into an input with the text value from the field', function(assert) {
  this.set('value', 'Julius Caesar');
  this.render(hbs`{{respond-common/editable-field value=value}}`);
  this.$('.editable-field .editable-field__value').click();
  return wait().then(() => {
    assert.equal(this.$('.editable-field input').length, 1, 'The editable-field now contains an input');
    assert.equal(this.$('.editable-field input').val().trim(), 'Julius Caesar', 'The input contains the field value');
  });
});

test('Clicking on the {{editable-field}} places the component into edit mode and transforms the field into a textarea with the text value from the field, if type="textarea" is defined', function(assert) {
  this.set('value', 'Julius Caesar');
  this.render(hbs`{{respond-common/editable-field type='textarea' value=value}}`);
  this.$('.editable-field .editable-field__value').click();
  return wait().then(() => {
    assert.equal(this.$('.editable-field textarea').length, 1, 'The editable-field now contains a textarea');
    assert.equal(this.$('.editable-field textarea').val().trim(), 'Julius Caesar', 'The textarea contains the field value');
  });
});

test('If there is no value on component, we still see a text input for editing on click, but with no value', function(assert) {
  this.render(hbs`{{respond-common/editable-field value=value}}`);
  this.$('.editable-field .editable-field__value').click();
  return wait().then(() => {
    assert.equal(this.$('.editable-field input').length, 1, 'The editable-field now contains an input');
    assert.equal(this.$('.editable-field input').val().trim(), '', 'The input contains no text');
  });
});

test('The {{editable-field}} shows a confirm and cancel button when in edit mode', function(assert) {
  this.render(hbs`{{respond-common/editable-field value=value}}`);
  this.$('.editable-field .editable-field__value').click();
  return wait().then(() => {
    assert.equal(this.$('.editable-field button.rsa-form-button i.rsa-icon-check-2-filled').length, 1, 'The editable-field now contains a confirm button');
    assert.equal(this.$('.editable-field button.rsa-form-button i.rsa-icon-close-filled').length, 1, 'The editable-field now contains a cancel button');
  });
});

test('Changes to the {{editable-field}}\'s input text will be reflected by a has-changes class on the component', function(assert) {
  this.set('value', 'Julius Caesar');
  this.render(hbs`{{respond-common/editable-field value=value}}`);
  this.$('.editable-field .editable-field__value').click();
  this.$('.editable-field input').val('Augustus').change();
  return wait().then(() => {
    assert.equal(this.$('.editable-field input').val().trim(), 'Augustus', 'The input contains the field value');
    assert.equal(this.$('.editable-field.has-changes').length, 1, 'There is a has-changes class on the component');
  });
});

test('The field reverts to non-editing mode if a blur occurs but not changes have been made', function(assert) {
  this.render(hbs`{{respond-common/editable-field value='Julius Caesar'}}`);
  this.$('.editable-field .editable-field__value').click();
  return wait().then(() => {
    this.$('.editable-field input').blur();
    return wait();
  })
  .then(() => {
    assert.equal(this.$('.editable-field input').length, 0, 'The editable field component does not have an input (b/c it is no longer in edit mode');
  });
});

test('The field does not revert to non-editing mode if a blur occurs but changes have been made', function(assert) {
  this.render(hbs`{{respond-common/editable-field value='Julius Caesar'}}`);
  this.$('.editable-field .editable-field__value').click();
  this.$('.editable-field input').val('Hadrian').change();
  this.$('.editable-field input').blur();
  return wait().then(() => {
    assert.equal(this.$('.editable-field input').length, 1, 'The editable field component does have an input (b/c it is still in edit mode)');
  });
});

test('The field does not revert to non-editing mode if a blur occurs but changes have been made, and when the there is a placeholder but no original value', function(assert) {
  this.set('value', null);
  this.set('placeholder', 'Elect your emperor');
  this.render(hbs`{{respond-common/editable-field value=value placeholder=placeholder}}`);
  this.$('.editable-field .editable-field__value').click();
  this.$('.editable-field input').val('Hadrian').change();
  this.$('.editable-field input').blur();
  return wait().then(() => {
    assert.equal(this.$('.editable-field input').length, 1, 'The editable field component does have an input (b/c it is still in edit mode)');
  });
});

test('Clicking the cancel button cancels the changes', function(assert) {
  this.set('value', 'Julius Caesar');
  const editableFieldInputSelector = '.editable-field input';
  this.render(hbs`{{respond-common/editable-field value=value}}`);

  this.$('.editable-field .editable-field__value').click();
  return wait().then(() => {
    assert.equal(this.$(editableFieldInputSelector).val().trim(), 'Julius Caesar', 'The editable field component shows an input with the original value');
    this.$(editableFieldInputSelector).val('Augustus').change();
    return wait();
  })
  .then(() => {
    assert.equal(this.$(editableFieldInputSelector).val(), 'Augustus', 'The value has changed in the input');
    assert.equal(this.$('.editable-field').hasClass('has-changes'), true, 'The component has the "has-changes" class name');
    this.$('.cancel-changes button').click();
    return wait();
  })
  .then(() => {
    assert.equal(this.$('.editable-field .editable-field__value').text().trim(), 'Julius Caesar', 'The value of the edit field is the original value');
    assert.equal(this.$('.editable-field').hasClass('has-changes'), false, 'The component no longer has the "has-changes" class name');
    assert.equal(this.$('.editable-field').hasClass('is-editing'), false, 'The component no longer has the "is-editing" class name');
  });
});

test('Clicking the confirm button returns the component to base (non-editing) mode with the new text', function(assert) {
  assert.expect(7);
  this.set('value', 'Julius Caesar');
  this.on('fieldUpdate', (value, originalValue, revert) => {
    assert.equal(value, 'Augustus', 'The sent action arguments include the new value');
    assert.equal(originalValue, 'Julius Caesar', 'The sent action arguments include the original value');
    assert.equal(typeOf(revert), 'function', 'The sent action argument include the revert callback');
  });
  const editableFieldInputSelector = '.editable-field input';
  this.render(hbs`{{respond-common/editable-field value=value onFieldChange=(action 'fieldUpdate')}}`);
  this.$('.editable-field .editable-field__value').click();
  return wait().then(() => {
    assert.equal(this.$(editableFieldInputSelector).val().trim(), 'Julius Caesar', 'The editable field component shows an input with the original value');
    this.$(editableFieldInputSelector).val('Augustus').change();
    return wait();
  })
  .then(() => {
    this.$('.confirm-changes button').click();
    return wait();
  })
  .then(() => {
    assert.equal(this.$('.editable-field .editable-field__value').text().trim(), 'Augustus', 'The value of the edit field is the new value');
    assert.equal(this.$('.editable-field').hasClass('has-changes'), false, 'The component no longer has the "has-changes" class name');
    assert.equal(this.$('.editable-field').hasClass('is-editing'), false, 'The component no longer has the "is-editing" class name');
  });
});

test('Confirm button is disabled when the value is empty and attribute allowEmptyValue is false', function(assert) {
  assert.expect(2);
  this.set('value', 'Julius Caesar');
  const editableFieldInputSelector = '.editable-field input';
  this.render(hbs`{{respond-common/editable-field value=value allowEmptyValue=false }}`);
  this.$('.editable-field .editable-field__value').click();
  return wait().then(() => {
    assert.equal(this.$(editableFieldInputSelector).val().trim(), 'Julius Caesar', 'The editable field component shows an input with the original value');
    this.$(editableFieldInputSelector).val('').change();
    return wait();
  })
    .then(() => {
      assert.equal(this.$('.confirm-changes .rsa-form-button-wrapper.is-disabled').length, 1, 'The confirm button is disabled');
    });
});

test('A change in the value while the component is in edit mode resets the component to non-edit mode with the new value', function(assert) {
  assert.expect(4);
  this.set('value', 'Julius Caesar');
  const editableFieldInputSelector = '.editable-field input';
  this.render(hbs`{{respond-common/editable-field value=value}}`);
  this.$('.editable-field .editable-field__value').click();
  return wait().then(() => {
    assert.equal(this.$(editableFieldInputSelector).val().trim(), 'Julius Caesar', 'The editable field component shows an input with the original value');
    this.$(editableFieldInputSelector).val('Hadrian').change();
    return wait();
  })
    .then(() => {
      this.set('value', 'Augustus');
      return wait();
    })
    .then(() => {
      assert.equal(this.$('.editable-field .editable-field__value').text().trim(), 'Augustus', 'The value of the edit field is the new value');
      assert.equal(this.$('.editable-field').hasClass('has-changes'), false, 'The component no longer has the "has-changes" class name');
      assert.equal(this.$('.editable-field').hasClass('is-editing'), false, 'The component no longer has the "is-editing" class name');
    });
});
