import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { patchSocket, throwSocket } from 'sa/tests/helpers/patch-socket';

module('Unit | Route | protected', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    this.owner.inject('route', 'request', 'service:request');
  });

  test('should fetch locales and dispatch with response data', async function(assert) {
    assert.expect(5);

    const redux = this.owner.lookup('service:redux');
    const route = this.owner.lookup('route:protected');
    route.set('router.currentRouteName', 'protected');

    patchSocket((method, modelName, query) => {
      assert.equal(method, 'getLocales');
      assert.equal(modelName, 'locales');
      assert.deepEqual(query, {});
    });

    const promise = route.getLocales();

    let localeState = redux.getState().global.preferences.locales;
    assert.deepEqual(localeState, [{ id: 'en-us', label: 'english' }]);

    await promise;

    localeState = redux.getState().global.preferences.locales;
    assert.deepEqual(localeState, [{ id: 'en-us', label: 'english' }, { id: 'es-es', label: 'spanish' }, { id: 'de-de', label: 'german' }]);
  });

  test('when error thrown the default locales are still available', async function(assert) {
    assert.expect(2);

    const redux = this.owner.lookup('service:redux');
    const route = this.owner.lookup('route:protected');
    route.set('router.currentRouteName', 'protected');

    const done = throwSocket({ methodToThrow: 'getLocales', modelNameToThrow: 'locales' });

    const promise = route.getLocales();

    let localeState = redux.getState().global.preferences.locales;
    assert.deepEqual(localeState, [{ id: 'en-us', label: 'english' }]);

    return promise.catch(() => {
      localeState = redux.getState().global.preferences.locales;
      assert.deepEqual(localeState, [{ id: 'en-us', label: 'english' }]);
      done();
    });
  });
});
