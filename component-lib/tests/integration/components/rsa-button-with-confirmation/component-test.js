import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { click, find, findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | RSA Button with Confirmation', function(hooks) {
  setupRenderingTest(hooks);

  const selectors = {
    confirmationContent: '#modalDestination .modal-content',
    confirmationNoteContent: '#modalDestination .modal-content .modal-content__note',
    cancelButton: '#modalDestination footer .modal-footer-buttons .is-standard button',
    confirmButton: '#modalDestination footer .modal-footer-buttons .is-primary button'
  };

  test('it renders to the DOM', async function(assert) {
    await render(hbs`{{#rsa-button-with-confirmation confirmationMessage='Click OK to confirm.'}}Push Me{{/rsa-button-with-confirmation}}`);
    assert.equal(find('button').textContent.trim(), 'Push Me', 'The block content appears as the button text');
  });

  test('it shows a confirmation modal on click', async function(assert) {
    await render(hbs`
    {{#rsa-button-with-confirmation confirmationMessage='Click OK to confirm.'}}Push Me{{/rsa-button-with-confirmation}}
    <div id="modalDestination"></div>`);
    assert.equal(findAll(selectors.confirmationContent).length, 0);
    await click('button');
    assert.equal(find(`${selectors.confirmationContent} p`).textContent.trim(), 'Click OK to confirm.');
    assert.equal(findAll(selectors.confirmationNoteContent).length, 0, 'There is no noteMessage displayed');
    assert.equal(find(selectors.cancelButton).textContent.trim(), 'No');
    assert.equal(find(selectors.confirmButton).textContent.trim(), 'Yes');
  });

  test('it shows the note message when provided', async function(assert) {
    await render(hbs`
    {{#rsa-button-with-confirmation confirmationMessage='Click OK to confirm.' noteMessage='Choose, but choose wisely'}}Push Me{{/rsa-button-with-confirmation}}
    <div id="modalDestination"></div>`);
    assert.equal(findAll(selectors.confirmationContent).length, 0);
    await click('button');
    assert.equal(find(`${selectors.confirmationContent} p`).textContent.trim(), 'Click OK to confirm.');
    assert.equal(findAll(selectors.confirmationNoteContent).length, 1, 'There is one noteMessage displayed');
    assert.equal(find(selectors.confirmationNoteContent).textContent.trim(), 'Choose, but choose wisely');
  });

  test('it closes the confirmation modal and does not call the action when the user clicks the cancel button', async function(assert) {
    assert.expect(3);
    this.set('onConfirm', () => {
      assert.ok(false);
    });
    await render(hbs`
    {{#rsa-button-with-confirmation confirmationMessage='Click OK to confirm.' onConfirm=(action onConfirm)}}Push Me{{/rsa-button-with-confirmation}}
    <div id="modalDestination"></div>`);
    assert.equal(findAll(selectors.confirmationContent).length, 0, 'The confirmation modal is not shown');
    await click('button');
    assert.equal(findAll(selectors.confirmationContent).length, 1, 'Clicking button shows confirmation modal');
    await click(selectors.cancelButton);
    assert.equal(findAll(selectors.confirmationContent).length, 0, 'Clicking No button closes modal');
  });

  test('it closes the confirmation modal and calls the onConfirm action when the user clicks the confirm button', async function(assert) {
    assert.expect(4);
    this.set('onConfirm', () => {
      assert.ok(true);
    });
    await render(hbs`
    {{#rsa-button-with-confirmation confirmationMessage='Click OK to confirm.' onConfirm=(action onConfirm)}}Push Me{{/rsa-button-with-confirmation}}
    <div id="modalDestination"></div>`);
    assert.equal(findAll(selectors.confirmationContent).length, 0, 'The confirmation modal is not shown');
    await click('button');
    assert.equal(findAll(selectors.confirmationContent).length, 1, 'Clicking button shows confirmation modal');
    await click(selectors.confirmButton);
    assert.equal(findAll(selectors.confirmationContent).length, 0, 'Clicking Yes button closes modal');
  });

  test('it shows a custom label for cancel and confirm buttons if the label attributes are provided', async function(assert) {
    await render(hbs`
    {{#rsa-button-with-confirmation
      cancelButtonLabel='Nah'
      confirmButtonLabel='Sounds good'
      confirmationMessage='Click OK to confirm.'}}Push Me{{/rsa-button-with-confirmation}}
    <div id="modalDestination"></div>`);
    assert.equal(findAll(selectors.confirmationContent).length, 0);
    await click('button');
    assert.equal(find(selectors.cancelButton).textContent.trim(), 'Nah');
    assert.equal(find(selectors.confirmButton).textContent.trim(), 'Sounds good');
  });
});