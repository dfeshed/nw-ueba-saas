import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import respondReducer from 'respond/reducers/respond/riac';
import ACTION_TYPES from 'respond/actions/types';
import makePackAction from '../../../helpers/make-pack-action';
import { LIFECYCLE } from 'redux-pack';

function generatePayload(riac) {
  return {
    data: {
      enabled: riac
    }
  };
}

module('Unit | Reducers | respond | riac', function(hooks) {
  setupTest(hooks);

  test('test riac reducer', function(assert) {
    const trueAction = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.GET_RIAC_SETTINGS,
      payload: generatePayload(true)
    });

    const endState = respondReducer(undefined, trueAction);
    assert.equal(endState.isRiacEnabled, true);

  });
});
