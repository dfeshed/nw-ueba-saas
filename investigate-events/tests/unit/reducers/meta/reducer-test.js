import { test, module } from 'qunit';
import Immutable from 'seamless-immutable';

import * as ACTION_TYPES from 'investigate-events/actions/types';
// import ReduxDataHelper from '../../../helpers/redux-data-helper';
import reducer from 'investigate-events/reducers/investigate/meta/reducer';

module('Unit | Reducers | meta | Investigate');

const values = {
  data: [
    {
      value: 'foo',
      count: 9821
    },
    {
      value: 'bar',
      count: 9638
    }
  ],
  status: 'complete',
  complete: true
};

test('ACTION_TYPES.INIT_STREAM_FOR_META updates values for meta on stream init', function(assert) {
  const prevState = Immutable.from({
    meta: [
      {
        info: { metaName: 'action' }
      }
    ]
  });
  const action = {
    type: ACTION_TYPES.INIT_STREAM_FOR_META,
    payload: {
      keyName: 'action',
      value: {
        data: [],
        status: 'streaming'
      }
    }
  };
  const result = reducer(prevState, action);
  assert.equal(result.meta.find((e) => e.info.metaName === 'action').values.status, 'streaming');
});

test('ACTION_TYPES.SET_META_RESPONSE populates respective meta with response values', function(assert) {
  const prevState = Immutable.from({
    meta: [
      {
        info: { metaName: 'action' },
        values: {
          data: [],
          status: 'streaming'
        }
      }
    ]
  });
  const action = {
    type: ACTION_TYPES.SET_META_RESPONSE,
    payload: {
      keyName: 'action',
      valueProps: values
    }
  };
  const result = reducer(prevState, action);
  const metaKey = result.meta.find((e) => e.info.metaName === 'action');
  assert.deepEqual(metaKey.values.data, values.data, 'Sets the values and their counts');
  assert.equal(metaKey.values.status, 'complete');
});

test('ACTION_TYPES.RESET_META_VALUES refreshes metaKeyState objects with default info, drops their current values', function(assert) {
  const prevState = Immutable.from({
    meta: [
      {
        info: { metaName: 'action' },
        values
      }
    ]
  });

  const metaKeyStates = [{
    info: { metaName: 'action', isOpen: true }
  }, {
    info: { metaName: 'foo', isOpen: false }
  }];
  const action = {
    type: ACTION_TYPES.RESET_META_VALUES,
    payload: { metaKeyStates }
  };
  const result = reducer(prevState, action);
  const metaKey = result.meta.find((e) => e.info.metaName === 'action');
  assert.notOk(metaKey.values, 'Did not find values object');
});

test('ACTION_TYPES.TOGGLE_META_FLAG toggles isOpen flag', function(assert) {
  const prevState = Immutable.from({
    meta: [
      {
        info: { metaName: 'action', isOpen: false },
        values
      }
    ]
  });
  const metaKey = { name: 'action', isOpen: true };
  const action = {
    type: ACTION_TYPES.TOGGLE_META_FLAG,
    payload: { metaKey }
  };

  const result = reducer(prevState, action);
  const meta = result.meta.find((e) => e.info.metaName === 'action');
  assert.ok(meta.info.isOpen, 'Toggles the flag to isOpen: true');
});