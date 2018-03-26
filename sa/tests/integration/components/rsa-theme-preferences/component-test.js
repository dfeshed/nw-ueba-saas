import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { patchFlash } from 'sa/tests/helpers/patch-flash';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { setupRenderingTest } from 'ember-qunit';
import { render, click, find, findAll } from '@ember/test-helpers';
import { patchReducer } from 'sa/tests/helpers/vnext-patch';
import { patchSocket, throwSocket } from 'sa/tests/helpers/patch-socket';

const labelSelector = '.rsa-form-radio-label';

module('Integration | Component | rsa-theme-preferences', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    const initState = { global: { preferences: { theme: 'LIGHT' } } };
    patchReducer(this, initState);
    initialize(this.owner);
  });

  test('includes group label with correct text value', async function(assert) {
    await render(hbs `{{rsa-theme-preferences}}`);
    const groupLabelSelector = '.rsa-form-radio-group-label';
    const groupLabel = find(groupLabelSelector);
    assert.equal(groupLabel.textContent, 'Theme');
  });

  test('includes radio button with label for both light and dark theme', async function(assert) {
    await render(hbs `{{rsa-theme-preferences}}`);
    assert.equal(findAll(labelSelector).length, 2);
    assert.equal(find(`${labelSelector}:nth-of-type(1)`).textContent.trim(), 'Dark');
    assert.equal(find(`${labelSelector}:nth-of-type(2)`).textContent.trim(), 'Light');
  });

  test('the correct radio button is selected', async function(assert) {
    await render(hbs `{{rsa-theme-preferences}}`);
    const darkRadioSelector = `${labelSelector}:nth-of-type(1) input[type=radio]`;
    const lightRadioSelector = `${labelSelector}:nth-of-type(2) input[type=radio]`;
    assert.equal(find(darkRadioSelector).checked, false);
    assert.equal(find(lightRadioSelector).checked, true);
    const darkRadioLabel = `${labelSelector}:nth-of-type(1)`;
    const lightRadioLabel = `${labelSelector}:nth-of-type(2)`;
    assert.ok(!find(darkRadioLabel).classList.contains('checked'));
    assert.ok(find(lightRadioLabel).classList.contains('checked'));
  });

  test('onclick the radio button should alter the active theme', async function(assert) {
    assert.expect(7);

    await render(hbs `{{rsa-theme-preferences}}`);

    patchSocket((method, modelName, query) => {
      assert.equal(method, 'setPreference');
      assert.equal(modelName, 'preferences');
      assert.deepEqual(query, {
        data: {
          themeType: 'DARK'
        }
      });
    });

    const darkRadioSelector = `${labelSelector}:nth-of-type(1) input[type=radio]`;
    const lightRadioSelector = `${labelSelector}:nth-of-type(2) input[type=radio]`;
    await click(darkRadioSelector);

    assert.equal(find(darkRadioSelector).checked, true);
    assert.equal(find(lightRadioSelector).checked, false);

    const darkRadioLabel = `${labelSelector}:nth-of-type(1)`;
    const lightRadioLabel = `${labelSelector}:nth-of-type(2)`;
    assert.ok(find(darkRadioLabel).classList.contains('checked'));
    assert.ok(!find(lightRadioLabel).classList.contains('checked'));
  });

  test('onclick will display flash error when socket throws', async function(assert) {
    assert.expect(6);

    await render(hbs `{{rsa-theme-preferences}}`);

    throwSocket();

    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedError = translation.t('userPreferences.theme.error');
      assert.equal(flash.type, 'error');
      assert.equal(flash.message.string, expectedError);
    });

    const darkRadioSelector = `${labelSelector}:nth-of-type(1) input[type=radio]`;
    const lightRadioSelector = `${labelSelector}:nth-of-type(2) input[type=radio]`;
    await click(darkRadioSelector);

    assert.equal(find(darkRadioSelector).checked, true);
    assert.equal(find(lightRadioSelector).checked, false);

    const darkRadioLabel = `${labelSelector}:nth-of-type(1)`;
    const lightRadioLabel = `${labelSelector}:nth-of-type(2)`;
    assert.ok(find(darkRadioLabel).classList.contains('checked'));
    assert.ok(!find(lightRadioLabel).classList.contains('checked'));
  });
});
