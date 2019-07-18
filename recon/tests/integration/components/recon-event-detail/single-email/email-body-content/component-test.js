import wait from 'ember-test-helpers/wait';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import EmberObject from '@ember/object';

let emailData;

module('Integration | Component | recon-event-detail/single-email/email-body-content', function(hooks) {
  setupRenderingTest(hooks);
  hooks.beforeEach(function() {
    emailData = {
      'messageId': '6eea4274b865446289540926194068e9',
      'messageKind': 'SMTP',
      'from': 'eddard.stark@verizon.net',
      'to': ['sansa.stark@verizon.net', 'arya.stark@verizon.net', 'robb.stark@verizon.net'],
      'replyTo': '',
      'cc': [],
      'bcc': ['jon.snow@verizon.net'],
      'subject': 'Winter is coming. Did anyone pay the plow guy?',
      'sent': 1554308061869,
      'received': 1554308861869,
      'headers': [
        {
          'name': 'Received',
          'value': 'from gwia.nw.gwu.edu ([161.253.150.112])rn  by iron3-smtp.tops.gwu.edu with ESMTP; 13 Feb 2008 11:55:13 -0500'
        },
        {
          'name': 'Received',
          'value': 'from GWIADOM-MTA by gwia.nw.gwu.edurntwith Novell_GroupWise; Wed, 13 Feb 2008 11:55:13 -0500'
        },
        { 'name': 'Mime-Version', 'value': '1.0' },
        { 'name': 'Content-Type', 'value': 'text/plain; charset=US-ASCII' },
        { 'name': 'Delivered-To', 'value': 'sansa.stark@verizon.net' }],
      'bodyContentType': 'PlainText',
      'bodyContent': 'email message text1 ...'
    };
  });

  test('renders single email body content, if data present', async function(assert) {
    this.set('email', EmberObject.create(emailData));
    await render(hbs`{{recon-event-detail/single-email/email-body-content email=email}}`);
    return wait().then(() => {
      assert.ok(find('.email-body-text'), 'show single email message content');
      const str = find('.email-body-text').textContent.trim().replace(/\s/g, '').substring(0, 200);
      assert.equal(str, 'emailmessagetext1...');
    });
  });

  test('renders single email html body content, if data present', async function(assert) {
    emailData = EmberObject.create(emailData);
    emailData.bodyContent = '&lt;BODY&gt;&lt;P&gt;email message text content&lt;/P&gt;&lt;/BODY&gt;';
    this.set('email', EmberObject.create(emailData));
    await render(hbs`{{recon-event-detail/single-email/email-body-content email=email}}`);
    return wait().then(() => {
      assert.ok(find('.email-body-text'), 'show single email message content');
      const str = find('.email-body-text').textContent.trim().replace(/\s/g, '').substring(0, 200);
      assert.equal(str, 'emailmessagetextcontent');
    });
  });

  test('do not renders single email body content, if data is not present', async function(assert) {
    this.set('email', null);
    await render(hbs`{{recon-event-detail/single-email/email-body-content email=email}}`);
    return wait().then(() => {
      assert.equal(find('.email-body-text').textContent.trim().length, 0);
    });
  });

});


