import { test, module } from 'qunit';
import reducer from 'recon/reducers/emails/reducer';
import * as ACTION_TYPES from 'recon/actions/types';
import Immutable from 'seamless-immutable';
import { slicedEmailData } from '../../../helpers/data/index';

module('Unit | Reducers | Emails | Recon');

const initialState = Immutable.from({
  emails: null,
  renderIds: null
});

test('test EMAIL_RECEIVE_PAGE action handler', function(assert) {
  const action = {
    type: ACTION_TYPES.EMAIL_RECEIVE_PAGE,
    payload: {
      emails: slicedEmailData
    }
  };
  const result = reducer(initialState, action);
  assert.equal(result.emails.emails.length, 2, 'set email content to email state');
});

test('test EMAIL_RENDER_NEXT action handler', function(assert) {
  const action = {
    type: ACTION_TYPES.EMAIL_RENDER_NEXT,
    payload: [
      ...slicedEmailData
    ]
  };
  const result = reducer(initialState, action);
  assert.equal(result.renderIds.length, 2, 'set email renderIds to email state');
});