import { module, test } from 'qunit';
import Component from '@ember/component';
import hbs from 'htmlbars-inline-precompile';
import { patchFlash } from 'sa/tests/helpers/patch-flash';
import { localStorageClear } from 'sa/tests/helpers/wait-for';
import { setupRenderingTest } from 'ember-qunit';
import { render, click, find, findAll, settled } from '@ember/test-helpers';
import { patchReducer } from 'sa/tests/helpers/vnext-patch';
import { clickTrigger, selectChoose } from 'ember-power-select/test-support/helpers';
import { patchSocket, throwSocket } from 'sa/tests/helpers/patch-socket';

const japaneseLocale = 'Japanese';
const englishLocale = 'English';
const powerSelect = '.power-select';
const labelSelector = '.rsa-form-label';
const textSelector = '.rsa-form-label .label-text';
const optionsSelector = '.ember-power-select-option';
const powerSelector = '.power-select .ember-power-select-trigger';
const trim = (text) => text.replace(/\s\s+/g, ' ').trim();

module('Integration | Component | rsa-locale-preferences', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    const initState = { global: { preferences: { locale: { id: 'en_US', key: 'en-us', label: 'english' }, locales: [{ id: 'en_US', key: 'en-us', label: 'english' }, { id: 'ja_JP', key: 'ja-jp', label: 'japanese' }] } } };
    patchReducer(this, initState);
  });

  hooks.afterEach(function() {
    return localStorageClear();
  });

  test('includes 1 label with correct classes', async function(assert) {
    await render(hbs `{{rsa-locale-preferences}}`);
    assert.equal(findAll(labelSelector).length, 1);
    const label = find(labelSelector);
    assert.ok(label.classList.contains('rsa-form-label'));
    assert.ok(label.classList.contains('power-select'));
  });

  test('label includes 1 label-text element with correct value', async function(assert) {
    await render(hbs `{{rsa-locale-preferences}}`);
    assert.equal(findAll(textSelector).length, 1);
    const text = find(textSelector);
    assert.equal(trim(text.textContent), 'Language');
  });

  test('label includes 1 select element with hydrated locale defaults', async function(assert) {
    await render(hbs `{{rsa-locale-preferences}}`);
    assert.equal(findAll(powerSelector).length, 1);
    assert.equal(trim(find(powerSelector).textContent), englishLocale);
    clickTrigger(powerSelect);
    assert.equal(findAll(optionsSelector).length, 2);
    assert.equal(trim(find(`${optionsSelector}:nth-of-type(1)`).textContent), englishLocale);
    assert.equal(trim(find(`${optionsSelector}:nth-of-type(2)`).textContent), japaneseLocale);
  });

  test('onchange of the select should alter the active locale', async function(assert) {
    assert.expect(4);

    await render(hbs `{{rsa-locale-preferences}}`);

    patchSocket((method, modelName, query) => {
      assert.equal(method, 'setPreference');
      assert.equal(modelName, 'preferences');
      assert.deepEqual(query, {
        data: {
          userLocale: 'ja_JP'
        }
      });
    });

    clickTrigger(powerSelect);
    selectChoose(powerSelector, japaneseLocale);

    return settled().then(async () => {
      const powerSelect = find(powerSelector);
      assert.equal(trim(powerSelect.textContent), japaneseLocale);
    });
  });

  test('onchange will display flash error when socket throws', async function(assert) {
    assert.expect(3);

    await render(hbs `{{rsa-locale-preferences}}`);

    throwSocket();

    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedError = translation.t('userPreferences.locale.error');
      assert.equal(flash.type, 'error');
      assert.equal(flash.message.string, expectedError);
    });

    clickTrigger(powerSelect);
    selectChoose(powerSelector, japaneseLocale);

    return settled().then(async () => {
      const powerSelect = find(powerSelector);
      assert.equal(trim(powerSelect.textContent), japaneseLocale);
    });
  });

  test('datetime will reflect proper locale after change occurs', async function(assert) {
    assert.expect(2);

    class FakeClazz extends Component {
      get layout() {
        return hbs`<div onclick={{action go}} class="time">{{moment-format (moment "1991-01-01 6:00 AM" "HH:mm A") "A"}}</div>`;
      }
    }

    this.owner.register('component:test-clazz', FakeClazz);

    const moment = this.owner.lookup('service:moment');

    this.set('go', () => {
      moment.changeLocale('ja-jp');
    });

    await render(hbs`{{test-clazz go=(action go)}}`);

    assert.equal(find('.time').textContent, 'PM');

    await click('.time');

    return settled().then(async () => {
      assert.equal(find('.time').textContent, '午後');
    });
  });
});
