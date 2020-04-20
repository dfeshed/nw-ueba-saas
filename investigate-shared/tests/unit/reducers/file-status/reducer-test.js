import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-shared/reducers/file-status/reducer';
import * as ACTION_TYPES from 'investigate-shared/actions/types';
import makePackAction from '../../../helpers/make-pack-action';
import { LIFECYCLE } from 'redux-pack';
import { setupTest } from 'ember-qunit';

module('Unit | Reducers | investigate', function(hooks) {
  setupTest(hooks);

  test('The SET_RESTRICTED_FILE_LIST sets the restricted file list', function(assert) {
    const previous = Immutable.from({
      restrictedFileList: []
    });

    const newAction = makePackAction(LIFECYCLE.SUCCESS, {
      meta: {
        belongsTo: 'FILE'
      },
      type: ACTION_TYPES.SET_RESTRICTED_FILE_LIST,
      payload: { data: ['cmd.exe', 'powershel.exe'] }
    });
    const newEndState = reducer(previous, newAction);
    assert.equal(newEndState.restrictedFileList.length, 2);
  });

});
