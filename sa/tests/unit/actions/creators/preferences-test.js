import { test, module } from 'qunit';
import { setupTest } from 'ember-qunit';
import { bindActionCreators } from 'redux';
import { localStorageClear } from 'sa/tests/helpers/wait-for';
import { patchFlash } from 'sa/tests/helpers/patch-flash';
import { patchReducer } from 'sa/tests/helpers/vnext-patch';
import { updateLocaleByKey } from 'sa/actions/creators/preferences';
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
      locale: { id: 'en_US', key: 'en-us', label: 'english' },
      locales: [
        { id: 'en_US', key: 'en-us', label: 'english' },
        { id: 'es_MX', key: 'es-mx', label: 'spanish', fileName: 'spanish_es-mx.js' },
        { id: 'de_DE', key: 'de-de', label: 'german', fileName: 'german_de-de.js' }
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
      locale: { id: 'en_US', key: 'en-us', label: 'english' },
      locales: [
        { id: 'en_US', key: 'en-us', label: 'english' }
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
      locale: { id: 'en_US', key: 'en-us', label: 'english' },
      locales: [
        { id: 'en_US', key: 'en-us', label: 'english' },
        { id: 'es_MX', key: 'es-mx', label: 'spanish' },
        { id: 'es_MX', key: 'es-mx', label: 'spanish2' }
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

});
