import wait from 'ember-test-helpers/wait';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import EmberObject from '@ember/object';
import emailData from '../../../../../data/subscriptions/reconstruction-email-data/stream/data';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';

const _first200 = (str) => str.trim().replace(/\s/g, '').substring(0, 200);

module('Integration | Component | recon-event-detail/single-email/email-body-content', function(hooks) {
  setupRenderingTest(hooks);

  test('renders single email body content, if response is not splitted and body content is less then 10K characters', async function(assert) {
    this.set('email', EmberObject.create(emailData[1]));
    await render(hbs`{{recon-event-detail/single-email/email-body-content email=email renderedAll=true}}`);
    return wait().then(() => {
      assert.ok(find('.email-body-text'), 'show single email message content');
      assert.notOk(find('.email-show-more'), 'do not display show more button');
      assert.equal(_first200(find('iframe').contentDocument.body.innerText), 'emailmessagetext2...');
    });
  });

  test('renders single email body content, if response is splitted and body content is more then 10K characters', async function(assert) {
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
    await render(hbs`{{recon-event-detail/single-email/email-body-content email=email renderedAll=false}}`);
    return wait().then(() => {
      assert.ok(find('.email-body-text'), 'show single email message content');
      assert.ok(find('.email-show-more'), 'display show more button');
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
    await render(hbs`{{recon-event-detail/single-email/email-body-content email=email renderedAll=false}}`);
    assert.ok(find('.email-body-text'), 'show single email message content');
    assert.ok(find('.email-show-more'), 'display show more button');
    await click('.email-show-more .rsa-form-button');
    return wait().then(() => {
      assert.equal(find('iframe').srcdoc.length, 13258, 'remaining characters of email content has rendered on show more');
      assert.notOk(find('.email-show-more'), 'do not display show more button');
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
    await render(hbs`{{recon-event-detail/single-email/email-body-content email=email renderedAll=false}}`);
    assert.ok(find('.email-body-text'), 'show single email message content');
    assert.ok(find('.email-show-more'), 'display show more button');
    await click('.email-show-more .rsa-form-button');
    return wait().then(() => {
      assert.equal(find('iframe').srcdoc.length, 20000, '20000 remaining characters of email content has rendered on show more');
      assert.ok(find('.email-show-more'), 'display show more button');
    });
  });

  test('renders single email html body content, if data present', async function(assert) {
    this.set('email', EmberObject.create(emailData[2]));
    await render(hbs`{{recon-event-detail/single-email/email-body-content email=email renderedAll=true}}`);
    assert.ok(find('.email-body-text'), 'show single email message content');
    assert.equal(_first200(find('iframe').contentDocument.body.innerText), 'googleemailmessagetextcontent');
  });
});


