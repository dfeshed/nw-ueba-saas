import { Promise } from 'rsvp';
import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { patchFetch } from 'sa/tests/helpers/patch-fetch';

module('Unit | Route | application', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    this.owner.inject('route', 'request', 'service:request');
  });

  test('should fetch locales and dispatch with response data', async function(assert) {
    assert.expect(2);

    const redux = this.owner.lookup('service:redux');
    const route = this.owner.lookup('route:application');
    route.set('router.currentRouteName', 'application');

    const promise = route.getLocales();

    let localeState = redux.getState().global.preferences.locales;
    assert.deepEqual(localeState, [{ id: 'en-us', label: 'english' }]);

    await promise;

    localeState = redux.getState().global.preferences.locales;
    assert.deepEqual(localeState, [{ id: 'en-us', label: 'english' }, { id: 'de-DE', label: 'german', fileName: 'german_de-DE.js' }, { id: 'es', label: 'spanish', fileName: 'spanish_es.js' }]);
  });

  test('when error thrown the default locales are still available', async function(assert) {
    assert.expect(2);

    const redux = this.owner.lookup('service:redux');
    const route = this.owner.lookup('route:application');
    route.set('router.currentRouteName', 'application');

    patchFetch(() => {
      return new Promise(function(resolve, reject) {
        reject('boom!');
      });
    });

    const promise = route.getLocales();

    let localeState = redux.getState().global.preferences.locales;
    assert.deepEqual(localeState, [{ id: 'en-us', label: 'english' }]);

    return promise.catch(() => {
      localeState = redux.getState().global.preferences.locales;
      assert.deepEqual(localeState, [{ id: 'en-us', label: 'english' }]);
    });
  });
});
