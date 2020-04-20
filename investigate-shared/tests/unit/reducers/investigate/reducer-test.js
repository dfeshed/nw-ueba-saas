import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-shared/reducers/investigate/reducer';
import * as ACTION_TYPES from 'investigate-shared/actions/types';
import makePackAction from '../../../helpers/make-pack-action';
import { LIFECYCLE } from 'redux-pack';
import { setupTest } from 'ember-qunit';

module('Unit | Reducers | investigate', function(hooks) {
  setupTest(hooks);

  test('The SET_INVESTIGATE_PREFERENCE get the filters', function(assert) {
    const previous = Immutable.from({
      serviceId: null
    });

    const newAction = makePackAction(LIFECYCLE.SUCCESS, {
      meta: {
        belongsTo: 'FILE'
      },
      type: ACTION_TYPES.SET_INVESTIGATE_PREFERENCE,
      payload: { data: '12345' }
    });
    const newEndState = reducer(previous, newAction);
    assert.equal(newEndState.serviceId, '12345');
  });

  test('The SET_INVESTIGATE_PREFERENCE sets serviceId as -1 on start', function(assert) {
    const previous = Immutable.from({
      serviceId: null
    });
    const newAction = makePackAction(LIFECYCLE.START, {
      type: ACTION_TYPES.SET_INVESTIGATE_PREFERENCE,
      payload: {}
    });
    const newEndState = reducer(previous, newAction);
    assert.equal(newEndState.serviceId, '-1', 'On start service id is -1');
  });
});
