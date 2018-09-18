import { test, module } from 'qunit';
import { setupTest } from 'ember-qunit';
import { bindActionCreators } from 'redux';
import { settled } from '@ember/test-helpers';
import { localStorageClear } from 'sa/tests/helpers/wait-for';
import { patchFlash } from 'sa/tests/helpers/patch-flash';
import { patchReducer } from 'sa/tests/helpers/vnext-patch';
import { patchSocket } from 'sa/tests/helpers/patch-socket';
import { updateLocale, updateLocaleByKey } from 'sa/actions/creators/preferences';
import { getLocale } from 'sa/reducers/global/preferences/selectors';

let setState;

module('Unit | Actions | Creators | Preferences', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    setState = (localeState) => {
      const state = { global: { preferences: localeState } };
      patchReducer(this, state);
    };
  });

  hooks.afterEach(function() {
    return localStorageClear();
  });

  test('updateLocaleByKey should set locale when userLocale found in locales', async function(assert) {
    setState({
      locale: { id: 'en_US', key: 'en-us', label: 'english', langCode: 'en', displayLabel: 'English' },
      locales: [
        { id: 'en_US', key: 'en-us', label: 'english', langCode: 'en', displayLabel: 'English' },
        { id: 'es_MX', key: 'es-mx', label: 'spanish', langCode: 'es', fileName: 'spanish_es-mx.js', displayLabel: 'Spanish' },
        { id: 'de_DE', key: 'de-de', label: 'german', langCode: 'de', fileName: 'german_de-de.js', displayLabel: 'German' }
      ]
    });

    const redux = this.owner.lookup('service:redux');

    let locale = getLocale(redux.getState());
    assert.equal(locale.id, 'en_US');

    bindActionCreators(updateLocaleByKey, redux.dispatch.bind(redux))('es_MX');

    locale = getLocale(redux.getState());
    assert.equal(locale.id, 'es_MX');
  });

  test('updateLocaleByKey will display flash error when userLocale not found in locales', async function(assert) {
    assert.expect(4);

    setState({
      locale: { id: 'en_US', key: 'en-us', label: 'english', langCode: 'en', displayLabel: 'English' },
      locales: [
        { id: 'en_US', key: 'en-us', label: 'english', langCode: 'en', displayLabel: 'English' }
      ]
    });

    const redux = this.owner.lookup('service:redux');

    let locale = getLocale(redux.getState());
    assert.equal(locale.id, 'en_US');

    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedError = translation.t('userPreferences.locale.fetchError');
      assert.equal(flash.type, 'error');
      assert.equal(flash.message.string, expectedError);
    });

    bindActionCreators(updateLocaleByKey, redux.dispatch.bind(redux))('es_MX');

    locale = getLocale(redux.getState());
    assert.equal(locale.id, 'en_US');
  });

  test('updateLocaleByKey will display flash error when userLocale found > 1x in locales', async function(assert) {
    assert.expect(4);

    setState({
      locale: { id: 'en_US', key: 'en-us', label: 'english', langCode: 'en', displayLabel: 'English' },
      locales: [
        { id: 'en_US', key: 'en-us', label: 'english', langCode: 'en', displayLabel: 'English' },
        { id: 'es_MX', key: 'es-mx', label: 'spanish', langCode: 'es', fileName: 'spanish_es-mx.js', displayLabel: 'Spanish' },
        { id: 'es_MX', key: 'es-mx', label: 'spanish2', langCode: 'es', fileName: 'spanish_es-mx.js', displayLabel: 'Spanish2' }
      ]
    });

    const redux = this.owner.lookup('service:redux');

    let locale = getLocale(redux.getState());
    assert.equal(locale.id, 'en_US');

    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedError = translation.t('userPreferences.locale.fetchError');
      assert.equal(flash.type, 'error');
      assert.equal(flash.message.string, expectedError);
    });

    bindActionCreators(updateLocaleByKey, redux.dispatch.bind(redux))('es_MX');

    locale = getLocale(redux.getState());
    assert.equal(locale.id, 'en_US');
  });

  test('updateLocaleByKey will not blow up when userLocale undefined', async function(assert) {
    assert.expect(2);

    setState({
      locale: { id: 'en_US', key: 'en-us', label: 'english', langCode: 'en', displayLabel: 'English' },
      locales: [
        { id: 'en_US', key: 'en-us', label: 'english', langCode: 'en', displayLabel: 'English' }
      ]
    });

    const redux = this.owner.lookup('service:redux');

    let locale = getLocale(redux.getState());
    assert.equal(locale.id, 'en_US');

    const done = patchFlash(() => {
      assert.ok(false, 'should not flash error message when userLocale undefined');
    });

    bindActionCreators(updateLocaleByKey, redux.dispatch.bind(redux))();

    return settled().then(async () => {
      locale = getLocale(redux.getState());
      assert.equal(locale.id, 'en_US');
    }).finally(async () => {
      done();
    });
  });

  test('updateLocaleByKey should set locale correctly when incoming preference string is 2 or 4 chars in length', async function(assert) {
    setState({
      locale: { id: 'en_US', key: 'en-us', label: 'english', langCode: 'en', displayLabel: 'English' },
      locales: [
        { id: 'en_US', key: 'en-us', label: 'english', langCode: 'en', displayLabel: 'English' },
        { id: 'es_MX', key: 'es-mx', label: 'spanish', langCode: 'es', fileName: 'spanish_es-mx.js', displayLabel: 'Spanish' },
        { id: 'de_DE', key: 'de-de', label: 'german', langCode: 'de', fileName: 'german_de-de.js', displayLabel: 'German' },
        { id: 'fr_FR', key: 'fr-fr', label: 'french', langCode: 'fr', fileName: 'french_fr-fr.js', displayLabel: 'French' },
        { id: 'ja_JP', key: 'ja-jp', label: 'japanese', langCode: 'ja', fileName: 'japanese_ja-jp.js', displayLabel: 'Japanese' }
      ]
    });

    const redux = this.owner.lookup('service:redux');

    let locale = getLocale(redux.getState());
    assert.equal(locale.id, 'en_US');

    bindActionCreators(updateLocaleByKey, redux.dispatch.bind(redux))('fr');

    locale = getLocale(redux.getState());
    assert.equal(locale.id, 'fr_FR');

    bindActionCreators(updateLocaleByKey, redux.dispatch.bind(redux))('ja');

    locale = getLocale(redux.getState());
    assert.equal(locale.id, 'ja_JP');

    bindActionCreators(updateLocaleByKey, redux.dispatch.bind(redux))('de');

    locale = getLocale(redux.getState());
    assert.equal(locale.id, 'de_DE');

    bindActionCreators(updateLocaleByKey, redux.dispatch.bind(redux))('fr_FR');

    locale = getLocale(redux.getState());
    assert.equal(locale.id, 'fr_FR');

    bindActionCreators(updateLocaleByKey, redux.dispatch.bind(redux))('ja_JP');

    locale = getLocale(redux.getState());
    assert.equal(locale.id, 'ja_JP');

    bindActionCreators(updateLocaleByKey, redux.dispatch.bind(redux))('de_DE');

    locale = getLocale(redux.getState());
    assert.equal(locale.id, 'de_DE');

    bindActionCreators(updateLocaleByKey, redux.dispatch.bind(redux))('en');

    locale = getLocale(redux.getState());
    assert.equal(locale.id, 'en_US');

    bindActionCreators(updateLocaleByKey, redux.dispatch.bind(redux))('en_US');

    locale = getLocale(redux.getState());
    assert.equal(locale.id, 'en_US');
  });

  test('updateLocale will send langCode when locale ja_JP', async function(assert) {
    assert.expect(3);

    setState({
      locale: { id: 'en_US', key: 'en-us', label: 'english', langCode: 'en', displayLabel: 'English' },
      locales: [
        { id: 'en_US', key: 'en-us', label: 'english', langCode: 'en', displayLabel: 'English' },
        { id: 'ja_JP', key: 'ja-jp', label: 'japanese', langCode: 'ja', fileName: 'japanese_ja-jp.js', displayLabel: 'Japanese' }
      ]
    });

    const redux = this.owner.lookup('service:redux');

    let locale = getLocale(redux.getState());
    assert.equal(locale.id, 'en_US');

    patchSocket((method, modelName, query) => {
      assert.deepEqual(query, {
        data: {
          userLocale: 'ja'
        }
      });
    });

    const updateLocaleAction = bindActionCreators(updateLocale, redux.dispatch.bind(redux));
    updateLocaleAction({
      id: 'ja_JP',
      key: 'ja-jp',
      label: 'japanese',
      langCode: 'ja',
      fileName: 'japanese_ja-jp.js',
      displayLabel: 'Japanese'
    });

    await settled();

    locale = getLocale(redux.getState());
    assert.equal(locale.id, 'ja_JP');
  });

  test('updateLocale will send id when locale es_MX', async function(assert) {
    assert.expect(3);

    setState({
      locale: { id: 'en_US', key: 'en-us', label: 'english', langCode: 'en', displayLabel: 'English' },
      locales: [
        { id: 'en_US', key: 'en-us', label: 'english', langCode: 'en', displayLabel: 'English' },
        { id: 'es_MX', key: 'es-mx', label: 'spanish', langCode: 'es', fileName: 'spanish_es-mx.js', displayLabel: 'Spanish' }
      ]
    });

    const redux = this.owner.lookup('service:redux');

    let locale = getLocale(redux.getState());
    assert.equal(locale.id, 'en_US');

    patchSocket((method, modelName, query) => {
      assert.deepEqual(query, {
        data: {
          userLocale: 'es_MX'
        }
      });
    });

    const updateLocaleAction = bindActionCreators(updateLocale, redux.dispatch.bind(redux));
    updateLocaleAction({
      id: 'es_MX',
      key: 'es-mx',
      label: 'spanish',
      langCode: 'es',
      fileName: 'spanish_es-mx.js',
      displayLabel: 'Spanish'
    });

    await settled();

    locale = getLocale(redux.getState());
    assert.equal(locale.id, 'es_MX');
  });

  test('updateLocale will send langCode when locale de_DE', async function(assert) {
    assert.expect(3);

    setState({
      locale: { id: 'en_US', key: 'en-us', label: 'english', langCode: 'en', displayLabel: 'English' },
      locales: [
        { id: 'en_US', key: 'en-us', label: 'english', langCode: 'en', displayLabel: 'English' },
        { id: 'de_DE', key: 'de-de', label: 'german', langCode: 'de', fileName: 'german_de-de.js', displayLabel: 'German' }
      ]
    });

    const redux = this.owner.lookup('service:redux');

    let locale = getLocale(redux.getState());
    assert.equal(locale.id, 'en_US');

    patchSocket((method, modelName, query) => {
      assert.deepEqual(query, {
        data: {
          userLocale: 'de'
        }
      });
    });

    const updateLocaleAction = bindActionCreators(updateLocale, redux.dispatch.bind(redux));
    updateLocaleAction({
      id: 'de_DE',
      key: 'de-de',
      label: 'german',
      langCode: 'de',
      fileName: 'german_de-de.js',
      displayLabel: 'German'
    });

    await settled();

    locale = getLocale(redux.getState());
    assert.equal(locale.id, 'de_DE');
  });

  test('updateLocale will send langCode when locale fr_FR', async function(assert) {
    assert.expect(3);

    setState({
      locale: { id: 'en_US', key: 'en-us', label: 'english', langCode: 'en', displayLabel: 'English' },
      locales: [
        { id: 'en_US', key: 'en-us', label: 'english', langCode: 'en', displayLabel: 'English' },
        { id: 'fr_FR', key: 'fr-fr', label: 'french', langCode: 'fr', fileName: 'french_fr-fr.js', displayLabel: 'French' }
      ]
    });

    const redux = this.owner.lookup('service:redux');

    let locale = getLocale(redux.getState());
    assert.equal(locale.id, 'en_US');

    patchSocket((method, modelName, query) => {
      assert.deepEqual(query, {
        data: {
          userLocale: 'fr'
        }
      });
    });

    const updateLocaleAction = bindActionCreators(updateLocale, redux.dispatch.bind(redux));
    updateLocaleAction({
      id: 'fr_FR',
      key: 'fr-fr',
      label: 'french',
      langCode: 'fr',
      fileName: 'french_fr-fr.js',
      displayLabel: 'French'
    });

    await settled();

    locale = getLocale(redux.getState());
    assert.equal(locale.id, 'fr_FR');
  });

  test('updateLocale will send id when locale en_US', async function(assert) {
    assert.expect(3);

    setState({
      locale: { id: 'de_DE', key: 'de-de', label: 'german', langCode: 'de', fileName: 'german_de-de.js', displayLabel: 'German' },
      locales: [
        { id: 'en_US', key: 'en-us', label: 'english', langCode: 'en', displayLabel: 'English' },
        { id: 'de_DE', key: 'de-de', label: 'german', langCode: 'de', fileName: 'german_de-de.js', displayLabel: 'German' }
      ]
    });

    const redux = this.owner.lookup('service:redux');

    let locale = getLocale(redux.getState());
    assert.equal(locale.id, 'de_DE');

    patchSocket((method, modelName, query) => {
      assert.deepEqual(query, {
        data: {
          userLocale: 'en_US'
        }
      });
    });

    const updateLocaleAction = bindActionCreators(updateLocale, redux.dispatch.bind(redux));
    updateLocaleAction({
      id: 'en_US',
      key: 'en-us',
      label: 'english',
      langCode: 'en',
      displayLabel: 'English'
    });

    await settled();

    locale = getLocale(redux.getState());
    assert.equal(locale.id, 'en_US');
  });
});
