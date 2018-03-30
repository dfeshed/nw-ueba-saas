import { test, module } from 'qunit';
import { setupTest } from 'ember-qunit';
import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'sa/actions/types';
import reducer from 'sa/reducers/global/preferences/index';

module('Unit | Reducers | Global | Preferences', function(hooks) {
  setupTest(hooks);

  test('should return the initial state', async function(assert) {
    const result = reducer(undefined, {});

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en-us', label: 'english' },
      locales: [{ id: 'en-us', label: 'english' }]
    });
  });

  test('UPDATE_PREFERENCES_THEME will only alter theme when the action.theme is legit', async function(assert) {
    let result = reducer(undefined, {});

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en-us', label: 'english' },
      locales: [{ id: 'en-us', label: 'english' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.UPDATE_PREFERENCES_THEME,
      theme: undefined
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en-us', label: 'english' },
      locales: [{ id: 'en-us', label: 'english' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.UPDATE_PREFERENCES_THEME,
      theme: 'undefined'
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en-us', label: 'english' },
      locales: [{ id: 'en-us', label: 'english' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.UPDATE_PREFERENCES_THEME,
      theme: null
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en-us', label: 'english' },
      locales: [{ id: 'en-us', label: 'english' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.UPDATE_PREFERENCES_THEME,
      theme: 'null'
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en-us', label: 'english' },
      locales: [{ id: 'en-us', label: 'english' }]
    });
  });

  test('ADD_PREFERENCES_LOCALES will normalize locales into key/value pairs and inject english as the first option', async function(assert) {
    let result = reducer(undefined, {});

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en-us', label: 'english' },
      locales: [{ id: 'en-us', label: 'english' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.ADD_PREFERENCES_LOCALES,
      locales: ['spanish_es', 'german_de-de']
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en-us', label: 'english' },
      locales: [
        { id: 'en-us', label: 'english' },
        { id: 'es', label: 'spanish', fileName: 'spanish_es' },
        { id: 'de-de', label: 'german', fileName: 'german_de-de' }
      ]
    });
  });

  test('ADD_PREFERENCES_LOCALES will produce a unique set of locales to ensure english is not duplicated', async function(assert) {
    let result = reducer(undefined, {});

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en-us', label: 'english' },
      locales: [{ id: 'en-us', label: 'english' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.ADD_PREFERENCES_LOCALES,
      locales: ['spanish_es', 'english_en-us', 'german_de-de']
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en-us', label: 'english' },
      locales: [
        { id: 'en-us', label: 'english' },
        { id: 'es', label: 'spanish', fileName: 'spanish_es' },
        { id: 'de-de', label: 'german', fileName: 'german_de-de' }
      ]
    });
  });

  test('ADD_PREFERENCES_LOCALES will return default locales when incoming data is incomplete or invalid', async function(assert) {
    let result = reducer(undefined, {});

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en-us', label: 'english' },
      locales: [{ id: 'en-us', label: 'english' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.ADD_PREFERENCES_LOCALES,
      locales: []
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en-us', label: 'english' },
      locales: [{ id: 'en-us', label: 'english' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.ADD_PREFERENCES_LOCALES,
      locales: undefined
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en-us', label: 'english' },
      locales: [{ id: 'en-us', label: 'english' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.ADD_PREFERENCES_LOCALES,
      locales: null
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en-us', label: 'english' },
      locales: [{ id: 'en-us', label: 'english' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.ADD_PREFERENCES_LOCALES
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en-us', label: 'english' },
      locales: [{ id: 'en-us', label: 'english' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.ADD_PREFERENCES_LOCALES,
      locales: ['spanishes-es', 'german_de-de']
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en-us', label: 'english' },
      locales: [{ id: 'en-us', label: 'english' }, { id: 'de-de', label: 'german', fileName: 'german_de-de' }]
    });
  });

  test('UPDATE_PREFERENCES_LOCALE will only alter locale when the action.locale is legit', async function(assert) {
    let result = Immutable.from({
      locale: { id: 'en-us', label: 'english' },
      locales: [{ id: 'en-us', label: 'english' }, { id: 'de-de', label: 'german' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.UPDATE_PREFERENCES_LOCALE,
      locale: { id: 'de-de', label: 'german' }
    });

    assert.deepEqual(result, {
      locale: { id: 'de-de', label: 'german' },
      locales: [{ id: 'en-us', label: 'english' }, { id: 'de-de', label: 'german' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.UPDATE_PREFERENCES_LOCALE,
      locale: 'undefined'
    });

    assert.deepEqual(result, {
      locale: { id: 'de-de', label: 'german' },
      locales: [{ id: 'en-us', label: 'english' }, { id: 'de-de', label: 'german' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.UPDATE_PREFERENCES_LOCALE,
      locale: null
    });

    assert.deepEqual(result, {
      locale: { id: 'de-de', label: 'german' },
      locales: [{ id: 'en-us', label: 'english' }, { id: 'de-de', label: 'german' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.UPDATE_PREFERENCES_LOCALE,
      locale: 'null'
    });

    assert.deepEqual(result, {
      locale: { id: 'de-de', label: 'german' },
      locales: [{ id: 'en-us', label: 'english' }, { id: 'de-de', label: 'german' }]
    });
  });

  test('REHYDRATE will only alter locale and theme when the JSON data structure is legit', async function(assert) {
    const previous = Immutable.from({
      theme: 'DARK',
      locale: { id: 'en-us', label: 'english' }
    });

    let result = reducer(previous, {
      type: ACTION_TYPES.REHYDRATE,
      payload: null
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en-us', label: 'english' }
    });

    result = reducer(previous, {
      type: ACTION_TYPES.REHYDRATE,
      payload: {
        global: null
      }
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'en-us', label: 'english' }
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
      locale: { id: 'en-us', label: 'english' }
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
      locale: { id: 'en-us', label: 'english' }
    });

    result = reducer(previous, {
      type: ACTION_TYPES.REHYDRATE,
      payload: {
        global: {
          preferences: {
            locale: { id: 'es', label: 'spanish', fileName: 'spanish_es' }
          }
        }
      }
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'es', label: 'spanish', fileName: 'spanish_es' }
    });

    result = reducer(previous, {
      type: ACTION_TYPES.REHYDRATE,
      payload: {
        global: {
          preferences: {
            theme: null,
            locale: { id: 'es', label: 'spanish', fileName: 'spanish_es' }
          }
        }
      }
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locale: { id: 'es', label: 'spanish', fileName: 'spanish_es' }
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
      locale: { id: 'es', label: 'spanish', fileName: 'spanish_es' }
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
      locale: { id: 'es', label: 'spanish', fileName: 'spanish_es' }
    });

    result = reducer(result, {
      type: ACTION_TYPES.REHYDRATE,
      payload: {
        global: {
          preferences: {
            locale: { id: 'de-de', label: 'german', fileName: 'german_de-de' }
          }
        }
      }
    });

    assert.deepEqual(result, {
      theme: 'LIGHT',
      locale: { id: 'de-de', label: 'german', fileName: 'german_de-de' }
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
      locale: { id: 'en-us', label: 'english' }
    });
  });
});
