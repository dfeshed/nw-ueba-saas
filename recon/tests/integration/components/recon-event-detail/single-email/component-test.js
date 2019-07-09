import wait from 'ember-test-helpers/wait';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import EmberObject from '@ember/object';

module('Integration | Component | recon-event-detail/single-email', function(hooks) {
  setupRenderingTest(hooks);

  test('renders single email content if data present', async function(assert) {
    this.set('email', EmberObject.create({
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
        { 'name': 'Received', 'value': 'from gwia.nw.gwu.edu ([161.253.150.112])rn  by iron3-smtp.tops.gwu.edu with ESMTP; 13 Feb 2008 11:55:13 -0500' },
        { 'name': 'Received', 'value': 'from GWIADOM-MTA by gwia.nw.gwu.edurntwith Novell_GroupWise; Wed, 13 Feb 2008 11:55:13 -0500' },
        { 'name': 'Mime-Version', 'value': '1.0' },
        { 'name': 'Content-Type', 'value': 'text/plain; charset=US-ASCII' },
        { 'name': 'Delivered-To', 'value': 'sansa.stark@verizon.net' }],
      'bodyContentType': 'PlainText',
      'bodyContent': 'email message text1 ...'
    }));
    await render(hbs`{{recon-event-detail/single-email email=email}}`);
    return wait().then(() => {
      assert.ok(find('.recon-email-view'), 'single email view is rendered');
      const str = find('.recon-email-view').textContent.trim().replace(/\s/g, '').substring(0, 200);
      assert.equal(str, 'fromeddard.stark@verizon.nettosansa.stark@verizon.net,arya.stark@verizon.net,robb.stark@verizon.netbccjon.snow@verizon.netsubjectWinteriscoming.Didanyonepaytheplowguy?AdditionalHeaderDetailsemailmessa');
    });
  });
});
