import { setupRenderingTest } from 'ember-qunit';
import { module, test } from 'qunit';
import { patchReducer } from '../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import emailData from '../../../../data/subscriptions/reconstruction-email-data/stream/data';
import { render, find, findAll, click } from '@ember/test-helpers';
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
    assert.equal(findAll('.recon-email-view').length, 4, '4 emails are shown');
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


  test('renders attachment warning if attachment is selected', async function(assert) {
    const state = {
      recon: {
        files: {
          selectedFileIds: ['1'],
          files: [
            {
              id: '1',
              fileName: 'attachment1.png'
            },
            {
              id: '2',
              fileName: 'attachment2.png'
            }
          ]
        },
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

  test('Expand/Collapse emails on click of expand/collapse icon', async function(assert) {
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

    assert.equal(findAll('.rsa-icon-add-circle-1-filled').length, 4, '4 emails are collapse by default');

    await click(findAll('.rsa-icon-add-circle-1-filled')[0]);
    assert.equal(findAll('.recon-email-collapse-header .rsa-icon-subtract-circle-1-filled').length, 1, 'First email is expanded');
    assert.equal(findAll('.recon-email-collapse-header .rsa-icon-add-circle-1-filled').length, 3, '3 emails are collapsed now after 1 email is expanded');

    await click(findAll('.rsa-icon-subtract-circle-1-filled')[0]);
    assert.equal(findAll('.rsa-icon-add-circle-1-filled').length, 4, '4 emails are collapse again');

    const strValue = find('.recon-email-header').textContent.trim().replace(/\s/g, '').substring(0, 200);
    assert.equal(strValue, 'fromeddard.stark@verizon.nettosansa.stark@verizon.net,arya.stark@verizon.net,robb.stark@verizon.netsubjectWinteriscoming.Didanyonepaytheplowguy?');
  });
});
