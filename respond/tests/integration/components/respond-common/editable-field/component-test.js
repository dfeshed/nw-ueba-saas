import { blur, click, fillIn, find, findAll, render, settled } from '@ember/test-helpers';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import { typeOf } from '@ember/utils';

module('Integration | Component | Editable Field', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  test('The editable-field renders to the DOM', async function(assert) {
    await render(hbs`{{respond-common/editable-field}}`);
    assert.equal(findAll('.editable-field').length, 1, 'The {{editable-field}} element is found in the DOM');
  });

  test('Providing a value to the {{editable-field}} renders it as text to the DOM', async function(assert) {
    this.set('value', 'Julius Caesar');
    await render(hbs`{{respond-common/editable-field value=value}}`);
    assert.equal(find('.editable-field .editable-field__value').textContent.trim(), 'Julius Caesar', 'When not in editing mode, the value is output to the DOM');
  });

  test('Providing a placeholder to the {{editable-field}} renders that text to the DOM when no value is provided', async function(assert) {
    await render(hbs`{{respond-common/editable-field placeholder="Dummy Text"}}`);
    assert.equal(find('.editable-field .editable-field__value').textContent.trim(), 'Dummy Text', 'The placeholder is output to the DOM when there is no value');
  });

  test('Clicking on the {{editable-field}} places the component into edit mode and transforms the field into an input with the text value from the field', async function(assert) {
    this.set('value', 'Julius Caesar');
    await render(hbs`{{respond-common/editable-field value=value}}`);
    await click('.editable-field .editable-field__value');
    return settled().then(() => {
      assert.equal(findAll('.editable-field input').length, 1, 'The editable-field now contains an input');
      assert.equal(find('.editable-field input').value.trim(), 'Julius Caesar', 'The input contains the field value');
    });
  });

  test('Clicking on the {{editable-field}} places the component into edit mode and transforms the field into a textarea with the text value from the field, if type="textarea" is defined', async function(assert) {
    this.set('value', 'Julius Caesar');
    await render(hbs`{{respond-common/editable-field type='textarea' value=value}}`);
    await click('.editable-field .editable-field__value');
    return settled().then(() => {
      assert.equal(findAll('.editable-field textarea').length, 1, 'The editable-field now contains a textarea');
      assert.equal(find('.editable-field textarea').value.trim(), 'Julius Caesar', 'The textarea contains the field value');
    });
  });

  test('If there is no value on component, we still see a text input for editing on click, but with no value', async function(assert) {
    await render(hbs`{{respond-common/editable-field value=value}}`);
    await click('.editable-field .editable-field__value');
    return settled().then(() => {
      assert.equal(findAll('.editable-field input').length, 1, 'The editable-field now contains an input');
      assert.equal(find('.editable-field input').value.trim(), '', 'The input contains no text');
    });
  });

  test('The {{editable-field}} shows a confirm and cancel button when in edit mode', async function(assert) {
    await render(hbs`{{respond-common/editable-field value=value}}`);
    await click('.editable-field .editable-field__value');
    return settled().then(() => {
      assert.equal(findAll('.editable-field button.rsa-form-button i.rsa-icon-check-2-filled').length, 1, 'The editable-field now contains a confirm button');
      assert.equal(findAll('.editable-field button.rsa-form-button i.rsa-icon-close-filled').length, 1, 'The editable-field now contains a cancel button');
    });
  });

  test('Changes to the {{editable-field}}\'s input text will be reflected by a has-changes class on the component', async function(assert) {
    this.set('value', 'Julius Caesar');
    await render(hbs`{{respond-common/editable-field value=value}}`);
    await click('.editable-field .editable-field__value');
    await fillIn('.editable-field input', 'Augustus');
    await blur('.editable-field input');
    return settled().then(() => {
      assert.equal(find('.editable-field input').value.trim(), 'Augustus', 'The input contains the field value');
      assert.equal(findAll('.editable-field.has-changes').length, 1, 'There is a has-changes class on the component');
    });
  });

  test('The field reverts to non-editing mode if a blur occurs but not changes have been made', async function(assert) {
    await render(hbs`{{respond-common/editable-field value='Julius Caesar'}}`);
    await click('.editable-field .editable-field__value');
    return settled().then(() => {
      this.$('.editable-field input').blur();
      return settled();
    })
    .then(() => {
      assert.equal(findAll('.editable-field input').length, 0, 'The editable field component does not have an input (b/c it is no longer in edit mode');
    });
  });

  test('The field does not revert to non-editing mode if a blur occurs but changes have been made', async function(assert) {
    await render(hbs`{{respond-common/editable-field value='Julius Caesar'}}`);
    await click('.editable-field .editable-field__value');
    await fillIn('.editable-field input', 'Hadrian');
    await blur('.editable-field input');
    this.$('.editable-field input').blur();
    return settled().then(() => {
      assert.equal(findAll('.editable-field input').length, 1, 'The editable field component does have an input (b/c it is still in edit mode)');
    });
  });

  test('The field does not revert to non-editing mode if a blur occurs but changes have been made, and when the there is a placeholder but no original value', async function(assert) {
    this.set('value', null);
    this.set('placeholder', 'Elect your emperor');
    await render(hbs`{{respond-common/editable-field value=value placeholder=placeholder}}`);
    await click('.editable-field .editable-field__value');
    await fillIn('.editable-field input', 'Hadrian');
    await blur('.editable-field input');
    this.$('.editable-field input').blur();
    return settled().then(() => {
      assert.equal(findAll('.editable-field input').length, 1, 'The editable field component does have an input (b/c it is still in edit mode)');
    });
  });

  test('Clicking the cancel button cancels the changes', async function(assert) {
    this.set('value', 'Julius Caesar');
    const editableFieldInputSelector = '.editable-field input';
    await render(hbs`{{respond-common/editable-field value=value}}`);

    await click('.editable-field .editable-field__value');
    return settled().then(async () => {
      assert.equal(find(editableFieldInputSelector).value.trim(), 'Julius Caesar', 'The editable field component shows an input with the original value');
      await fillIn(editableFieldInputSelector, 'Augustus');
      await blur(editableFieldInputSelector);
      return settled();
    })
    .then(async () => {
      assert.equal(find(editableFieldInputSelector).value, 'Augustus', 'The value has changed in the input');
      assert.equal(find('.editable-field').classList.contains('has-changes'), true, 'The component has the "has-changes" class name');
      await click('.cancel-changes button');
      return settled();
    })
    .then(() => {
      assert.equal(find('.editable-field .editable-field__value').textContent.trim(), 'Julius Caesar', 'The value of the edit field is the original value');
      assert.equal(find('.editable-field').classList.contains('has-changes'), false, 'The component no longer has the "has-changes" class name');
      assert.equal(find('.editable-field').classList.contains('is-editing'), false, 'The component no longer has the "is-editing" class name');
    });
  });

  test('Clicking the confirm button returns the component to base (non-editing) mode with the new text', async function(assert) {
    assert.expect(7);

    this.set('value', 'Julius Caesar');
    this.set('fieldUpdate', (value, originalValue, revert) => {
      assert.equal(value, 'Augustus', 'The sent action arguments include the new value');
      assert.equal(originalValue, 'Julius Caesar', 'The sent action arguments include the original value');
      assert.equal(typeOf(revert), 'function', 'The sent action argument include the revert callback');
    });
    const editableFieldInputSelector = '.editable-field input';
    await render(hbs`{{respond-common/editable-field value=value onFieldChange=(action fieldUpdate)}}`);
    await click('.editable-field .editable-field__value');
    return settled().then(async () => {
      assert.equal(find(editableFieldInputSelector).value.trim(), 'Julius Caesar', 'The editable field component shows an input with the original value');
      await fillIn(editableFieldInputSelector, 'Augustus');
      await blur(editableFieldInputSelector);
      return settled();
    })
    .then(async () => {
      await click('.confirm-changes button');
      return settled();
    })
    .then(() => {
      assert.equal(find('.editable-field .editable-field__value').textContent.trim(), 'Augustus', 'The value of the edit field is the new value');
      assert.equal(find('.editable-field').classList.contains('has-changes'), false, 'The component no longer has the "has-changes" class name');
      assert.equal(find('.editable-field').classList.contains('is-editing'), false, 'The component no longer has the "is-editing" class name');
    });
  });

  test('Confirm button is disabled when the value is empty and attribute allowEmptyValue is false', async function(assert) {
    assert.expect(2);
    this.set('value', 'Julius Caesar');
    const editableFieldInputSelector = '.editable-field input';
    await render(hbs`{{respond-common/editable-field value=value allowEmptyValue=false }}`);
    await click('.editable-field .editable-field__value');
    return settled().then(async () => {
      assert.equal(find(editableFieldInputSelector).value.trim(), 'Julius Caesar', 'The editable field component shows an input with the original value');
      await fillIn(editableFieldInputSelector, '');
      await blur(editableFieldInputSelector);
      return settled();
    })
      .then(() => {
        assert.equal(findAll('.confirm-changes .rsa-form-button-wrapper.is-disabled').length, 1, 'The confirm button is disabled');
      });
  });

  test('A change in the value while the component is in edit mode resets the component to non-edit mode with the new value', async function(assert) {
    assert.expect(4);
    this.set('value', 'Julius Caesar');
    const editableFieldInputSelector = '.editable-field input';
    await render(hbs`{{respond-common/editable-field value=value}}`);
    await click('.editable-field .editable-field__value');
    return settled().then(async () => {
      assert.equal(find(editableFieldInputSelector).value.trim(), 'Julius Caesar', 'The editable field component shows an input with the original value');
      await fillIn(editableFieldInputSelector, 'Hadrian');
      await blur(editableFieldInputSelector);
      return settled();
    })
      .then(() => {
        this.set('value', 'Augustus');
        return settled();
      })
      .then(() => {
        assert.equal(find('.editable-field .editable-field__value').textContent.trim(), 'Augustus', 'The value of the edit field is the new value');
        assert.equal(find('.editable-field').classList.contains('has-changes'), false, 'The component no longer has the "has-changes" class name');
        assert.equal(find('.editable-field').classList.contains('is-editing'), false, 'The component no longer has the "is-editing" class name');
      });
  });
});
