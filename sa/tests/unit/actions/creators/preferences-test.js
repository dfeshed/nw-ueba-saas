import { test, module } from 'qunit';
import { setupTest } from 'ember-qunit';
import { bindActionCreators } from 'redux';
import { patchFlash } from 'sa/tests/helpers/patch-flash';
import { patchReducer } from 'sa/tests/helpers/vnext-patch';
import { updateLocaleByKey } from 'sa/actions/creators/preferences';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { getLocale } from 'sa/reducers/global/preferences/selectors';

let setState;

module('Unit | Actions | Creators | Preferences', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    setState = (localeState) => {
      const state = { global: { preferences: localeState } };
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  hooks.afterEach(function() {
    localStorage.removeItem('reduxPersist:global');
  });

  test('updateLocaleByKey should set locale when userLocale found in locales', async function(assert) {
    setState({
      locale: { id: 'en-us', label: 'english' },
      locales: [
        { id: 'en-us', label: 'english' },
        { id: 'es', label: 'spanish', fileName: 'spanish_es.js' },
        { id: 'de-DE', label: 'german', fileName: 'german_de-DE.js' }
      ]
    });

    const redux = this.owner.lookup('service:redux');

    let locale = getLocale(redux.getState());
    assert.equal(locale.id, 'en-us');

    bindActionCreators(updateLocaleByKey, redux.dispatch.bind(redux))('es');

    locale = getLocale(redux.getState());
    assert.equal(locale.id, 'es');
  });

  test('updateLocaleByKey will display flash error when userLocale not found in locales', async function(assert) {
    assert.expect(4);

    setState({
      locale: { id: 'en-us', label: 'english' },
      locales: [
        { id: 'en-us', label: 'english' }
      ]
    });

    const redux = this.owner.lookup('service:redux');

    let locale = getLocale(redux.getState());
    assert.equal(locale.id, 'en-us');

    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedError = translation.t('userPreferences.locale.fetchError');
      assert.equal(flash.type, 'error');
      assert.equal(flash.message.string, expectedError);
    });

    bindActionCreators(updateLocaleByKey, redux.dispatch.bind(redux))('es');

    locale = getLocale(redux.getState());
    assert.equal(locale.id, 'en-us');
  });

  test('updateLocaleByKey will display flash error when userLocale found > 1x in locales', async function(assert) {
    assert.expect(4);

    setState({
      locale: { id: 'en-us', label: 'english' },
      locales: [
        { id: 'en-us', label: 'english' },
        { id: 'es', label: 'wat1' },
        { id: 'es', label: 'wat2' }
      ]
    });

    const redux = this.owner.lookup('service:redux');

    let locale = getLocale(redux.getState());
    assert.equal(locale.id, 'en-us');

    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedError = translation.t('userPreferences.locale.fetchError');
      assert.equal(flash.type, 'error');
      assert.equal(flash.message.string, expectedError);
    });

    bindActionCreators(updateLocaleByKey, redux.dispatch.bind(redux))('es');

    locale = getLocale(redux.getState());
    assert.equal(locale.id, 'en-us');
  });

});
