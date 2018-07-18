import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, click, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | endpoint/edit-file-status/button', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function(assert) {
    await render(hbs`{{endpoint/edit-file-status/button}}`);
    assert.equal(this.element.textContent.trim(), 'Edit File Status', 'Edit file status button renders.');
  });


  test('Edit file status button is enabled', async function(assert) {
    await render(hbs`{{endpoint/edit-file-status/button isDisabled=false}}`);
    assert.equal(findAll('.file-status-button')[0].classList.contains('is-disabled'), false, 'Edit file status Button is enabled.');
  });

  test('Edit file status button is disabled', async function(assert) {
    await render(hbs`{{endpoint/edit-file-status/button isDisabled=true}}`);
    assert.equal(findAll('.file-status-button')[0].classList.contains('is-disabled'), true, 'Edit file status Button is disabled.');
  });

  test('Edit file status button as Icon only', async function(assert) {
    await render(hbs`{{endpoint/edit-file-status/button showOnlyIcons=true}}`);
    assert.equal(findAll('.file-status-button')[0].textContent.trim(), '', 'Edit file status Button appears as icon only, has no text.');
    assert.equal(findAll('.file-status-button .rsa-form-button .rsa-icon').length, 1, 'Edit file status Button has rsa-icon class.');
  });

  test('Edit file status button with title', async function(assert) {
    await render(hbs`{{endpoint/edit-file-status/button showOnlyIcons=false}}`);
    assert.equal(findAll('.file-status-button')[0].textContent.trim(), 'Edit File Status', 'Edit file status button has title.');
    assert.equal(findAll('.file-status-button .rsa-form-button .rsa-icon').length, 1, 'Edit file status Button has rsa-icon class.');
  });

  test('on click renders the file edit status modal', async function(assert) {
    this.set('defaultAction', function() {
      assert.ok('External function called on click of button');
    });
    await render(hbs`{{endpoint/edit-file-status/button defaultAction=(action defaultAction)}}`);
    assert.equal(findAll('.file-status-button').length, 1, 'Edit file status button is present.');
    await click('.file-status-button .rsa-form-button');
  });

});
