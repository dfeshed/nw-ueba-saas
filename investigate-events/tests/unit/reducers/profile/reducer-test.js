import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-events/reducers/investigate/profile/reducer';
import * as ACTION_TYPES from 'investigate-events/actions/types';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../helpers/make-pack-action';
import { DEFAULT_PROFILES } from '../../../helpers/redux-data-helper';
import sort from 'fast-sort';

module('Unit | Reducers | profile | Investigate');

const profiles1 = [...DEFAULT_PROFILES, { id: 2468, name: 'Hello Profile' }];
const profiles2 = [{ id: 9876, name: 'ZZZZZ' }, ...DEFAULT_PROFILES, { id: 9876, name: 'New Profile 1' }];

test('Shall get profiles from server', function(assert) {
  const previous = Immutable.from({
    profile: null
  });

  const successAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.PROFILES_RETRIEVE,
    payload: { data: profiles1 }
  });
  const newEndState = reducer(previous, successAction);
  assert.deepEqual(newEndState.profiles, profiles1, 'profiles shall be fetched');
});

test('Shall sort profiles alphabetically', function(assert) {
  const previous = Immutable.from({
    profile: null
  });

  const successAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.PROFILES_RETRIEVE,
    payload: { data: profiles2 }
  });
  const newEndState = reducer(previous, successAction);
  const sorted = sort(profiles2).by([{ asc: (group) => group.name.toUpperCase() }]);

  assert.deepEqual(newEndState.profiles, sorted, 'profiles shall be fetched');
  assert.deepEqual(newEndState.profiles[0], sorted[0], 'profiles shall be sorted alphabetically');
  assert.deepEqual(newEndState.profiles[newEndState.profiles.length - 1], sorted[sorted.length - 1],
    'profiles shall be sorted alphabetically');
});
