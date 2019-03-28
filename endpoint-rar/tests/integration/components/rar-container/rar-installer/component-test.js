import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, find, fillIn, triggerKeyEvent, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import Immutable from 'seamless-immutable';
import { patchReducer } from '../../../../helpers/vnext-patch';

import { revertPatch } from '../../../../helpers/patch-reducer';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';

let setState;

module('Integration | Component | rar-container', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    this.owner.inject('component', 'i18n', 'service:i18n');
  });
  hooks.afterEach(function() {
    revertPatch();
  });

  test('rar-installer component button renders', async function(assert) {

    new ReduxDataHelper(setState).loading(true).build();

    await render(hbs`{{rar-container/rar-installer}}`);

    assert.equal(findAll('.rar-container_rar-installer_downloadInstaller .rsa-loader').length, 1, 'loader has rendered.');
  });

  test('rar-installer component button renders', async function(assert) {

    new ReduxDataHelper(setState).loading(false).build();

    await render(hbs`{{rar-container/rar-installer}}`);

    assert.equal(find('.rar-container_rar-installer_downloadInstaller .backButton').textContent.trim(), 'Download installer', 'Download intaller text has rendered.');
  });

  test('password validation content', async function(assert) {

    await render(hbs`{{rar-container/rar-installer}}`);

    await click('.password-input-js input');
    await fillIn('.password-input-js input', 'te');
    await triggerKeyEvent('.password-input-js input', 'keyup', 65);
    assert.equal(find('.input-error').textContent.trim(), 'Can contain alphanumeric or special characters, and a minimum of 3 characters.');
  });

  test('password validation content on clicking download installer', async function(assert) {

    await render(hbs`{{rar-container/rar-installer}}`);

    await click('.password-input-js input');
    await fillIn('.password-input-js input', 'te 1');
    await triggerKeyEvent('.password-input-js input', 'keyup', 65);
    await click('.rar-container_rar-installer_downloadInstaller button');
    assert.equal(find('.input-error').textContent.trim(), 'Can contain alphanumeric or special characters, and a minimum of 3 characters.');
  });

  test('password validation empty', async function(assert) {

    await render(hbs`{{rar-container/rar-installer}}`);

    await click('.password-input-js input');
    await fillIn('.password-input-js input', ' ');
    await triggerKeyEvent('.password-input-js input', 'keyup', 65);
    assert.equal(find('.input-error').textContent.trim(), 'Please enter RAR password');
  });

});