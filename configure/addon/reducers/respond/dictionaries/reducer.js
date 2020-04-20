import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'configure/actions/types/respond';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';

const initialState = {
  categoryTags: []
};

export default reduxActions.handleActions({
  [ACTION_TYPES.FETCH_CATEGORY_TAGS]: (state, action) => (
    handle(state, action, {
      start: (s) => s.set('categoryTags', []),
      failure: (s) => s.set('categoryTags', []),
      success: (s) => s.set('categoryTags', action.payload.data) }
    )
  )
}, Immutable.from(initialState));