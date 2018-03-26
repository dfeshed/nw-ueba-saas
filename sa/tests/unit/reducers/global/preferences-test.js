import { test, module } from 'qunit';
import { setupTest } from 'ember-qunit';
import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'sa/actions/types';
import reducer from 'sa/reducers/global/preferences';

module('Unit | Reducers | Global | Preferences', function(hooks) {
  setupTest(hooks);

  test('should return the initial state', async function(assert) {
    const result = reducer(undefined, {});

    assert.deepEqual(result, {
      theme: 'DARK',
      locales: [{ 'english': 'en' }]
    });
  });

  test('UPDATE_PREFERENCES_THEME will only alter theme when the action.theme is legit', async function(assert) {
    let result = reducer(undefined, {});

    assert.deepEqual(result, {
      theme: 'DARK',
      locales: [{ 'english': 'en' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.UPDATE_PREFERENCES_THEME,
      theme: undefined
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locales: [{ 'english': 'en' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.UPDATE_PREFERENCES_THEME,
      theme: 'undefined'
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locales: [{ 'english': 'en' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.UPDATE_PREFERENCES_THEME,
      theme: null
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locales: [{ 'english': 'en' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.UPDATE_PREFERENCES_THEME,
      theme: 'null'
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locales: [{ 'english': 'en' }]
    });
  });

  test('REHYDRATE will only alter theme when the JSON data structure is legit', async function(assert) {
    const previous = Immutable.from({
      theme: 'DARK'
    });

    let result = reducer(previous, {
      type: ACTION_TYPES.REHYDRATE,
      payload: null
    });

    assert.deepEqual(result, {
      theme: 'DARK'
    });

    result = reducer(previous, {
      type: ACTION_TYPES.REHYDRATE,
      payload: {
        global: null
      }
    });

    assert.deepEqual(result, {
      theme: 'DARK'
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
      theme: 'DARK'
    });

    result = reducer(previous, {
      type: ACTION_TYPES.REHYDRATE,
      payload: {
        global: {
          preferences: {
            theme: null
          }
        }
      }
    });

    assert.deepEqual(result, {
      theme: 'DARK'
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
      theme: 'LIGHT'
    });
  });

  test('ADD_PREFERENCES_LOCALES will normalize locales into key/value pairs and inject English as the first option', async function(assert) {
    let result = reducer(undefined, {});

    assert.deepEqual(result, {
      theme: 'DARK',
      locales: [{ 'english': 'en' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.ADD_PREFERENCES_LOCALES,
      locales: ['spanish_es', 'german_de']
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locales: [
        { 'english': 'en' },
        { 'spanish': 'es' },
        { 'german': 'de' }
      ]
    });
  });

  test('ADD_PREFERENCES_LOCALES will produce a unique set of locales to ensure english is not duplicated', async function(assert) {
    let result = reducer(undefined, {});

    assert.deepEqual(result, {
      theme: 'DARK',
      locales: [{ 'english': 'en' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.ADD_PREFERENCES_LOCALES,
      locales: ['spanish_es', 'english_en', 'german_de']
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locales: [
        { 'english': 'en' },
        { 'spanish': 'es' },
        { 'german': 'de' }
      ]
    });
  });

  test('ADD_PREFERENCES_LOCALES will return default locales when incoming data is incomplete or invalid', async function(assert) {
    let result = reducer(undefined, {});

    assert.deepEqual(result, {
      theme: 'DARK',
      locales: [{ 'english': 'en' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.ADD_PREFERENCES_LOCALES,
      locales: []
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locales: [{ 'english': 'en' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.ADD_PREFERENCES_LOCALES,
      locales: undefined
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locales: [{ 'english': 'en' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.ADD_PREFERENCES_LOCALES,
      locales: null
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locales: [{ 'english': 'en' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.ADD_PREFERENCES_LOCALES
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locales: [{ 'english': 'en' }]
    });

    result = reducer(result, {
      type: ACTION_TYPES.ADD_PREFERENCES_LOCALES,
      locales: ['spanishes', 'german_de']
    });

    assert.deepEqual(result, {
      theme: 'DARK',
      locales: [{ 'english': 'en' }, { 'german': 'de' }]
    });
  });
});
