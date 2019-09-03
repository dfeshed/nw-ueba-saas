import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { renderedEmails, hasNoEmailContent, hasRenderIds, hasEmailAttachments } from 'recon/reducers/emails/selectors';
import emailData from '../../../data/subscriptions/reconstruction-email-data/stream/data';

module('Unit | selector | emails');

const renderedEmailContentTests = (selector) => {

  return {
    noEmailNoRenderIds: selector(Immutable.from({
      emails: {
        emails: [],
        renderIds: []
      }
    })),
    hasEmailNoRenderIds: selector(Immutable.from({
      emails: {
        emails: emailData,
        renderIds: []
      }
    })),
    noEmailHasRenderIds: selector(Immutable.from({
      emails: {
        emails: [],
        renderIds: ['6eea4274b865446289540926194068e9', '6eea4274b865446289540926194068e8']
      }
    })),
    hasEmailHasRenderIds: selector(Immutable.from({
      emails: {
        emails: emailData,
        renderIds: ['6eea4274b865446289540926194068e9', '6eea4274b865446289540926194068e8']
      }
    }))
  };
};

test('renderedEmails', function(assert) {
  assert.expect(4);
  const tests = renderedEmailContentTests(renderedEmails);

  assert.equal(tests.noEmailNoRenderIds.length, 0, 'renderedEmail should return empty array, when emails/render IDs missing');
  assert.equal(tests.hasEmailNoRenderIds.length, 0, 'renderedEmail should return empty array, when has render IDs');
  assert.equal(tests.noEmailHasRenderIds.length, 0, 'renderedEmail should return empty array, when no emails');
  assert.equal(tests.hasEmailHasRenderIds.length, 4, 'renderedEmail should not return empty array, when emails/render Ids exists');

});

test('hasNoEmailContent', function(assert) {
  assert.expect(4);

  const tests = {
    noEmailContent: hasNoEmailContent(Immutable.from({
      emails: {}
    })),
    emailContentNull: hasNoEmailContent(Immutable.from({
      emails: {
        emails: null
      }
    })),
    emailContentEmpty: hasNoEmailContent(Immutable.from({
      emails: {
        emails: []
      }
    })),
    hasEmailContent: hasNoEmailContent(Immutable.from({
      emails: {
        emails: emailData
      }
    }))
  };
  assert.equal(tests.noEmailContent, true, 'hasNoEmailContent should return true, when emails missing');
  assert.equal(tests.emailContentNull, true, 'hasNoEmailContent should return true, when emails null');
  assert.equal(tests.emailContentEmpty, true, 'hasNoEmailContent should return true, when emails empty');
  assert.equal(tests.hasEmailContent, false, 'hasNoEmailContent should return false, when emails present');
});

test('hasRenderIds', function(assert) {
  assert.expect(4);

  const tests = {
    noEmailContent: hasRenderIds(Immutable.from({
      emails: {}
    })),
    renderIdsNull: hasRenderIds(Immutable.from({
      emails: {
        renderIds: null
      }
    })),
    renderIdsEmpty: hasRenderIds(Immutable.from({
      emails: {
        renderIds: []
      }
    })),
    hasRenderIds: hasRenderIds(Immutable.from({
      emails: {
        renderIds: ['6eea4274b865446289540926194068e9', '6eea4274b865446289540926194068e8']
      }
    }))
  };
  assert.equal(tests.noEmailContent, false, 'hasRenderIds should return false, when no email content');
  assert.equal(tests.renderIdsNull, false, 'hasRenderIds should return false, when renderIds missing');
  assert.equal(tests.renderIdsEmpty, false, 'hasRenderIds should return false, when renderIds null');
  assert.equal(tests.hasRenderIds, true, 'hasRenderIds should return true, when renderIds present');
});

test('hasEmailAttachments', function(assert) {
  assert.expect(4);

  const tests = {
    noEmailContent: hasEmailAttachments(Immutable.from({
      emails: {}
    })),
    attachmentsNull: hasEmailAttachments(Immutable.from({
      emails: {
        attachments: null
      }
    })),
    attachmentsEmpty: hasEmailAttachments(Immutable.from({
      emails: {
        attachments: []
      }
    })),
    hasAttachments: hasEmailAttachments(Immutable.from({
      emails: {
        emails: emailData
      }
    }))
  };
  assert.equal(tests.noEmailContent, false, 'hasEmailAttachments should return false, when no email content');
  assert.equal(tests.attachmentsNull, false, 'hasEmailAttachments should return false, when attachments missing');
  assert.equal(tests.attachmentsEmpty, false, 'hasEmailAttachments should return false, when attachments null');
  assert.equal(tests.hasAttachments, true, 'hasEmailAttachments should return true, when attachments present');
});


