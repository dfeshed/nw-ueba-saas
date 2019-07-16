import $ from 'jquery';
import { get } from '@ember/object';
import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { t } from 'ember-i18n/test-support';
import { waitUntil, settled } from '@ember/test-helpers';
import { localStorageClear } from 'sa/tests/helpers/wait-for';
import * as ACTION_TYPES from 'sa/actions/types';
import { patchFetch } from 'sa/tests/helpers/patch-fetch';
import { patchFlash } from 'sa/tests/helpers/patch-flash';
import { Promise } from 'rsvp';

const timeout = 10000;

module('Unit | Controller | application', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    this.owner.inject('controller', 'i18n', 'service:i18n');
    return localStorageClear();
  });

  hooks.afterEach(function() {
    return localStorageClear();
  });

  test('will NOT alter the body class when theme is undefined (string)', async function(assert) {
    $('body').addClass('our-application');

    let updatesRun = 0;
    const redux = this.owner.lookup('service:redux');
    const controller = this.owner.lookup('controller:application');
    const original = controller._updateBodyClass;
    controller._updateBodyClass = function() {
      updatesRun++;
      return original.apply(this, arguments);
    };

    redux.dispatch({ type: ACTION_TYPES.UPDATE_PREFERENCES_THEME, theme: 'undefined' });

    assert.ok($('body').hasClass('dark-theme'));
    assert.ok($('body').hasClass('our-application'));
    assert.notOk($('body').hasClass('undefined-theme'));

    // While IE11 is supported this _updateBodyClass call is important
    // because without it we never fetch the dark.css file explicitly
    assert.equal(updatesRun, 1);
  });

  test('will NOT alter the body class when theme is undefined (type)', async function(assert) {
    $('body').addClass('our-application');

    let updatesRun = 0;
    const redux = this.owner.lookup('service:redux');
    const controller = this.owner.lookup('controller:application');
    const original = controller._updateBodyClass;
    controller._updateBodyClass = function() {
      updatesRun++;
      return original.apply(this, arguments);
    };

    redux.dispatch({ type: ACTION_TYPES.UPDATE_PREFERENCES_THEME, theme: undefined });

    assert.ok($('body').hasClass('dark-theme'));
    assert.ok($('body').hasClass('our-application'));
    assert.notOk($('body').hasClass('undefined-theme'));

    // While IE11 is supported this _updateBodyClass call is important
    // because without it we never fetch the dark.css file explicitly
    assert.equal(updatesRun, 1);
  });

  test('will alter the body class when theme is truly different', async function(assert) {
    $('body').addClass('our-application');

    let updatesRun = 0;
    const redux = this.owner.lookup('service:redux');
    const controller = this.owner.lookup('controller:application');
    const original = controller._updateBodyClass;
    controller._updateBodyClass = function() {
      updatesRun++;
      return original.apply(this, arguments);
    };

    redux.dispatch({ type: ACTION_TYPES.UPDATE_PREFERENCES_THEME, theme: 'LIGHT' });
    assert.ok($('body').hasClass('light-theme'));
    assert.ok($('body').hasClass('our-application'));
    assert.notOk($('body').hasClass('dark-theme'));
    assert.equal(updatesRun, 1);

    redux.dispatch({ type: ACTION_TYPES.UPDATE_PREFERENCES_THEME, theme: 'DARK' });
    assert.ok($('body').hasClass('dark-theme'));
    assert.ok($('body').hasClass('our-application'));
    assert.notOk($('body').hasClass('light-theme'));
    assert.equal(updatesRun, 2);

    redux.dispatch({ type: ACTION_TYPES.UPDATE_PREFERENCES_THEME, theme: 'DARK' });
    assert.ok($('body').hasClass('dark-theme'));
    assert.ok($('body').hasClass('our-application'));
    assert.notOk($('body').hasClass('light-theme'));
    assert.equal(updatesRun, 2);
    controller._updateBodyClass = original;
  });

  test('will add locale script only when locale id is truly different', async function(assert) {
    let updatesRun = 0;
    const redux = this.owner.lookup('service:redux');
    const controller = this.owner.lookup('controller:application');
    const original = controller._addDynamicLocale;
    controller._addDynamicLocale = function() {
      updatesRun++;
    };

    redux.dispatch({ type: ACTION_TYPES.UPDATE_PREFERENCES_LOCALE, locale: { id: 'es_MX', key: 'es-mx', label: 'spanish', fileName: 'spanish_es-mx.js' } });
    assert.equal(updatesRun, 1);

    redux.dispatch({ type: ACTION_TYPES.UPDATE_PREFERENCES_LOCALE, locale: { id: 'en_US', key: 'en-us', label: 'english' } });
    assert.equal(updatesRun, 2);

    redux.dispatch({ type: ACTION_TYPES.UPDATE_PREFERENCES_LOCALE, locale: { id: 'en_US', key: 'en-us', label: 'english' } });
    assert.equal(updatesRun, 2);
    controller._addDynamicLocale = original;
  });

  test('will flash error when fetch script fails after locale change', async function(assert) {
    assert.expect(2);

    const redux = this.owner.lookup('service:redux');

    this.owner.lookup('controller:application');

    patchFetch(() => {
      return new Promise(function(resolve, reject) {
        reject('boom!');
      });
    });

    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedError = translation.t('userPreferences.locale.fetchError');
      assert.equal(flash.type, 'error');
      assert.equal(flash.message.string, expectedError);
    });

    redux.dispatch({ type: ACTION_TYPES.UPDATE_PREFERENCES_LOCALE, locale: { id: 'es_MX', key: 'es-mx', label: 'spanish', fileName: 'spanish_es-mx.js' } });

    return settled();
  });

  test('moment will be updated when locale changes', async function(assert) {
    assert.expect(2);

    const dynamicScripts = () => document.body.querySelectorAll('script#dynamicLocale');

    const moment = this.owner.lookup('service:moment');
    const redux = this.owner.lookup('service:redux');

    this.owner.lookup('controller:application');

    patchFetch(() => {
      return new Promise(function(resolve) {
        resolve({
          ok: true,
          text() {
          }
        });
      });
    });

    redux.dispatch({ type: ACTION_TYPES.UPDATE_PREFERENCES_LOCALE, locale: { id: 'es_MX', key: 'es-mx', label: 'spanish', fileName: 'spanish_es-mx.js' } });

    return settled().then(async() => {
      assert.equal(get(moment, 'locale'), 'es');

      redux.dispatch({ type: ACTION_TYPES.UPDATE_PREFERENCES_LOCALE, locale: { id: 'en_US', key: 'en-us', label: 'english' } });

      return settled().then(async() => {
        assert.equal(get(moment, 'locale'), 'en');
      });
    }).finally(async() => {
      document.body.removeChild(dynamicScripts()[0]);
    });
  });

  test('will fetch/append script and set i18n to correct character set when locale changes', async function(assert) {
    assert.expect(17);

    let fetchCount = 0;
    const dynamicScripts = () => document.body.querySelectorAll('script#dynamicLocale');
    const i18n = this.owner.lookup('service:i18n');
    const redux = this.owner.lookup('service:redux');

    this.owner.lookup('controller:application');

    assert.equal(get(i18n, 'locale'), 'en-us');
    assert.equal(dynamicScripts().length, 0);
    assert.equal(t('title').toString(), 'Missing translation: title');

    const fileFetch = (url) => {
      fetchCount++;
      assert.equal(url, '/locales/spanish_es-mx.js');
      return new Promise(function(resolve) {
        resolve({
          ok: true,
          text() {
            return new Promise(function(r) {
              r("define('sa/locales/es-mx/translations', ['exports'], function (exports) { exports.default = { title: 'spanish_x' }; })");
            });
          }
        });
      });
    };

    patchFetch((url) => fileFetch(url));
    redux.dispatch({ type: ACTION_TYPES.UPDATE_PREFERENCES_LOCALE, locale: { id: 'es_MX', key: 'es-mx', label: 'spanish', fileName: 'spanish_es-mx.js' } });

    return settled().then(async() => {
      assert.equal(fetchCount, 1);
      assert.equal(get(i18n, 'locale'), 'es-mx');
      assert.equal(dynamicScripts().length, 1);

      await waitUntil(() => t('title').toString() === 'spanish_x', { timeout });
      assert.equal(t('title').toString(), 'spanish_x');

      patchFetch((url) => fileFetch(url));
      redux.dispatch({ type: ACTION_TYPES.UPDATE_PREFERENCES_LOCALE, locale: { id: 'en_US', key: 'en-us', label: 'english' } });

      return settled().then(async() => {
        assert.equal(fetchCount, 1);
        assert.equal(get(i18n, 'locale'), 'en-us');
        assert.equal(dynamicScripts().length, 1);

        await waitUntil(() => t('title').toString() === 'Missing translation: title', { timeout });
        assert.equal(t('title').toString(), 'Missing translation: title');

        redux.dispatch({ type: ACTION_TYPES.UPDATE_PREFERENCES_LOCALE, locale: { id: 'es_MX', key: 'es-mx', label: 'spanish', fileName: 'spanish_es-mx.js' } });

        return settled().then(async() => {
          assert.equal(fetchCount, 2);
          assert.equal(get(i18n, 'locale'), 'es-mx');
          assert.equal(dynamicScripts().length, 1);

          await waitUntil(() => t('title').toString() === 'spanish_x', { timeout });
          assert.equal(t('title').toString(), 'spanish_x');
        });
      });
    }).finally(async() => {
      document.body.removeChild(dynamicScripts()[0]);
    });
  });

  test('will flash error when fetch script fails with 404 aka: missing response.ok', async function(assert) {
    assert.expect(2);

    const redux = this.owner.lookup('service:redux');

    this.owner.lookup('controller:application');

    patchFetch(() => {
      return new Promise(function(resolve) {
        resolve({
          ok: null,
          text() {
          }
        });
      });
    });

    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedError = translation.t('userPreferences.locale.fetchError');
      assert.equal(flash.type, 'error');
      assert.equal(flash.message.string, expectedError);
    });

    redux.dispatch({ type: ACTION_TYPES.UPDATE_PREFERENCES_LOCALE, locale: { id: 'es_MX', key: 'es-mx', label: 'spanish', fileName: 'spanish_es-mx.js' } });

    return settled();
  });
});
