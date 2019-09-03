import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, find, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import EmberObject from '@ember/object';
import emailData from '../../../../../data/subscriptions/reconstruction-email-data/stream/data';

module('Integration | Component | recon-event-detail/single-email/email-header', function(hooks) {
  setupRenderingTest(hooks);

  test('renders single email header content, if data present', async function(assert) {
    this.set('email', EmberObject.create(emailData[1]));
    await render(hbs`{{recon-event-detail/single-email/email-header email=email}}`);
    assert.equal(findAll('.recon-email-header').length, 1);
    assert.equal(findAll('.rsa-icon-arrow-right-12-filled').length, 1, 'Additional Header is collapsed by default');
    const str = find('.recon-email-header').textContent.trim().replace(/\s/g, '').substring(0, 200);
    assert.equal(str, 'fromeddard.stark@verizon.nettosansa.stark@verizon.net,arya.stark@verizon.net,robb.stark@verizon.netbccjon.snow@verizon.netsubjectWinteriscoming.Didanyonepaytheplowguy?AdditionalHeaderDetails');
  });

  test('Expand/Collapse all additional headers on click of additional header details', async function(assert) {
    this.set('email', EmberObject.create(emailData[1]));
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
    this.set('email', EmberObject.create(emailData[1]));
    await render(hbs`{{recon-event-detail/single-email/email-header email=email}}`);
    assert.equal(findAll('span.attachment-meta').length, 0);
  });

  test('renders attachment header & content, if attachment present', async function(assert) {
    this.set('email', EmberObject.create(emailData[1]));
    await render(hbs`{{recon-event-detail/single-email/email-header email=email hasEmailAttachments=true}}`);
    assert.equal(findAll('span.attachment-meta').length, 2);
    assert.equal(findAll('span.attachment-meta')[0].children[0].getAttribute('href'), 'http://...');
    assert.equal(findAll('span.attachment-meta')[0].innerText, 'thewindsofwinter.docx');
    assert.equal(findAll('span.attachment-meta')[1].innerText, 'windsofwinter.docx');
  });
});
