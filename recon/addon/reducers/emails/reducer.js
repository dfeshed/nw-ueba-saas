import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import * as ACTION_TYPES from 'recon/actions/types';

const emailsInitialState = Immutable.from({
  isEmail: true,
  emails: null,
  renderIds: null
});

const emailsReducer = handleActions({

  [ACTION_TYPES.EMAIL_RENDER_NEXT]: (state, { payload }) => {
    const ids = payload.map((e) => e.messageId);
    return state.set('renderIds', state.renderIds ? state.renderIds.concat(ids) : ids);
  },

  [ACTION_TYPES.EMAIL_RECEIVE_PAGE]: (state, { payload }) => {
    return state.set('emails', state.emails ? state.emails.concat(payload) : payload);
  }

}, emailsInitialState);

export default emailsReducer;
