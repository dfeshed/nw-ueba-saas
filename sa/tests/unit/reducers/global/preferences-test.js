import { test, module } from 'qunit';
import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'sa/actions/types';
import reducer from 'sa/reducers/global/preferences';

module('Unit | Reducers | Global | Preferences');

test('should return the initial state', function(assert) {
  const result = reducer(undefined, {});

  assert.deepEqual(result, {
    theme: 'DARK'
  });
});

test('UPDATE will only alter theme when the action.theme is legit', function(assert) {
  let result = reducer(undefined, {});

  assert.deepEqual(result, {
    theme: 'DARK'
  });

  result = reducer(result, {
    type: ACTION_TYPES.UPDATE_PREFERENCES_THEME,
    theme: undefined
  });

  assert.deepEqual(result, {
    theme: 'DARK'
  });

  result = reducer(result, {
    type: ACTION_TYPES.UPDATE_PREFERENCES_THEME,
    theme: 'undefined'
  });

  assert.deepEqual(result, {
    theme: 'DARK'
  });

  result = reducer(result, {
    type: ACTION_TYPES.UPDATE_PREFERENCES_THEME,
    theme: null
  });

  assert.deepEqual(result, {
    theme: 'DARK'
  });

  result = reducer(result, {
    type: ACTION_TYPES.UPDATE_PREFERENCES_THEME,
    theme: 'null'
  });

  assert.deepEqual(result, {
    theme: 'DARK'
  });
});

test('REHYDRATE will only alter theme when the JSON data structure is legit', function(assert) {
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
