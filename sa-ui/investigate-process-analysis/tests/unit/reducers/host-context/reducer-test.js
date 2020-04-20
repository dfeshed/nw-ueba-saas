import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-process-analysis/reducers/host-context/reducer';
import * as ACTION_TYPES from 'investigate-process-analysis/actions/types';
import makePackAction from '../../../helpers/make-pack-action';
import { LIFECYCLE } from 'redux-pack';

module('Unit | Reducers | host-context', function() {

  test('SET_HOST_CONTEXT will sets file property', function(assert) {
    const previous = Immutable.from({
      hostList: null
    });
    const successAction = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.SET_HOST_CONTEXT,
      payload: new Array(10)
    });
    const newEndState = reducer(previous, successAction);
    assert.equal(newEndState.hostList.length, 10);
  });
});

