import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | endpoint/edit-file-status', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('Renders the edit file-status button', async function(assert) {
    await render(hbs`{{endpoint/edit-file-status}}`);
    assert.equal(findAll('.edit-file-status').length, 1, 'Edit Files status button has rendered.');
    assert.equal(findAll('.file-status-button')[0].textContent.trim(), 'Edit File Status', 'edit-file-status button text verified.');
  });

  test('it should disable the the button', async function(assert) {
    await render(hbs`{{endpoint/edit-file-status isDisabled=true}}`);
    assert.equal(findAll('.file-status-button')[0].classList.contains('is-disabled'), true, 'Edit files status Button is disabled');
  });


  test('on click renders the file edit status modal', async function(assert) {
    await render(hbs`{{endpoint/edit-file-status}}`);
    await click('.file-status-button button');
    assert.equal(document.querySelector('#modalDestination .file-status-modal').children.length, 4, 'Expected to render service modal');
  });

  test('on click renders the file edit status modal', async function(assert) {
    assert.expect(1);
    this.set('onSaveFileStatus', function() {
      assert.equal(arguments.length, 2, 'onSaveFileStatus called with two arguments');
    });
    await render(hbs`{{endpoint/edit-file-status onSaveFileStatus=onSaveFileStatus}}`);
    await click('.file-status-button button');
    await click(document.querySelector('.save-file-status button'));
  });

  test('file status values', async function(assert) {
    this.set('showFileStatusModal', true);
    await render(hbs`{{endpoint/edit-file-status showFileStatusModal=showFileStatusModal}}`);
    assert.equal(findAll('.file-status-radio').length, 5, 'Four file status values have been rendered');
    assert.equal(findAll('.file-status-radio')[0].textContent.trim(), 'Neutral', 'first file status');
  });

  test('toggle blacklist additional options', async function(assert) {
    this.set('showFileStatusModal', true);
    await render(hbs`{{endpoint/edit-file-status showFileStatusModal=showFileStatusModal}}`);
    assert.equal(findAll('.file-status-radio')[4].textContent.trim(), 'Blacklist', 'blacklist file status');
    assert.equal(findAll('.black-list-options').length, 0, 'blacklist option are not rendered');
    await click('.file-status-radio:last-child .status-type');
    assert.equal(findAll('.black-list-options').length, 1, 'blacklist option have been rendered');
  });

});
