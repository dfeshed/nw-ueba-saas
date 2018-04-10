import { test, module } from 'qunit';
import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'sa/actions/types';
import reducer from 'sa/reducers/global/preferences/index';

module('Unit | Reducers | Global | Preferences', function() {

  test('should return the initial state', async function(assert) {
    const result = reducer(undefined, {});

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en_US', key: 'en-us', label: 'english' },
      locales: [{ id: 'en_US', key: 'en-us', label: 'english' }]
    });
  });

  test('UPDATE_PREFERENCES_THEME will only alter theme when the action.theme is legit', async function(assert) {
    let result = reducer(undefined, {});

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en_US', key: 'en-us', label: 'english' },
      locales: [{ id: 'en_US', key: 'en-us', label: 'english' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.UPDATE_PREFERENCES_THEME,
      theme: undefined
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en_US', key: 'en-us', label: 'english' },
      locales: [{ id: 'en_US', key: 'en-us', label: 'english' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.UPDATE_PREFERENCES_THEME,
      theme: 'undefined'
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en_US', key: 'en-us', label: 'english' },
      locales: [{ id: 'en_US', key: 'en-us', label: 'english' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.UPDATE_PREFERENCES_THEME,
      theme: null
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en_US', key: 'en-us', label: 'english' },
      locales: [{ id: 'en_US', key: 'en-us', label: 'english' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.UPDATE_PREFERENCES_THEME,
      theme: 'null'
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en_US', key: 'en-us', label: 'english' },
      locales: [{ id: 'en_US', key: 'en-us', label: 'english' }]
    });
  });

  test('ADD_PREFERENCES_LOCALES will normalize locales into key/value pairs and inject english as the first option', async function(assert) {
    let result = reducer(undefined, {});

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en_US', key: 'en-us', label: 'english' },
      locales: [{ id: 'en_US', key: 'en-us', label: 'english' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.ADD_PREFERENCES_LOCALES,
      locales: [
        { name: 'spanish_es-mx.js', type: 'file', size: 288 },
        { name: 'german_de-de.js', type: 'file', size: 289 }
      ]
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en_US', key: 'en-us', label: 'english' },
      locales: [
        { id: 'en_US', key: 'en-us', label: 'english' },
        { id: 'es_MX', key: 'es-mx', label: 'spanish', fileName: 'spanish_es-mx.js' },
        { id: 'de_DE', key: 'de-de', label: 'german', fileName: 'german_de-de.js' }
      ]
    });
  });

  test('ADD_PREFERENCES_LOCALES will not blow up when locales fail to normalize', async function(assert) {
    let result = reducer(undefined, {});

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en_US', key: 'en-us', label: 'english' },
      locales: [{ id: 'en_US', key: 'en-us', label: 'english' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.ADD_PREFERENCES_LOCALES,
      locales: [
        { name: 'spanish_es.js', type: 'file', size: 288 },
        { name: 'german_de-de.js', type: 'file', size: 289 }
      ]
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en_US', key: 'en-us', label: 'english' },
      locales: [
        { id: 'en_US', key: 'en-us', label: 'english' },
        { id: 'de_DE', key: 'de-de', label: 'german', fileName: 'german_de-de.js' }
      ]
    });

    result = reducer(undefined, {});

    result = reducer(result, {
      type: ACTION_TYPES.ADD_PREFERENCES_LOCALES,
      locales: [
        { name: 'spanish_', type: 'file', size: 288 },
        { name: 'german_de-de.js', type: 'file', size: 289 }
      ]
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en_US', key: 'en-us', label: 'english' },
      locales: [
        { id: 'en_US', key: 'en-us', label: 'english' },
        { id: 'de_DE', key: 'de-de', label: 'german', fileName: 'german_de-de.js' }
      ]
    });

    result = reducer(undefined, {});

    result = reducer(result, {
      type: ACTION_TYPES.ADD_PREFERENCES_LOCALES,
      locales: [
        { name: '', type: 'file', size: 288 },
        { name: 'german_de-de.js', type: 'file', size: 289 }
      ]
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en_US', key: 'en-us', label: 'english' },
      locales: [
        { id: 'en_US', key: 'en-us', label: 'english' },
        { id: 'de_DE', key: 'de-de', label: 'german', fileName: 'german_de-de.js' }
      ]
    });

    result = reducer(undefined, {});

    result = reducer(result, {
      type: ACTION_TYPES.ADD_PREFERENCES_LOCALES,
      locales: [
        { name: null, type: 'file', size: 288 },
        { name: 'german_de-de.js', type: 'file', size: 289 }
      ]
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en_US', key: 'en-us', label: 'english' },
      locales: [
        { id: 'en_US', key: 'en-us', label: 'english' },
        { id: 'de_DE', key: 'de-de', label: 'german', fileName: 'german_de-de.js' }
      ]
    });
  });

  test('ADD_PREFERENCES_LOCALES will produce a unique set of locales to ensure english is not duplicated', async function(assert) {
    let result = reducer(undefined, {});

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en_US', key: 'en-us', label: 'english' },
      locales: [{ id: 'en_US', key: 'en-us', label: 'english' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.ADD_PREFERENCES_LOCALES,
      locales: [
        { name: 'spanish_es-mx.js', type: 'file', size: 288 },
        { name: 'english_en_US.js', type: 'file', size: 288 },
        { name: 'german_de-de.js', type: 'file', size: 289 }
      ]
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en_US', key: 'en-us', label: 'english' },
      locales: [
        { id: 'en_US', key: 'en-us', label: 'english' },
        { id: 'es_MX', key: 'es-mx', label: 'spanish', fileName: 'spanish_es-mx.js' },
        { id: 'de_DE', key: 'de-de', label: 'german', fileName: 'german_de-de.js' }
      ]
    });
  });

  test('ADD_PREFERENCES_LOCALES will return default locales when incoming data is incomplete or invalid', async function(assert) {
    let result = reducer(undefined, {});

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en_US', key: 'en-us', label: 'english' },
      locales: [{ id: 'en_US', key: 'en-us', label: 'english' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.ADD_PREFERENCES_LOCALES,
      locales: []
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en_US', key: 'en-us', label: 'english' },
      locales: [{ id: 'en_US', key: 'en-us', label: 'english' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.ADD_PREFERENCES_LOCALES,
      locales: undefined
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en_US', key: 'en-us', label: 'english' },
      locales: [{ id: 'en_US', key: 'en-us', label: 'english' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.ADD_PREFERENCES_LOCALES,
      locales: null
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en_US', key: 'en-us', label: 'english' },
      locales: [{ id: 'en_US', key: 'en-us', label: 'english' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.ADD_PREFERENCES_LOCALES
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en_US', key: 'en-us', label: 'english' },
      locales: [{ id: 'en_US', key: 'en-us', label: 'english' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.ADD_PREFERENCES_LOCALES,
      locales: [
        { name: 'spanish-es.js', type: 'file', size: 288 },
        { name: 'german_de-de.js', type: 'file', size: 289 }
      ]
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en_US', key: 'en-us', label: 'english' },
      locales: [
        { id: 'en_US', key: 'en-us', label: 'english' },
        { id: 'de_DE', key: 'de-de', label: 'german', fileName: 'german_de-de.js' }
      ]
    });
  });

  test('UPDATE_PREFERENCES_LOCALE will only alter locale when the action.locale is legit', async function(assert) {
    let result = Immutable.from({
      locale: { id: 'en_US', key: 'en-us', label: 'english' },
      locales: [{ id: 'en_US', key: 'en-us', label: 'english' }, { id: 'de_DE', label: 'german' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.UPDATE_PREFERENCES_LOCALE,
      locale: { id: 'de_DE', label: 'german' }
    });

    assert.deepEqual(result, {
      locale: { id: 'de_DE', label: 'german' },
      locales: [{ id: 'en_US', key: 'en-us', label: 'english' }, { id: 'de_DE', label: 'german' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.UPDATE_PREFERENCES_LOCALE,
      locale: 'undefined'
    });

    assert.deepEqual(result, {
      locale: { id: 'de_DE', label: 'german' },
      locales: [{ id: 'en_US', key: 'en-us', label: 'english' }, { id: 'de_DE', label: 'german' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.UPDATE_PREFERENCES_LOCALE,
      locale: null
    });

    assert.deepEqual(result, {
      locale: { id: 'de_DE', label: 'german' },
      locales: [{ id: 'en_US', key: 'en-us', label: 'english' }, { id: 'de_DE', label: 'german' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.UPDATE_PREFERENCES_LOCALE,
      locale: 'null'
    });

    assert.deepEqual(result, {
      locale: { id: 'de_DE', label: 'german' },
      locales: [{ id: 'en_US', key: 'en-us', label: 'english' }, { id: 'de_DE', label: 'german' }]
    });
  });

  test('REHYDRATE will only alter locale and theme when the JSON data structure is legit', async function(assert) {
    const previous = Immutable.from({
      theme: 'DARK',
      locale: { id: 'en_US', key: 'en-us', label: 'english' }
    });

    let result = reducer(previous, {
      type: ACTION_TYPES.REHYDRATE,
      payload: null
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en_US', key: 'en-us', label: 'english' }
    });

    result = reducer(previous, {
      type: ACTION_TYPES.REHYDRATE,
      payload: {
        global: null
      }
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en_US', key: 'en-us', label: 'english' }
    });

    result = reducer(previous, {
      type: ACTION_TYPES.REHYDRATE,
      payload: {
        global: {
          preferences: null
        }
      }
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en_US', key: 'en-us', label: 'english' }
    });

    result = reducer(previous, {
      type: ACTION_TYPES.REHYDRATE,
      payload: {
        global: {
          preferences: {
            theme: null,
            locale: null
          }
        }
      }
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en_US', key: 'en-us', label: 'english' }
    });

    result = reducer(previous, {
      type: ACTION_TYPES.REHYDRATE,
      payload: {
        global: {
          preferences: {
            locale: { id: 'es_MX', key: 'es-mx', label: 'spanish', fileName: 'spanish_es-mx.js' }
          }
        }
      }
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'es_MX', key: 'es-mx', label: 'spanish', fileName: 'spanish_es-mx.js' }
    });

    result = reducer(previous, {
      type: ACTION_TYPES.REHYDRATE,
      payload: {
        global: {
          preferences: {
            theme: null,
            locale: { id: 'es_MX', key: 'es-mx', label: 'spanish', fileName: 'spanish_es-mx.js' }
          }
        }
      }
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'es_MX', key: 'es-mx', label: 'spanish', fileName: 'spanish_es-mx.js' }
    });

    result = reducer(result, {
      type: ACTION_TYPES.REHYDRATE,
      payload: {
        global: {
          preferences: {
            theme: 'LIGHT'
          }
        }
      }
    });

    assert.deepEqual(result, {
      theme: 'LIGHT',
      locale: { id: 'es_MX', key: 'es-mx', label: 'spanish', fileName: 'spanish_es-mx.js' }
    });

    result = reducer(result, {
      type: ACTION_TYPES.REHYDRATE,
      payload: {
        global: {
          preferences: {
            theme: 'LIGHT',
            locale: null
          }
        }
      }
    });

    assert.deepEqual(result, {
      theme: 'LIGHT',
      locale: { id: 'es_MX', key: 'es-mx', label: 'spanish', fileName: 'spanish_es-mx.js' }
    });

    result = reducer(result, {
      type: ACTION_TYPES.REHYDRATE,
      payload: {
        global: {
          preferences: {
            locale: { id: 'de_DE', key: 'de-de', label: 'german', fileName: 'german_de-de.js' }
          }
        }
      }
    });

    assert.deepEqual(result, {
      theme: 'LIGHT',
      locale: { id: 'de_DE', key: 'de-de', label: 'german', fileName: 'german_de-de.js' }
    });

    result = reducer(previous, {
      type: ACTION_TYPES.REHYDRATE,
      payload: {
        global: {
          preferences: {
            theme: 'LIGHT'
          }
        }
      }
    });

    assert.deepEqual(result, {
      theme: 'LIGHT',
      locale: { id: 'en_US', key: 'en-us', label: 'english' }
    });
  });
});
