import { handleActions } from 'redux-actions';
import Immutable from 'seamless-immutable';
import { handle } from 'redux-pack';

import * as ACTION_TYPES from 'investigate-shared/actions/types';

const fileStatusState = Immutable.from({
  restrictedFileList: []
});

const fileStatusReducer = handleActions({

  [ACTION_TYPES.SET_RESTRICTED_FILE_LIST]: (state, action) => {
    return handle(state, action, {
      success: (s) => {
        const { payload: { data } } = action;
        return s.set('restrictedFileList', data);
      }
    });
  }
}, fileStatusState);

export default fileStatusReducer;
