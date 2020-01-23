import { module, test, skip } from 'qunit';
import { render, findAll, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import EmberObject from '@ember/object';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { setupRenderingTest } from 'ember-qunit';
import DataHelper from '../../../helpers/data-helper';


module('Integration | Component | recon event content', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    this.set('accessControl', EmberObject.create({}));
    this.set('accessControl.hasReconAccess', true);
    this.set('redux', this.owner.lookup('service:redux'));
    initialize(this.owner);
  });

  test('it renders child view', async function(assert) {
    new DataHelper(this.get('redux'))
      .initializeData()
      .setViewToText()
      .populateTexts();
    await render(hbs`{{recon-event-content accessControl=accessControl}}`);
    assert.equal(findAll('.recon-event-detail-text').length, 1, 'Child view missing');
  });

  test('it renders content error', async function(assert) {
    new DataHelper(this.get('redux'))
      .setViewToText()
      .contentRetrieveFailure(129);
    await render(hbs`{{recon-event-content accessControl=accessControl}}`);

    assert.equal(findAll('.rsa-panel-message').length, 1, 'Content error not set');
    assert.equal(findAll('.recon-pager').length, 1, 'Pager not rendered');
  });

  test('content error is not shown when there is data for a view', async function(assert) {
    new DataHelper(this.get('redux'))
      .initializeData()
      .populateFiles()
      .setViewToFile()
      .contentRetrieveFailure(129);
    await render(hbs`{{recon-event-content accessControl=accessControl}}`);

    assert.notOk(find('.rsa-panel-message'), 'Content error should not be shown when data is available');
  });

  test('it renders spinner', async function(assert) {
    new DataHelper(this.get('redux'))
      .setViewToText()
      .contentRetrieveStarted();
    await render(hbs`{{recon-event-content accessControl=accessControl}}`);

    assert.equal(findAll('.recon-loader').length, 1, 'Spinner missing');
  });

  test('log events redirect to text view', async function(assert) {
    new DataHelper(this.get('redux'))
      .initializeData({ eventId: 1, endpointId: 2, meta: [['medium', 32]] })
      .setViewToText()
      .populateTexts();

    await render(hbs`{{recon-event-content accessControl=accessControl}}`);
    assert.equal(findAll('.recon-event-detail-text').length, 1, 'On the Text View');
    assert.equal(findAll('.recon-event-detail-packets').length, 0, 'Not on the Packet View');
    assert.equal(findAll('.recon-event-detail-files').length, 0, 'Not on the File View');
  });

  test('endpoint events redirect to empty view', async function(assert) {
    new DataHelper(this.get('redux'))
      .initializeData({ eventId: 2, endpointId: 2, meta: [['nwe.callback_id', 'foo']] });

    await render(hbs`{{recon-event-content accessControl=accessControl}}`);
    assert.equal(findAll('.recon-event-detail-text').length, 0, 'Not On the Text View');
    assert.equal(findAll('.recon-event-detail-packets').length, 0, 'Not on the Packet View');
    assert.equal(findAll('.recon-event-detail-files').length, 0, 'Not on the File View');
  });

  test('displays correct error when missing permissions', async function(assert) {
    this.set('accessControl.hasReconAccess', false);

    new DataHelper(this.get('redux'))
      .initializeData({ eventId: 1, endpointId: 2, meta: [['medium', 32]] })
      .setViewToText();

    await render(hbs`{{recon-event-content accessControl=accessControl}}`);
    assert.equal(find('.rsa-panel-message .message').textContent.trim(), 'You do not have the required permissions to view this content.', 'Error is displayed with correct message');
  });

  test('it renders appropriate message when there is a content error on text view', async function(assert) {
    new DataHelper(this.get('redux'))
      .setViewToText()
      .contentRetrieveFailure(1);
    await render(hbs`{{recon-event-content accessControl=accessControl}}`);

    assert.equal(findAll('.rsa-panel-message').length, 1, 'Content error not set');
    const translation = this.owner.lookup('service:i18n');

    const noContentString = find('.rsa-panel-message').textContent.trim();
    assert.equal(noContentString, translation.t('recon.error.noTextContentData').trim(), 'Did not find no content message panel');
  });

  // skipped for 11.4 release since email reconstruction is turned off for 11.4, unskip this when email recon will turned back on
  skip('displays correct error when reconstruction error occurs', async function(assert) {
    new DataHelper(this.get('redux'))
      .initializeData({ eventId: 1, endpointId: 2, meta: [['service', 80]] })
      .setViewToEmail()
      .contentRetrieveFailure(65536);

    await render(hbs`{{recon-event-content accessControl=accessControl}}`);
    assert.ok(find('.rsa-panel-message .message'), 'reconstruction error message panel is shown');
    assert.equal(find('.rsa-panel-message .message').textContent.trim(), 'No Email reconstruction available for this event.', 'Correct error message is shown for reconstruction error');
  });

  // skipped for 11.4 release since email reconstruction is turned off for 11.4, unskip this when email recon will turned back on
  skip('test redirect to classic web recon view for web mail', async function(assert) {
    new DataHelper(this.get('redux'))
      .initializeData({ eventId: 1, endpointId: 2, meta: [['service', 80], ['alias.host', 'web.mail.google.com']] })
      .setViewToEmail()
      .contentRetrieveFailure(65536);

    await render(hbs`{{recon-event-content accessControl=accessControl}}`);

    assert.ok(find('.rsa-panel-message .message'), 'classic redirection message panel is shown');
    assert.equal(find('.rsa-panel-message .message').textContent.trim(), 'This session has a web email. View the reconstruction of this session here', 'Classic Web email redirection message is shown');
    assert.equal(find('.rsa-panel-message .message a').pathname, '/investigation/2/navigate/event/WEB/1', 'Classic redirect URL is correct');
  });
});
