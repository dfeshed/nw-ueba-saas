import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, find, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import EmberObject from '@ember/object';

let emailData;

module('Integration | Component | recon-event-detail/single-email/email-header', function(hooks) {
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

  test('renders single email header content, if data present', async function(assert) {
    this.set('email', EmberObject.create(emailData));
    await render(hbs`{{recon-event-detail/single-email/email-header email=email}}`);
    assert.equal(findAll('.recon-email-header').length, 1);
    assert.equal(findAll('.rsa-icon-arrow-right-12-filled').length, 1, 'Additional Header is collapsed by default');
    const str = find('.recon-email-header').textContent.trim().replace(/\s/g, '').substring(0, 200);
    assert.equal(str, 'fromeddard.stark@verizon.nettosansa.stark@verizon.net,arya.stark@verizon.net,robb.stark@verizon.netbccjon.snow@verizon.netsubjectWinteriscoming.Didanyonepaytheplowguy?AdditionalHeaderDetails');
  });

  test('Expand/Collapse all additional headers on click of additional header details', async function(assert) {
    this.set('email', EmberObject.create(emailData));
    await render(hbs`{{recon-event-detail/single-email/email-header email=email}}`);
    assert.equal(findAll('.rsa-icon-arrow-right-12-filled').length, 1, 'Additional Header is collapsed by default');
    await click(findAll('.rsa-icon-arrow-right-12-filled')[0]);
    assert.equal(findAll('.rsa-icon-arrow-down-12-filled').length, 1, 'Additional Header is expanded');
    const str = find('.recon-email-header').textContent.trim().replace(/\s/g, '').substring(0, 200);
    assert.equal(str, 'fromeddard.stark@verizon.nettosansa.stark@verizon.net,arya.stark@verizon.net,robb.stark@verizon.netbccjon.snow@verizon.netsubjectWinteriscoming.Didanyonepaytheplowguy?AdditionalHeaderDetailsReceivedfr');
    await click(findAll('.rsa-icon-arrow-down-12-filled')[0]);
    assert.equal(findAll('.rsa-icon-arrow-right-12-filled').length, 1, 'Additional Header is collapsed again');
    const strValue = find('.recon-email-header').textContent.trim().replace(/\s/g, '').substring(0, 200);
    assert.equal(strValue, 'fromeddard.stark@verizon.nettosansa.stark@verizon.net,arya.stark@verizon.net,robb.stark@verizon.netbccjon.snow@verizon.netsubjectWinteriscoming.Didanyonepaytheplowguy?AdditionalHeaderDetails');
  });

  test('renders attachment header & content, if attachment present', async function(assert) {
    this.set('email', EmberObject.create(emailData));
    await render(hbs`{{recon-event-detail/single-email/email-header email=email}}`);
    assert.equal(findAll('span.attachment-meta').length, 0);
  });

  test('renders attachment header & content, if attachment present', async function(assert) {
    emailData.attachments = [
      {
        'attachmentId': '15337f91add74662a7ae1fbf7e9c2084',
        'filename': 'windsofwinter.docx',
        'contentType': 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
        'hash': 'fe0489f04e2f34164f07af8a3fcc7ec297508d81526c204755b84aac105b1560',
        'url': 'http://...'
      }
    ];
    this.set('email', EmberObject.create(emailData));
    await render(hbs`{{recon-event-detail/single-email/email-header email=email hasEmailAttachments=true}}`);
    assert.equal(findAll('span.attachment-meta').length, 1);
    assert.equal(findAll('span.attachment-meta')[0].children[0].getAttribute('href'), 'http://...');
    assert.equal(findAll('span.attachment-meta')[0].innerText, 'windsofwinter.docx');
  });

});
