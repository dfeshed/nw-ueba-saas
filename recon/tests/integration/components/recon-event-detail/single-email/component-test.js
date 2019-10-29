import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, findAll, waitUntil } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import EmberObject from '@ember/object';
import emailData from '../../../../data/subscriptions/reconstruction-email-data/stream/data';

const _first200 = (str) => str.trim().replace(/\s/g, '').substring(0, 200);

module('Integration | Component | recon-event-detail/single-email', function(hooks) {
  setupRenderingTest(hooks);

  test('renders single email content if data present', async function(assert) {
    this.set('email', EmberObject.create(emailData[1]));
    this.set('emailCount', 1);
    this.set('emailIndex', 1);
    await render(hbs`{{recon-event-detail/single-email emailIndex=emailIndex emailCount=emailCount email=email}}`);

    assert.ok(find('.recon-email-view'), 'single email view is rendered');
    assert.equal(findAll('.recon-email-collapse-header .rsa-icon-subtract-circle-1').length, 1, 'email is expanded by default');
    const str = find('.recon-email-header').textContent.concat(find('iframe').contentDocument.body.innerText);
    assert.equal(_first200(str), 'fromeddard.stark@verizon.nettosansa.stark@verizon.net,arya.stark@verizon.net,robb.stark@verizon.netbccjon.snow@verizon.netsubjectWinteriscoming.Didanyonepaytheplowguy?attachmentsAdditionalHeaderDetail');
  });

  test('total of emails text is displayed on email header', async function(assert) {
    this.set('email', EmberObject.create(emailData[1]));
    this.set('emailCount', 1);
    this.set('emailIndex', 1);

    await render(hbs`{{recon-event-detail/single-email emailIndex=emailIndex emailCount=emailCount email=email}}`);
    assert.equal(find('.recon-email-collapse-header .header-text').textContent.trim(), '1 of 1 messages');
  });

  test('secure email body is lazy-loaded and rendered', async function(assert) {
    const [, , , , , email] = emailData;
    email.bodyUrl = '/investigate/recon/serve/email/body/message-id';
    this.set('email', EmberObject.create(email));
    this.set('emailCount', 1);
    this.set('emailIndex', 1);

    await render(hbs`{{recon-event-detail/single-email emailIndex=emailIndex emailCount=emailCount email=email}}`);
    await waitUntil(() => {
      return !find('.rsa-loader');
    });
    assert.ok(find('iframe'), 'email body is rendered');
  });

  test('spinner is shown before secure email body is loaded', async function(assert) {
    const [, , , , , email] = emailData;
    email.bodyUrl = '/investigate/recon/serve/email/body/message-id-delayed';
    this.set('email', EmberObject.create(email));
    this.set('emailCount', 1);
    this.set('emailIndex', 1);

    render(hbs`{{recon-event-detail/single-email emailIndex=emailIndex emailCount=emailCount email=email}}`);
    await waitUntil(() => {
      return find('.rsa-loader');
    });
    assert.ok(find('.rsa-loader'), 'spinner is rendered');

    await waitUntil(() => {
      return !find('.rsa-loader');
    }, { timeout: 10000 });
    assert.ok(find('iframe'), 'email body is rendered eventually');
  });

  test('error is shown when email body is not fetched from the bodyUrl', async function(assert) {
    const [, , , , , email] = emailData;
    email.bodyUrl = '/investigate/recon/serve/email/body/message-id-invalid';
    this.set('email', EmberObject.create(email));
    this.set('emailCount', 1);
    this.set('emailIndex', 1);

    render(hbs`{{recon-event-detail/single-email emailIndex=emailIndex emailCount=emailCount email=email}}`);
    await waitUntil(() => {
      return !find('.rsa-loader');
    }, { timeout: 10000 });
    assert.ok(find('.email-lazyload-error'), 'email body retrieve error is shown');
  });
});
