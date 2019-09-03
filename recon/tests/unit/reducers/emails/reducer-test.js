import { test, module } from 'qunit';
import reducer from 'recon/reducers/emails/reducer';
import * as ACTION_TYPES from 'recon/actions/types';
import Immutable from 'seamless-immutable';
import emailData from '../../../data/subscriptions/reconstruction-email-data/stream/data';

module('Unit | Reducers | Emails | Recon');

const initialState = Immutable.from({
  isEmail: false,
  emails: null,
  renderIds: null,
  renderedAll: null
});

test('test email INITIALIZE action handler', function(assert) {
  const currentState = initialState.merge({ isEmail: true });
  const result = reducer(currentState, {
    type: ACTION_TYPES.INITIALIZE
  });
  assert.equal(result.isEmail, true, 'initialize isEmail field in recon email');
  assert.equal(result.emails, null, 'initialize emails field in recon email');
  assert.equal(result.renderIds, null, 'initialize renderIds field in recon email');
});

test('test EMAIL_RECEIVE_PAGE action handler', function(assert) {
  const action = {
    type: ACTION_TYPES.EMAIL_RECEIVE_PAGE,
    payload: {
      data: emailData,
      meta: {
        complete: true
      }
    }
  };
  const result = reducer(initialState, action);
  assert.equal(result.emails.length, 4, 'set email content to email state');
});

test('test EMAIL_RENDER_NEXT action handler', function(assert) {
  const action = {
    type: ACTION_TYPES.EMAIL_RENDER_NEXT,
    payload: [
      ...emailData
    ]
  };
  const result = reducer(initialState, action);
  assert.equal(result.renderIds.length, 4, 'set email renderIds to email state');
});
