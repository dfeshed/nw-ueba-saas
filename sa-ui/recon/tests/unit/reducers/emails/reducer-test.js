import { test, module } from 'qunit';
import reducer from 'recon/reducers/emails/reducer';
import * as ACTION_TYPES from 'recon/actions/types';
import Immutable from 'seamless-immutable';
import emailData from '../../../data/subscriptions/reconstruction-email-data/stream/data';
import _ from 'lodash';

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
  assert.ok(result.emails.length > 4, 'set email content to email state');
});

test('test EMAIL_RECEIVE_PAGE action handler - handle split email body', function(assert) {
  const emailData1 = _.cloneDeep(emailData).sort((a, b) => a.messageId < b.messageId);
  let part1 = _.cloneDeep(emailData1.slice(0, 4));
  let splitEmail = part1[part1.length - 1];
  splitEmail.bodyContent = splitEmail.bodyContent.substring(0, 2000);

  let action = {
    type: ACTION_TYPES.EMAIL_RECEIVE_PAGE,
    payload: {
      data: part1,
      meta: {
        'RECON-EMAIL-MESSAGE-SPLIT': true,
        complete: false
      }
    }
  };
  const result1 = reducer(initialState, action);
  assert.ok(result1.emails.length === 4, 'First batch of emails stored');
  assert.equal(result1.emails[3].bodyContent.length, 2000, 'Email body is 2000 char');
  assert.ok(result1.emails[3].partial, 'Last email marked partial');

  part1 = _.cloneDeep(emailData1.slice(0, 4));
  splitEmail = part1[part1.length - 1];
  const part2FirstMail = _.cloneDeep(part1[part1.length - 1]);
  part2FirstMail.bodyContent = splitEmail.bodyContent.substring(2000, splitEmail.bodyContent.length);
  const part2 = emailData1.slice(4, emailData1.length);

  action = {
    type: ACTION_TYPES.EMAIL_RECEIVE_PAGE,
    payload: {
      data: [part2FirstMail].concat(part2),
      meta: {
        complete: true
      }
    }
  };

  const result2 = reducer(result1, action);
  assert.equal(result2.emails.length, 6, 'Next batch of emails stored');
  assert.ok(result2.emails[3].bodyContent.length > 3000, 'Partial email body is updated with the complete content');
  assert.equal(result2.emails[3].partial, false, 'Partial flag cleared');
});

test('test EMAIL_RENDER_NEXT action handler', function(assert) {
  const action = {
    type: ACTION_TYPES.EMAIL_RENDER_NEXT,
    payload: [
      ...emailData
    ]
  };
  const result = reducer(initialState, action);
  assert.ok(result.renderIds.length > 4, 'set email renderIds to email state');
});

test('test CLOSE_RECON', function(assert) {

  const initialEmailState = Immutable.from({
    isEmail: true,
    emails: null,
    renderIds: null,
    renderedAll: null
  });

  const currentState = initialEmailState.merge({
    emails: ['foo'],
    renderIds: ['1'],
    renderedAll: true
  });

  const action = {
    type: ACTION_TYPES.CLOSE_RECON
  };

  const result = reducer(currentState, action);
  assert.deepEqual(result, initialEmailState);

});
