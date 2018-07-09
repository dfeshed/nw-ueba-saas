import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | endpoint/file-actionbar', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('it renders', async function(assert) {
    await render(hbs`{{endpoint/file-actionbar}}`);
    assert.equal(findAll('.file-actionbar').length, 1, 'file-actionbar component has rendered.');
    assert.equal(findAll('.file-actionbar .rsa-form-button').length, 5, 'five buttons have been rendered.');
  });

  test('presence of priority buttons', async function(assert) {
    await render(hbs`{{endpoint/file-actionbar}}`);
    assert.equal(findAll('.file-actionbar .file-status-button')[0].textContent.trim(), 'Edit File Status', 'Edit file status button is present.');
    assert.equal(findAll('.file-actionbar .pivot-to-investigate-button')[0].textContent.trim(), 'Pivot to Investigate', 'Pivot to investigate button is present.');
  });

  test('presence of buttons with icons only', async function(assert) {
    await render(hbs`{{endpoint/file-actionbar}}`);
    assert.equal(findAll('.file-actionbar .watch-button')[0].classList.contains('is-icon-only'), true, 'Button is icon only.');
    assert.equal(findAll('.file-actionbar .watch-button')[0].textContent.trim(), '', 'Icon only buttons do not have any text to display.');
  });

  test('when component is used to show only icons', async function(assert) {
    await render(hbs`{{endpoint/file-actionbar showOnlyIcons=true}}`);
    assert.equal(findAll('.file-actionbar .file-status-button')[0].textContent.trim(), '', 'Edit file status button is present without any text content.');
    assert.equal(findAll('.file-actionbar .file-status-button')[0].classList.contains('is-icon-only'), true, 'Edit file statusButton is icon only.');
    assert.equal(findAll('.file-actionbar .pivot-to-investigate-button')[0].classList.contains('is-icon-only'), true, 'Pivot to investigate Button is icon only.');
  });

  test('when noFiles selected', async function(assert) {
    await render(hbs`{{endpoint/file-actionbar selectedFileCount=0}}`);
    assert.equal(findAll('.file-actionbar .file-status-button')[0].classList.contains('is-disabled'), true, 'Edit file status Button is disabled when no files are selected');
    assert.equal(findAll('.file-actionbar .pivot-to-investigate-button')[0].classList.contains('is-disabled'), true, 'Pivot-to-investigate Button is disabled when no files are selected');
  });

  test('Buttons enabling/disabling for multiple files selection', async function(assert) {
    await render(hbs`{{endpoint/file-actionbar selectedFileCount=2}}`);
    assert.equal(findAll('.file-actionbar .file-status-button')[0].classList.contains('is-disabled'), false, 'Edit file status Button is enabled when multiple files are selected.');
    assert.equal(findAll('.file-actionbar .pivot-to-investigate-button')[0].classList.contains('is-disabled'), true, 'Pivot-to-investigate Button is disabled when multiple files are selected.');
  });
});
