import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import * as ACTION_TYPES from 'recon/actions/types';

const emailsInitialState = Immutable.from({
  isEmail: true,
  emails: null,
  renderIds: null,
  renderedAll: null
});

/**
 * Checks if the last email in the current set of emails is split. If yes, merges the 1st email in the new batch with
 * the last email of the previous batch
 *
 * @param currentEmails - The current set of emails in the state
 * @param newEmails - The new set of emails received as payload
 * @returns {*} - Merged list of emails
 * @private
 */
const _mergeSplitEmails = (currentEmails, newEmails) => {
  if (currentEmails[currentEmails.length - 1].partial) { // if the last email in the array (received in the previous batch) was partial,
    let partialEmail = currentEmails[currentEmails.length - 1];
    // append the body of the first email in the new batch to the last email of the previous batch
    const [remaining] = newEmails;
    const combinedBody = partialEmail.bodyContent.concat(remaining.bodyContent);
    partialEmail = partialEmail.merge({ bodyContent: combinedBody, partial: false });
    newEmails.shift(); // remove the first item in the new list
    currentEmails = currentEmails.set(currentEmails.length - 1, partialEmail);
  }
  return currentEmails.concat(newEmails);
};

const emailsReducer = handleActions({

  [ACTION_TYPES.INITIALIZE]: (state) => {
    return emailsInitialState.merge({
      isEmail: state.isEmail
    });
  },

  [ACTION_TYPES.CLOSE_RECON]: (state) => {
    return state.merge(emailsInitialState);
  },

  [ACTION_TYPES.EMAIL_RENDER_NEXT]: (state, { payload }) => {
    const ids = payload.map((e) => e.messageId);
    return state.set('renderIds', state.renderIds ? state.renderIds.concat(ids) : ids);
  },

  [ACTION_TYPES.EMAIL_RECEIVE_PAGE]: (state, { payload }) => {
    const { data, meta } = payload;
    let emailData;
    if (state.emails) {
      emailData = _mergeSplitEmails(state.emails, data);
    } else {
      emailData = data;
    }
    if (emailData && emailData.length > 0 && meta['RECON-EMAIL-MESSAGE-SPLIT']) {
      // if the meta indicates that the email is spit, set the "partial" flag on the last email
      emailData[emailData.length - 1].partial = true;
    }
    return state.merge({ ...state, emails: emailData, renderedAll: meta.complete });
  }

}, emailsInitialState);

export default emailsReducer;
