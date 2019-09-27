import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, find, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import EmberObject from '@ember/object';
import Immutable from 'seamless-immutable';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import emailData from '../../../../../data/subscriptions/reconstruction-email-data/stream/data';
import files from '../../../../../data/subscriptions/reconstruction-file-data/query/data';

module('Integration | Component | recon-event-detail/single-email/email-header', function(hooks) {
  setupRenderingTest(hooks);

  test('renders single email header content, if data present', async function(assert) {
    this.set('email', EmberObject.create(emailData[1]));
    await render(hbs`{{recon-event-detail/single-email/email-header email=email}}`);
    assert.equal(findAll('.recon-email-header').length, 1);
    assert.equal(findAll('.rsa-icon-arrow-right-12-filled').length, 2, 'Attachments and Additional Header sections rendered as collapsed');
    const str = find('.recon-email-header').textContent.trim().replace(/\s/g, '').substring(0, 200);
    assert.equal(str, 'fromeddard.stark@verizon.nettosansa.stark@verizon.net,arya.stark@verizon.net,robb.stark@verizon.netbccjon.snow@verizon.netsubjectWinteriscoming.Didanyonepaytheplowguy?attachmentsAdditionalHeaderDetail');
  });

  test('Expand/Collapse all additional headers on click of additional header details', async function(assert) {
    this.set('email', EmberObject.create(emailData[1]));
    await render(hbs`{{recon-event-detail/single-email/email-header email=email}}`);
    await click(findAll('.rsa-icon-arrow-right-12-filled')[1]);
    assert.equal(findAll('.rsa-icon-arrow-down-12-filled').length, 1, 'Additional Header is expanded');
    const str = find('.recon-email-header').textContent.trim().replace(/\s/g, '').substring(0, 200);
    assert.equal(str, 'fromeddard.stark@verizon.nettosansa.stark@verizon.net,arya.stark@verizon.net,robb.stark@verizon.netbccjon.snow@verizon.netsubjectWinteriscoming.Didanyonepaytheplowguy?attachmentsAdditionalHeaderDetail');
    await click(findAll('.rsa-icon-arrow-down-12-filled')[0]);
    assert.equal(findAll('.rsa-icon-arrow-right-12-filled').length, 2, 'Additional Header is collapsed again');
    const strValue = find('.recon-email-header').textContent.trim().replace(/\s/g, '').substring(0, 200);
    assert.equal(strValue, 'fromeddard.stark@verizon.nettosansa.stark@verizon.net,arya.stark@verizon.net,robb.stark@verizon.netbccjon.snow@verizon.netsubjectWinteriscoming.Didanyonepaytheplowguy?attachmentsAdditionalHeaderDetail');
  });

  test('Expand/Collapse attachments section', async function(assert) {
    this.set('email', EmberObject.create(emailData[1]));
    const state = {
      recon: {
        files: {
          files,
          selectedFileIds: []
        }
      }
    };
    patchReducer(this, Immutable.from(state));
    await render(hbs`{{recon-event-detail/single-email/email-header email=email}}`);
    assert.equal(findAll('.attachments .rsa-icon-arrow-right-12-filled').length, 1, 'Attachments section is collapsed by default');
    await click(findAll('.attachments .rsa-icon-arrow-right-12-filled')[0]);
    assert.equal(findAll('.attachments .rsa-icon-arrow-down-12-filled').length, 1, 'Attachments section is expanded');
    assert.equal(findAll('.rsa-form-checkbox-label').length, 3, 'Attachments must be listed as check boxes');
    const attachments = findAll('.attachment-name');
    assert.equal(attachments[0].textContent.trim(), 'All Attachments', 'First item must be All Attachments');
    assert.equal(attachments[1].textContent.trim(), 'thewindsofwinter.docx');
    assert.equal(attachments[2].textContent.trim(), 'windsofwinter.docx');
  });

  test('Checking All attachments selects all attachments', async function(assert) {
    this.set('email', EmberObject.create(emailData[1]));
    const state = {
      recon: {
        files: {
          files,
          selectedFileIds: []
        }
      }
    };
    patchReducer(this, Immutable.from(state));
    await render(hbs`{{recon-event-detail/single-email/email-header email=email}}`);
    await click(findAll('.attachments .rsa-icon-arrow-right-12-filled')[0]);
    assert.equal(findAll('.rsa-form-checkbox-label .checked').length, 0, 'No attachments selected to start with');
    await click(find('.rsa-form-checkbox-label'));
    assert.equal(findAll('.rsa-form-checkbox-label .checked').length, 3, 'All the attachments selected');
    await click(find('.rsa-form-checkbox-label'));
    assert.equal(findAll('.rsa-form-checkbox-label .checked').length, 0, 'All the attachments unselected');
  });
});
