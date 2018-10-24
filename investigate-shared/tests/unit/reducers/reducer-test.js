import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-shared/reducers/endpoint-filter/reducer';
import * as ACTION_TYPES from 'investigate-shared/actions/types';
import makePackAction from '../../helpers/make-pack-action';
import { LIFECYCLE } from 'redux-pack';
import { setupTest } from 'ember-qunit';

module('Unit | Reducers | investigate-files | file-filter', function(hooks) {
  setupTest(hooks);
  test('should return the initial state', function(assert) {
    const result = reducer(undefined, {});
    assert.deepEqual(result, {
      selectedFilter: null,
      expressionList: [],
      savedFilterList: []
    });
  });

  test('The GET_FILTER get the filters', function(assert) {
    const previous = Immutable.from({
      savedFilterList: []
    });

    const newAction = makePackAction(LIFECYCLE.SUCCESS, {
      meta: {
        belongsTo: 'FILE'
      },
      type: ACTION_TYPES.GET_FILTER,
      payload: { data: [ { filterType: 'FILE' }, { filterType: 'MACHINE' }] }
    });
    const newEndState = reducer(previous, newAction);
    assert.equal(newEndState.savedFilterList.length, 1);
  });

  test('The SAVE_FILTER set the filters', function(assert) {
    const previous = Immutable.from({
      savedFilterList: []
    });

    const newAction = makePackAction(LIFECYCLE.SUCCESS, {
      meta: {
        belongsTo: 'FILE'
      },
      type: ACTION_TYPES.SAVE_FILTER,
      payload: { data: { filterType: 'FILE' } }
    });
    const newEndState = reducer(previous, newAction);
    assert.equal(newEndState.savedFilterList.length, 1);
  });

  test('The DELETE_FILTER set the filters', function(assert) {
    const previous = Immutable.from({
      savedFilterList: [{
        id: 1
      }],
      expressionList: [{}]
    });

    const newAction = makePackAction(LIFECYCLE.SUCCESS, {
      meta: {
        belongsTo: 'FILE'
      },
      type: ACTION_TYPES.DELETE_FILTER,
      payload: { data: { id: 1 } }
    });
    const newEndState = reducer(previous, newAction);
    assert.equal(newEndState.savedFilterList.length, 0);
  });

  test('RESET_FILTER reset saved filter', function(assert) {
    const previous = Immutable.from({
      selectedFilter: {},
      expressionList: [{}]
    });
    const result = reducer(previous, { type: ACTION_TYPES.RESET_FILTER });
    assert.equal(result.selectedFilter.id, 1);
    assert.equal(result.selectedFilter.criteria.expressionList.length, 0);
  });
});

