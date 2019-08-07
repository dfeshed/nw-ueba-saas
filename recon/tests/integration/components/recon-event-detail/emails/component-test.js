import { setupRenderingTest } from 'ember-qunit';
import { module, test } from 'qunit';
import { patchReducer } from '../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import emailData from '../../../../data/subscriptions/reconstruction-email-data/stream/data';
import { render, find, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | recon-event-detail/emails', function(hooks) {
  setupRenderingTest(hooks);

  test('renders emails if data present', async function(assert) {
    const state = {
      recon: {
        emails: {
          emails: emailData,
          renderIds: ['6eea4274b86544628954f0926194068e9', '6eea4274b865446289540926194068e8']
        }
      }
    };
    patchReducer(this, Immutable.from(state));
    await render(hbs`{{recon-event-detail/emails}}`);
    assert.equal(findAll('.rsa-panel-message').length, 0, 'do not show error message when email data is present');
    const str = find('.email-scroll-box').textContent.trim().replace(/\s/g, '').substring(0, 300);
    assert.equal(str, 'fromeddard.stark@verizon.nettosansa.stark@verizon.net,arya.stark@verizon.net,robb.stark@verizon.netbccjon.snow@verizon.netsubjectWinteriscoming.Didanyonepaytheplowguy?attachmentsthewindsofwinter.docx,windsofwinter.docx,ResumeforS.Lindsay.pdfAdditionalHeaderDetailsemailmessagetext1...fromeddard.stark');
  });

  test('renders error when no email data present', async function(assert) {
    const state = {
      recon: {
        emails: {
          emails: []
        }
      }
    };
    patchReducer(this, Immutable.from(state));
    await render(hbs`{{recon-event-detail/emails}}`);
    assert.equal(findAll('.rsa-panel-message').length, 1, 'show error message when no email data');
    assert.notOk(find('.attachment-download-warning'), 'attachment warning is not rendered');
  });


  test('renders warning if attachment present', async function(assert) {
    const state = {
      recon: {
        emails: {
          emails: emailData,
          renderIds: ['6eea4274b865446289540926194068e9', '6eea4274b865446289540926194068e8']
        }
      }
    };
    patchReducer(this, Immutable.from(state));
    await render(hbs`{{recon-event-detail/emails}}`);
    assert.ok(find('.attachment-download-warning'), 'attachment warning is rendered');
  });
});
