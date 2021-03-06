import wait from 'ember-test-helpers/wait';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, click, waitUntil } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import EmberObject from '@ember/object';
import emailData from '../../../../../data/subscriptions/reconstruction-email-data/stream/data';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import _ from 'lodash';

const _first200 = (str) => str.trim().replace(/\s/g, '').substring(0, 200);

module('Integration | Component | recon-event-detail/single-email/email-body-content', function(hooks) {
  setupRenderingTest(hooks);

  test('renders single email body content, if response is not split and body content is less than 10K characters', async function(assert) {
    this.set('email', EmberObject.create(emailData[1]));
    await render(hbs`{{recon-event-detail/single-email/email-body-content email=email}}`);
    return wait().then(() => {
      assert.ok(find('.email-body-text'), 'show single email message content');
      assert.notOk(find('.email-show-remaining'), 'do not display show remaining button percentage');
      assert.notOk(find('.rendered-email-percent'), 'do not display rendered content percentage');
      assert.equal(_first200(find('iframe').contentDocument.body.innerText), 'emailmessagetext2...');
    });
  });

  test('renders single email body content, if response is split and body content is more than 10K characters', async function(assert) {
    const state = {
      recon: {
        emails: {
          emails: emailData,
          renderIds: ['6eea4274b86544628954f0926194068e9', '6eea4274b865446289540926194068e8']
        }
      }
    };
    patchReducer(this, Immutable.from(state));
    this.set('email', EmberObject.create(emailData[0]));
    await render(hbs`{{recon-event-detail/single-email/email-body-content email=email}}`);
    return wait().then(() => {
      assert.ok(find('.email-body-text'), 'show single email message content');
      assert.equal(find('.rendered-email-percent').textContent.trim(), 'Showing 75%', 'display rendered content with percentage');
      assert.equal(find('.email-show-remaining').textContent.trim(), 'Show Remaining 25%', 'display show remaining button with percentage');
      assert.equal(find('iframe').contentDocument.body.innerText.length, 10000, '10000 characters of email content has rendered');
    });
  });

  test('click on show more button should render the remaining characters less than 10K ', async function(assert) {
    const state = {
      recon: {
        emails: {
          emails: emailData,
          renderIds: ['6eea4274b86544628954f0926194068e9', '6eea4274b865446289540926194068e8']
        }
      }
    };
    patchReducer(this, Immutable.from(state));
    this.set('email', EmberObject.create(emailData[0]));
    await render(hbs`{{recon-event-detail/single-email/email-body-content email=email}}`);
    assert.ok(find('.email-body-text'), 'show single email message content');
    assert.equal(find('.rendered-email-percent').textContent.trim(), 'Showing 75%', 'display rendered content with percentage');
    assert.equal(find('.email-show-remaining').textContent.trim(), 'Show Remaining 25%', 'display show remaining button with percentage');
    await click('.email-show-remaining .rsa-form-button');
    return wait().then(() => {
      assert.equal(find('iframe').srcdoc.length, 13258, 'remaining characters of email content has rendered on show more');
      assert.notOk(find('.email-show-remaining'), 'do not display show remaining button percentage');
      assert.notOk(find('.rendered-email-percent'), 'do not display rendered content percentage');
    });
  });

  test('click on show more button should render the remaining characters more than 10K ', async function(assert) {
    const state = {
      recon: {
        emails: {
          emails: emailData,
          renderIds: ['6eea4274b86544628954f0926194068e9', '6eea4274b865446289540926194068e8']
        }
      }
    };
    patchReducer(this, Immutable.from(state));
    this.set('email', EmberObject.create(emailData[3]));
    await render(hbs`{{recon-event-detail/single-email/email-body-content email=email}}`);
    assert.equal(find('.rendered-email-percent').textContent.trim(), 'Showing 31%', 'display rendered content with percentage');
    assert.equal(find('.email-show-remaining').textContent.trim(), 'Show Remaining 69%', 'display show remaining button with percentage');
    await click('.email-show-remaining .rsa-form-button');
    return wait().then(() => {
      assert.equal(find('iframe').srcdoc.length, 20000, '20000 remaining characters of email content has rendered on show more');
      assert.equal(find('.rendered-email-percent').textContent.trim(), 'Showing 64%', 'display rendered content percentage changed after click');
      assert.equal(find('.email-show-remaining').textContent.trim(), 'Show Remaining 36%', 'display show remaining button percentage changed after click');

    });
  });

  test('renders single email html body content, if data present', async function(assert) {
    this.set('email', EmberObject.create(emailData[2]));
    await render(hbs`{{recon-event-detail/single-email/email-body-content email=email}}`);
    assert.ok(find('.email-body-text'), 'show single email message content');
    await waitUntil(() => {
      return !!find('iframe').contentDocument.body;
    });
    assert.equal(_first200(find('iframe').contentDocument.body.innerText), 'googleemailmessagetextcontentgoogle2');
  });

  test('renders well formatted plain text email body content', async function(assert) {
    const [, , , email] = emailData;
    const emailCopy = _.cloneDeep(email);
    emailCopy.bodyContent = 'first line\\nsecond line\\nthird line';
    this.set('email', EmberObject.create(emailCopy));
    await render(hbs`{{recon-event-detail/single-email/email-body-content email=email}}`);
    assert.equal(find('iframe').contentDocument.body.innerText, emailCopy.bodyContent, 'Plain text email body must be well formatted');
  });

  test('adds hrefs to hyperlinks in the body', async function(assert) {
    this.set('email', EmberObject.create(emailData[2]));
    await render(hbs`{{recon-event-detail/single-email/email-body-content email=email}}`);
    const renderedBody = find('iframe').contentDocument.body;
    const hyperlinks = renderedBody.querySelectorAll('a');
    assert.ok(hyperlinks.length > 0, 'hyperlinks in the body are rendered');
    hyperlinks.forEach((a) => {
      assert.equal(a.href, 'javascript:void(0);', 'dummy href added to the hyperlink');
    });
  });

  test('clicking on hyperlink shows original url in a popup', async function(assert) {
    this.set('email', EmberObject.create(emailData[2]));
    await render(hbs`<div id='modalDestination'></div>{{recon-event-detail/single-email/email-body-content email=email}}`);
    const renderedBody = find('iframe').contentDocument.body;
    const hyperlinks = renderedBody.querySelectorAll('a');
    assert.equal(hyperlinks.length, 2, 'hyperlinks in the body are rendered');

    await click(hyperlinks[0]);
    assert.ok(find('.email-recon-link-info-dialog'), 'link popup is shown');
    assert.equal(find('.email-recon-link-info-dialog .original-link').innerText.trim(), 'http://www.google.com', 'original link included in the popup');

    await click(hyperlinks[1]);
    assert.ok(find('.email-recon-link-info-dialog'), 'link popup is shown');
    assert.equal(find('.email-recon-link-info-dialog .original-link').innerText.trim(), 'http://www.google2.com', 'original link included in the popup');
  });
});
