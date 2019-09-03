import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import * as ACTION_TYPES from 'recon/actions/types';

const emailsInitialState = Immutable.from({
  isEmail: true,
  emails: null,
  renderIds: null,
  renderedAll: null
});

const emailsReducer = handleActions({

  [ACTION_TYPES.INITIALIZE]: (state) => {
    return emailsInitialState.merge({
      isEmail: state.isEmail
    });
  },

  [ACTION_TYPES.EMAIL_RENDER_NEXT]: (state, { payload }) => {
    const ids = payload.map((e) => e.messageId);
    return state.set('renderIds', state.renderIds ? state.renderIds.concat(ids) : ids);
  },

  [ACTION_TYPES.EMAIL_RECEIVE_PAGE]: (state, { payload }) => {
    const emailData = state.emails ? state.emails.concat(payload.data) : payload.data;
    return state.merge({ ...state, emails: emailData, renderedAll: payload.meta.complete });
  }

}, emailsInitialState);

export default emailsReducer;
