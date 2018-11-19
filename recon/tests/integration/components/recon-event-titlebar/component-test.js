import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { patchReducer } from '../../../helpers/vnext-patch';
import hbs from 'htmlbars-inline-precompile';
import { clickTrigger, selectChoose } from 'ember-power-select/test-support/helpers';
import { click, find, findAll, render } from '@ember/test-helpers';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import sinon from 'sinon';
import * as VisualCreators from 'recon/actions/visual-creators';
import * as DataCreators from 'recon/actions/data-creators';

const visualCreatorsStub = sinon.stub(VisualCreators, 'closeRecon');
const dataCreatorsStub = sinon.stub(DataCreators, 'setNewReconView');

let setState;

module('Integration | Component | recon event titlebar', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
  });

  // recon view

  test('renders log event label and no view selection for log events', async function(assert) {
    new ReduxDataHelper(setState).isLogEvent().build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.equal(find('.event-title').textContent.trim(), 'Log Event Details', 'Title is correct text');
    assert.notOk(find('.ember-power-select-trigger'), 'there is not a power select rendered');
  });

  test('renders endpoint event label and no view selection for endpoint events', async function(assert) {
    new ReduxDataHelper(setState).isEndpointEvent().build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.equal(find('.event-title').textContent.trim(), 'Endpoint Event Details', 'Title is correct text');
    assert.equal(find('.tview-heading').textContent.trim(), 'Text Analysis', 'Text Analysis availabel for Endpoint Events');
    assert.notOk(find('.ember-power-select-trigger'), 'there is not a power select rendered');
  });

  test('renders network event label and view selection for network events', async function(assert) {
    new ReduxDataHelper(setState).isPacketView().isNetworkEvent().build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.equal(find('.event-title').textContent.trim(), 'Network Event Details', 'Title is correct text');
    assert.equal(find('.ember-power-select-trigger').textContent.trim(), 'Packet Analysis', 'power select is populated with correct default');
  });

  test('when reconstruction view is action is called', async function(assert) {
    new ReduxDataHelper(setState).isTextView().build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.equal(find('.ember-power-select-trigger').textContent.trim(), 'Text Analysis', 'power select is populated with correct default');
    await selectChoose('.heading-select', 'File');
    assert.equal(dataCreatorsStub.calledOnce, true, 'action is called');
    assert.equal(dataCreatorsStub.args[0][0].name, 'FILE', 'right recon view is provided');
    dataCreatorsStub.reset();

    await selectChoose('.heading-select', 'Packet');
    assert.equal(dataCreatorsStub.calledOnce, true, 'action is called');
    assert.equal(dataCreatorsStub.args[0][0].name, 'PACKET', 'right recon view is provided');
    dataCreatorsStub.restore();
  });

  test('all views enabled for network sessions', async function(assert) {
    new ReduxDataHelper(setState).isPacketView().isNetworkEvent().build();
    await render(hbs`{{recon-event-titlebar}}`);
    await clickTrigger();
    assert.equal(findAll('.ember-power-select-option').length, 4, 'File, Text, Email, and Web View');
  });

  test('Show all tabs for network event', async function(assert) {
    new ReduxDataHelper(setState).isPacketView().isNetworkEvent().build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.equal(findAll('.rsa-nav-tab').length, 5, 'show 5 analysis tabs');
    assert.equal(find('.rsa-nav-tab.is-active').textContent.trim(), 'Packet Analysis');
  });

  test('Show only text analysis label for log event', async function(assert) {
    new ReduxDataHelper(setState).isTextView().isLogEvent().build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.notOk(find('.rsa-nav-tab'));
    assert.equal(find('.tview-label').textContent.trim(), 'Text Analysis');
  });

  test('Click on Email tab does not change the tab', async function(assert) {
    new ReduxDataHelper(setState).isTextView().build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.equal(find('.rsa-nav-tab.is-active').textContent.trim(), 'Text Analysis');
    await selectChoose('.heading-select', 'Email');
    assert.equal(find('.rsa-nav-tab.is-active').textContent.trim(), 'Text Analysis');
  });

  test('Click on Web tab opens the web meta in a new tab and does not change the current tab', async function(assert) {
    new ReduxDataHelper(setState).isPacketView().build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.equal(find('.rsa-nav-tab.is-active').textContent.trim(), 'Packet Analysis');
    await selectChoose('.heading-select', 'Web');
    assert.equal(find('.rsa-nav-tab.is-active').textContent.trim(), 'Packet Analysis');
  });


  // header toggle

  test('header toggle renders properly when active', async function(assert) {
    new ReduxDataHelper(setState).isTextView().isHeaderOpen(true).build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.ok(find('.rsa-icon-layout-6-filled'), 'icon is displayed');
    assert.ok(find('.rsa-icon-layout-6-filled.active'), 'icon is active');
  });

  test('header toggle renders properly when inactive', async function(assert) {
    new ReduxDataHelper(setState).isTextView().isHeaderOpen(false).build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.ok(find('.rsa-icon-layout-6-filled'), 'icon is displayed');
    assert.notOk(find('.rsa-icon-layout-6-filled.active'), 'icon is not active');
  });

  test('clicking header toggles header icon being active', async function(assert) {
    new ReduxDataHelper(setState).isTextView().isHeaderOpen(true).build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.ok(find('.rsa-icon-layout-6-filled.active'), 'icon is active to stat');
    await click('.rsa-icon-layout-6-filled');
    assert.notOk(find('.rsa-icon-layout-6-filled.active'), 'icon toggles to inactive after toggle');
  });

  // TODO, move this to a integration/unit test on the action creator, just call the function with a new settting
  // and verify the setting gets sent
  test('isHeaderOpen toggle should result in call to setPreferences with new setting for flag', async function(assert) {
    assert.expect(4);
    new ReduxDataHelper(setState).isTextView().isHeaderOpen(true).build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.ok(find('.rsa-icon-layout-6-filled'), 'icon is displayed');
    assert.ok(find('.rsa-icon-layout-6-filled.active'), 'icon is active');
    await click('.rsa-icon-layout-6-filled.active');
    assert.ok(find('.rsa-icon-layout-6-filled'), 'icon is displayed');
    assert.notOk(find('.rsa-icon-layout-6-filled.active'), 'icon is not active');
  });

  // request/response toggle

  test('log events do not render request/response toggles', async function(assert) {
    new ReduxDataHelper(setState).isTextView().isLogEvent().build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.notOk(find('.toggle-request'), 'request toggle should be missing');
    assert.notOk(find('.toggle-response'), 'response toggle should be missing');
  });

  test('endpoint events do not render request/response toggles', async function(assert) {
    new ReduxDataHelper(setState).isTextView().isEndpointEvent().build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.notOk(find('.toggle-request'), 'request toggle should be missing');
    assert.notOk(find('.toggle-response'), 'response toggle should be missing');
  });

  test('network events render request/response button', async function(assert) {
    new ReduxDataHelper(setState).isTextView().isNetworkEvent().build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.ok(find('.toggle-request'), 'request toggle should be present');
    assert.ok(find('.toggle-response'), 'response toggle should be present');
  });

  // request toggle

  test('request toggle renders properly when active', async function(assert) {
    new ReduxDataHelper(setState).isTextView().isRequestShown(true).build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.ok(find('.toggle-request'), 'icon is displayed');
    assert.ok(find('.toggle-request.active'), 'icon is active');
  });

  test('request toggle renders properly when inactive', async function(assert) {
    new ReduxDataHelper(setState).isTextView().isRequestShown(false).build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.ok(find('.toggle-request'), 'icon is displayed');
    assert.notOk(find('.toggle-request.active'), 'icon is not active');
  });

  test('clicking toggle request toggles icon to inactive', async function(assert) {
    new ReduxDataHelper(setState).isTextView().isRequestShown(true).build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.ok(find('.toggle-request.active'), 'icon is active to start');
    await click('.toggle-request');
    assert.notOk(find('.toggle-request.active'), 'icon flips to inactive after toggle');
  });

  // response toggle

  test('response toggle renders properly when active', async function(assert) {
    new ReduxDataHelper(setState).isTextView().isResponseShown(true).build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.ok(find('.toggle-response'), 'icon is displayed');
    assert.ok(find('.toggle-response.active'), 'icon is active');
  });

  test('response toggle renders properly when inactive', async function(assert) {
    new ReduxDataHelper(setState).isTextView().isResponseShown(false).build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.ok(find('.toggle-response'), 'icon is displayed');
    assert.notOk(find('.toggle-response.active'), 'icon is not active');
  });

  test('clicking toggle response toggles icon to inactive', async function(assert) {
    new ReduxDataHelper(setState).isTextView().isResponseShown(true).build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.ok(find('.toggle-response.active'), 'icon is active to start');
    await click('.toggle-response');
    assert.notOk(find('.toggle-response.active'), 'icon flips to inactive after toggle');
  });

  // meta toggle

  test('meta toggle renders properly when active', async function(assert) {
    new ReduxDataHelper(setState).isTextView().isMetaShown(true).build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.ok(find('.toggle-meta'), 'icon is displayed');
    assert.ok(find('.toggle-meta.active'), 'icon is active');
  });

  test('meta toggle renders properly when inactive', async function(assert) {
    new ReduxDataHelper(setState).isTextView().isMetaShown(false).build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.ok(find('.toggle-meta'), 'icon is displayed');
    assert.notOk(find('.toggle-meta.active'), 'icon is not active');
  });

  test('clicking meta toggle toggles icon to inactive', async function(assert) {
    new ReduxDataHelper(setState).isTextView().isMetaShown(true).build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.ok(find('.toggle-meta.active'), 'icon is active to start');
    await click('.toggle-meta');
    assert.notOk(find('.toggle-meta.active'), 'icon flips to inactive after toggle');
  });

  // standalone mode

  test('shrink and close icons do not show in standalone mode', async function(assert) {
    new ReduxDataHelper(setState).isFileView().isStandalone(true).build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.notOk(find('.rsa-icon-shrink-diagonal-2-filled'), 'icon is not visible');
    assert.notOk(find('.rsa-icon-expand-diagonal-4-filled'), 'icon is not visible');
    assert.notOk(find('.rsa-icon-close-filled'), 'icon is not visible');
  });

  test('shrink and close icons show when not in standalone mode', async function(assert) {
    new ReduxDataHelper(setState)
      .isFileView()
      .isStandalone(false)
      .isReconExpanded(true)
      .isReconOpen(true)
      .build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.ok(find('.rsa-icon-shrink-diagonal-2-filled'), 'icon is visible');
    assert.ok(find('.rsa-icon-close-filled'), 'icon is visible');
  });

  // shrink/expand button

  test('shrink toggle renders proper expanded state', async function(assert) {
    new ReduxDataHelper(setState).isFileView().isReconExpanded(true).build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.ok(find('.rsa-icon-shrink-diagonal-2-filled'), 'icon is visible');
    assert.notOk(find('.rsa-icon-expand-diagonal-4-filled'), 'icon is not visible');
  });

  test('shrink toggle renders proper shrunk state', async function(assert) {
    new ReduxDataHelper(setState).isFileView().isReconExpanded(false).build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.notOk(find('.rsa-icon-shrink-diagonal-2-filled'), 'icon is not visible');
    assert.ok(find('.rsa-icon-expand-diagonal-4-filled'), 'icon is visible');
  });

  test('clicking shrink executes action', async function(assert) {
    new ReduxDataHelper(setState).isFileView().isReconExpanded(true).build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.ok(find('.rsa-icon-shrink-diagonal-2-filled'), 'icon is visible');
    await click('.rsa-icon-shrink-diagonal-2-filled');
    assert.notOk(find('.rsa-icon-shrink-diagonal-2-filled'), 'icon is not visible');
    assert.ok(find('.rsa-icon-expand-diagonal-4-filled'), 'icon is visible');
  });

  // close button

  test('clicking close executes action', async function(assert) {
    new ReduxDataHelper(setState).isTextView().isReconOpen(true).build();
    await render(hbs`{{recon-event-titlebar}}`);
    await click('.rsa-icon-close-filled');
    assert.equal(visualCreatorsStub.callCount, 1, 'close recon action creator called one time');
    visualCreatorsStub.restore();
  });
});
