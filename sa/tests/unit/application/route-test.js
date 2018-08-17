import { Promise } from 'rsvp';
import Service from '@ember/service';
import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { localStorageClear } from 'sa/tests/helpers/wait-for';
import { patchFetch } from 'sa/tests/helpers/patch-fetch';
import { getLocales } from 'sa/reducers/global/preferences/selectors';

const english = { id: 'en_US', key: 'en-us', label: 'english', langCode: 'en', displayLabel: 'English' };
const spanish = { id: 'es_MX', key: 'es-mx', label: 'spanish', langCode: 'es', fileName: 'spanish_es-mx.js', displayLabel: 'Spanish' };
const german = { id: 'de_DE', key: 'de-de', label: 'german', langCode: 'de', fileName: 'german_de-de.js', displayLabel: 'German' };
const japanese = { id: 'ja_JP', key: 'ja-jp', label: 'japanese', langCode: 'ja', fileName: 'japanese_ja-jp.js', displayLabel: 'Japanese' };

module('Unit | Route | application', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    this.owner.inject('route', 'request', 'service:request');
  });

  hooks.afterEach(function() {
    return localStorageClear();
  });

  test('should fetch locales and dispatch with response data', async function(assert) {
    assert.expect(2);

    const redux = this.owner.lookup('service:redux');

    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'application'
    }));
    const route = this.owner.lookup('route:application');

    const promise = route.getLocales();

    let locales = getLocales(redux.getState());
    assert.deepEqual(locales, [english]);

    await promise;

    locales = getLocales(redux.getState());
    assert.deepEqual(locales, [english, german, japanese, spanish]);
  });

  test('when error thrown the default locales are still available', async function(assert) {
    assert.expect(2);

    const redux = this.owner.lookup('service:redux');

    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'application'
    }));
    const route = this.owner.lookup('route:application');

    patchFetch(() => {
      return new Promise(function(resolve, reject) {
        reject('boom!');
      });
    });

    let locales = getLocales(redux.getState());
    assert.deepEqual(locales, [english]);

    const promise = route.getLocales();

    return promise.then(() => {
      locales = getLocales(redux.getState());
      assert.deepEqual(locales, [english]);
    }).catch(() => {
      assert.ok(false, 'should not have thrown exception');
    });
  });

  test('when the response for locales is html the app will not blow up', async function(assert) {
    assert.expect(2);

    const redux = this.owner.lookup('service:redux');

    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'application'
    }));
    const route = this.owner.lookup('route:application');

    patchFetch(() => {
      return new Promise(function(resolve) {
        resolve({
          ok: true,
          text() {
            return new Promise(function(r) {
              r('<html />');
            });
          }
        });
      });
    });

    let locales = getLocales(redux.getState());
    assert.deepEqual(locales, [english]);

    const promise = route.getLocales();

    return promise.then(() => {
      locales = getLocales(redux.getState());
      assert.deepEqual(locales, [english]);
    }).catch(() => {
      assert.ok(false, 'should not have thrown exception');
    });
  });
});
