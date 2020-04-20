import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-events/reducers/investigate/meta-group/reducer';
import * as ACTION_TYPES from 'investigate-events/actions/types';
import { LIFECYCLE } from 'redux-pack';
import sort from 'fast-sort';
import makePackAction from '../../../helpers/make-pack-action';
import DEFAULT_META_GROUPS from '../../../data/subscriptions/meta-group/findAll/data';

module('Unit | Reducers | meta-group | Investigate');

test('Shall get meta groups from server', function(assert) {
  const previous = Immutable.from({
    metaGroups: null
  });
  const successAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.META_GROUPS_RETRIEVE,
    payload: { data: DEFAULT_META_GROUPS }
  });
  const newEndState = reducer(previous, successAction);
  assert.deepEqual(newEndState.metaGroups, DEFAULT_META_GROUPS, 'meta groups shall be fetched');
});

test('Shall sort meta groups alphabetically', function(assert) {
  const previous = Immutable.from({
    metaGroups: null
  });
  const successAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.META_GROUPS_RETRIEVE,
    payload: { data: DEFAULT_META_GROUPS }
  });
  const newEndState = reducer(previous, successAction);
  const sorted = sort(DEFAULT_META_GROUPS).by([{ asc: (group) => group.name.toUpperCase() }]);

  assert.deepEqual(newEndState.metaGroups, sorted, 'meta groups shall be fetched');
  assert.deepEqual(newEndState.metaGroups[0], sorted[0], 'meta groups shall be sorted alphabetically');
  assert.deepEqual(newEndState.metaGroups[newEndState.metaGroups.length - 1], sorted[sorted.length - 1],
    'meta groups shall be sorted alphabetically');
});
