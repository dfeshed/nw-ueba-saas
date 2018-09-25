import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, click, triggerEvent } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import sinon from 'sinon';

module('Integration | Component | endpoint/file-actionbar', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('it renders', async function(assert) {
    this.set('itemList', []);
    await render(hbs`{{endpoint/file-actionbar itemList=itemList}}`);
    assert.equal(findAll('.file-actionbar').length, 1, 'file-actionbar component has rendered.');
    assert.equal(findAll('.file-actionbar .rsa-form-button').length, 3, 'five buttons have been rendered.');
  });

  test('presence of priority buttons', async function(assert) {
    this.set('itemList', []);
    await render(hbs`{{endpoint/file-actionbar itemList=itemList}}`);
    assert.equal(findAll('.file-actionbar .file-status-button')[0].textContent.trim(), 'Edit File Status', 'Edit file status button is present.');
    assert.equal(findAll('.file-actionbar .pivot-to-investigate-button')[0].textContent.trim(), 'Pivot to Investigate', 'Pivot to investigate button is present.');
  });

  test('when component is used to show only icons', async function(assert) {
    this.set('itemList', []);
    await render(hbs`{{endpoint/file-actionbar itemList=itemList showOnlyIcons=true}}`);
    assert.equal(findAll('.file-actionbar .file-status-button')[0].textContent.trim(), '', 'Edit file status button is present without any text content.');
    assert.equal(findAll('.file-actionbar .file-status-button')[0].classList.contains('is-icon-only'), true, 'Edit file statusButton is icon only.');
    assert.equal(findAll('.file-actionbar .pivot-to-investigate-button')[0].classList.contains('is-icon-only'), true, 'Pivot to investigate Button is icon only.');
  });

  test('when noFiles selected', async function(assert) {
    this.set('itemList', []);
    await render(hbs`{{endpoint/file-actionbar itemList=itemList selectedFileCount=0}}`);
    assert.equal(findAll('.file-actionbar .file-status-button')[0].classList.contains('is-disabled'), true, 'Edit file status Button is disabled when no files are selected');
    assert.equal(findAll('.file-actionbar .pivot-to-investigate-button')[0].classList.contains('is-disabled'), true, 'Pivot-to-investigate Button is disabled when no files are selected');
  });

  test('Buttons enabling/disabling for multiple files selection', async function(assert) {
    this.set('itemList', [{ machineOSType: 'windows' }]);
    await render(hbs`{{endpoint/file-actionbar itemList=itemList selectedFileCount=2}}`);
    assert.equal(findAll('.file-actionbar .file-status-button')[0].classList.contains('is-disabled'), false, 'Edit file status Button is enabled when multiple files are selected.');
    assert.equal(findAll('.file-actionbar .pivot-to-investigate-button')[0].classList.contains('is-disabled'), true, 'Pivot-to-investigate Button is disabled when multiple files are selected.');
  });

  test('More action external lookup for google', async function(assert) {
    this.set('itemList', [
      { machineOSType: 'windows', fileName: 'abc', checksumSha256: 'abc1', checksumSha1: 'abc2', checksumMd5: 'abcmd5' },
      { machineOSType: 'windows', fileName: 'xyz', checksumSha256: 'xyz1', checksumSha1: 'xyz2', checksumMd5: 'xyzmd5' }
    ]);
    const actionSpy = sinon.spy(window, 'open');
    await render(hbs`{{endpoint/file-actionbar itemList=itemList showIcons=false selectedFileCount=2}}`);
    assert.equal(findAll('.more-action-button')[0].classList.contains('is-disabled'), false, 'More action button should enable.');
    await click('.more-action-button');
    assert.equal(findAll('.rsa-dropdown-action-list li').length, 3, 'All the list options should render.');
    await triggerEvent('.panel2', 'mouseover');
    assert.equal(findAll('.rsa-dropdown-action-list li').length, 7, 'All the list options should render.');
    await click(findAll('.rsa-dropdown-action-list li')[3]);
    assert.equal(findAll('.rsa-dropdown-action-list li').length, 3, 'Sub menu options should hide.');
    assert.equal(actionSpy.callCount, 2);
    actionSpy.reset();
    actionSpy.restore();
  });
});
