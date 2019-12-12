import { module, test, skip } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { patchReducer } from '../../../helpers/vnext-patch';
import hbs from 'htmlbars-inline-precompile';
import { clickTrigger, selectChoose } from 'ember-power-select/test-support/helpers';
import { click, find, findAll, render } from '@ember/test-helpers';
import ReduxDataHelper from '../../../helpers/redux-data-helper';

let setState;

module('Integration | Component | recon event titlebar', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
  });

  // recon view

  test('Display correct title and buttons in the titlebar if eventType is set to "LOG"', async function(assert) {
    new ReduxDataHelper(setState).eventType('LOG').build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.equal(find('.event-title').textContent.trim(), 'Log Event Details', 'title was incorrect');
    assert.notOk(find('.ember-power-select-trigger'), 'a power select to change the view should not be rendered');
    assert.ok(findAll('.rsa-icon').length, 'missing icons');
  });

  test('Display correct title and buttons in the titlebar if eventType is set to "ENDPOINT"', async function(assert) {
    new ReduxDataHelper(setState).eventType('ENDPOINT').build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.equal(find('.event-title').textContent.trim(), 'Endpoint Event Details', 'title was incorrect');
    assert.notOk(find('.ember-power-select-trigger'), 'a power select to change the view should not be rendered');
    assert.ok(findAll('.rsa-icon').length, 'missing icons');
  });

  test('Display correct title and buttons in the titlebar if eventType is set to "NETWORK"', async function(assert) {
    new ReduxDataHelper(setState).eventType('NETWORK').build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.equal(find('.event-title').textContent.trim(), 'Network Event Details', 'title was incorrect');
    assert.ok(findAll('.ember-power-select-trigger').length, 'a power select to change the view was not rendered');
    assert.ok(findAll('.rsa-icon').length, 'missing icons');
  });

  test('Display titlebar buttons once meta has been loaded so that we dont have to show/hide again n again', async function(assert) {
    new ReduxDataHelper(setState).meta([['foo', 'bar'], ['fooz', 'ball']]).isNetworkEvent().isPacketView().build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.equal(find('.event-title').textContent.trim(), 'Network Event Details', 'Title is correct text');
    assert.equal(findAll('.ember-power-select-trigger').length, 1, 'Contains the trigger with default value');
  });

  test('renders log event label and no view selection for log events', async function(assert) {
    new ReduxDataHelper(setState).meta([['foo', 'bar'], ['fooz', 'ball']]).isLogEvent().build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.equal(find('.event-title').textContent.trim(), 'Log Event Details', 'Title is correct text');
    assert.notOk(find('.ember-power-select-trigger'), 'there is not a power select rendered');
  });

  test('renders endpoint event label and no view selection for endpoint events', async function(assert) {
    new ReduxDataHelper(setState).meta([['foo', 'bar'], ['fooz', 'ball']]).isEndpointEvent().build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.equal(find('.event-title').textContent.trim(), 'Endpoint Event Details', 'Title is correct text');
    assert.equal(find('.tview-heading').textContent.trim(), 'Text', 'Text Analysis available for Endpoint Events');
    assert.notOk(find('.ember-power-select-trigger'), 'there is not a power select rendered');
  });

  test('renders network event label and view selection for network events', async function(assert) {
    new ReduxDataHelper(setState).meta([['foo', 'bar'], ['fooz', 'ball']]).isPacketView().isNetworkEvent().build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.equal(find('.event-title').textContent.trim(), 'Network Event Details', 'Title is correct text');
    assert.equal(find('.ember-power-select-trigger').textContent.trim(), 'Packet', 'power select is populated with correct default');
  });

  test('when reconstruction view is action is called', async function(assert) {
    new ReduxDataHelper(setState).meta([['foo', 'bar'], ['fooz', 'ball']]).isTextView().isNetworkEvent().build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.equal(find('.ember-power-select-trigger').textContent.trim(), 'Text', 'power select is populated with correct default');
    await selectChoose('.heading-select .ember-power-select-trigger', 'File');

    let redux = this.owner.lookup('service:redux');
    assert.equal(redux.getState().recon.visuals.currentReconView.name, 'FILE', 'Current recon view is File');

    await selectChoose('.heading-select .ember-power-select-trigger', 'Packet');
    redux = this.owner.lookup('service:redux');
    assert.equal(redux.getState().recon.visuals.currentReconView.name, 'PACKET', 'Current recon view is Packet');
  });

  test('all views enabled for network sessions', async function(assert) {
    new ReduxDataHelper(setState).meta([['foo', 'bar'], ['fooz', 'ball']]).isPacketView().isNetworkEvent().build();
    await render(hbs`{{recon-event-titlebar}}`);
    await clickTrigger();
    assert.equal(findAll('.ember-power-select-option').length, 4, 'File, Text, Email and Web View');
  });

  test('Show all tabs for network event', async function(assert) {
    new ReduxDataHelper(setState).meta([['foo', 'bar'], ['fooz', 'ball']]).isPacketView().isNetworkEvent().build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.equal(findAll('.rsa-nav-tab').length, 5, 'show 5 analysis tabs');
    assert.equal(find('.rsa-nav-tab.is-active').textContent.trim(), 'Packet');
  });

  test('Show only text analysis label for log event', async function(assert) {
    new ReduxDataHelper(setState).meta([['foo', 'bar'], ['fooz', 'ball']]).isTextView().isLogEvent().build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.notOk(find('.rsa-nav-tab'));
    assert.equal(find('.tview-label').textContent.trim(), 'Text');
  });

  // skipped for 11.4 release since email reconstruction is turned off for 11.4, unskip this when email recon will turned back on
  skip('Click on Email tab changes the tab', async function(assert) {
    new ReduxDataHelper(setState).meta([['foo', 'bar'], ['fooz', 'ball']]).isTextView().build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.equal(find('.rsa-nav-tab.is-active').textContent.trim(), 'Text');
    await selectChoose('.heading-select', 'Email');
    assert.equal(find('.rsa-nav-tab.is-active').textContent.trim(), 'Email');
  });

  // Remove this test when email reconstruction is turned back on
  test('Click on Email tab opens the event in a new tab and does not change the current tab', async function(assert) {
    new ReduxDataHelper(setState).meta([['foo', 'bar'], ['fooz', 'ball']]).isPacketView().build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.equal(find('.rsa-nav-tab.is-active').textContent.trim(), 'Packet');
    await selectChoose('.heading-select', 'Email');
    assert.equal(find('.rsa-nav-tab.is-active').textContent.trim(), 'Packet');
  });

  test('Click on Web tab opens the web meta in a new tab and does not change the current tab', async function(assert) {
    new ReduxDataHelper(setState).meta([['foo', 'bar'], ['fooz', 'ball']]).isPacketView().build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.equal(find('.rsa-nav-tab.is-active').textContent.trim(), 'Packet');
    await selectChoose('.heading-select', 'Web');
    assert.equal(find('.rsa-nav-tab.is-active').textContent.trim(), 'Packet');
  });

  // Remove/Update this test when email reconstruction is turned back on
  test('email and web views shows icon of classic redirect', async function(assert) {
    new ReduxDataHelper(setState).meta([['foo', 'bar'], ['fooz', 'ball']]).isPacketView().isNetworkEvent().build();
    await render(hbs`{{recon-event-titlebar}}`);
    await clickTrigger();
    assert.equal(findAll('.ember-power-select-option .rsa-icon-expand-5').length, 2, 'Email and Web View');
  });

  // header toggle

  test('header toggle renders properly when active', async function(assert) {
    new ReduxDataHelper(setState).meta([['foo', 'bar'], ['fooz', 'ball']]).isTextView().isHeaderOpen(true).build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.ok(find('.rsa-icon-layout-6'), 'icon is displayed');
    assert.ok(find('.rsa-icon-layout-6.active'), 'icon is active');
  });

  test('header toggle renders properly when inactive', async function(assert) {
    new ReduxDataHelper(setState).meta([['foo', 'bar'], ['fooz', 'ball']]).isTextView().isHeaderOpen(false).build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.ok(find('.rsa-icon-layout-6'), 'icon is displayed');
    assert.notOk(find('.rsa-icon-layout-6.active'), 'icon is not active');
  });

  test('clicking header toggles header icon being active', async function(assert) {
    new ReduxDataHelper(setState).meta([['foo', 'bar'], ['fooz', 'ball']]).isTextView().isHeaderOpen(true).build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.ok(find('.rsa-icon-layout-6.active'), 'icon is active to stat');
    await click('.rsa-icon-layout-6');
    assert.notOk(find('.rsa-icon-layout-6.active'), 'icon toggles to inactive after toggle');
  });

  // TODO, move this to a integration/unit test on the action creator, just call the function with a new settting
  // and verify the setting gets sent
  test('isHeaderOpen toggle should result in call to setPreferences with new setting for flag', async function(assert) {
    assert.expect(4);
    new ReduxDataHelper(setState).meta([['foo', 'bar'], ['fooz', 'ball']]).isTextView().isHeaderOpen(true).build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.ok(find('.rsa-icon-layout-6'), 'icon is displayed');
    assert.ok(find('.rsa-icon-layout-6.active'), 'icon is active');
    await click('.rsa-icon-layout-6.active');
    assert.ok(find('.rsa-icon-layout-6'), 'icon is displayed');
    assert.notOk(find('.rsa-icon-layout-6.active'), 'icon is not active');
  });

  // request/response toggle

  test('log events do not render request/response toggles', async function(assert) {
    new ReduxDataHelper(setState).meta([['foo', 'bar'], ['fooz', 'ball']]).isTextView().isLogEvent().build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.notOk(find('.toggle-request'), 'request toggle should be missing');
    assert.notOk(find('.toggle-response'), 'response toggle should be missing');
  });

  test('endpoint events do not render request/response toggles', async function(assert) {
    new ReduxDataHelper(setState).meta([['foo', 'bar'], ['fooz', 'ball']]).isTextView().isEndpointEvent().build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.notOk(find('.toggle-request'), 'request toggle should be missing');
    assert.notOk(find('.toggle-response'), 'response toggle should be missing');
  });

  test('network events render request/response button', async function(assert) {
    new ReduxDataHelper(setState).meta([['foo', 'bar'], ['fooz', 'ball']]).isTextView().isNetworkEvent().build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.ok(find('.toggle-request'), 'request toggle should be present');
    assert.ok(find('.toggle-response'), 'response toggle should be present');
  });

  // request toggle

  test('request toggle renders properly when active', async function(assert) {
    new ReduxDataHelper(setState).meta([['foo', 'bar'], ['fooz', 'ball']]).isTextView().isRequestShown(true).build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.ok(find('.toggle-request'), 'icon is displayed');
    assert.ok(find('.toggle-request.active'), 'icon is active');
  });

  test('request toggle renders properly when inactive', async function(assert) {
    new ReduxDataHelper(setState).meta([['foo', 'bar'], ['fooz', 'ball']]).isTextView().isRequestShown(false).build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.ok(find('.toggle-request'), 'icon is displayed');
    assert.notOk(find('.toggle-request.active'), 'icon is not active');
  });

  test('clicking toggle request toggles icon to inactive', async function(assert) {
    new ReduxDataHelper(setState).meta([['foo', 'bar'], ['fooz', 'ball']]).isTextView().isRequestShown(true).build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.ok(find('.toggle-request.active'), 'icon is active to start');
    await click('.toggle-request');
    assert.notOk(find('.toggle-request.active'), 'icon flips to inactive after toggle');
  });

  // response toggle

  test('response toggle renders properly when active', async function(assert) {
    new ReduxDataHelper(setState).meta([['foo', 'bar'], ['fooz', 'ball']]).isTextView().isResponseShown(true).build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.ok(find('.toggle-response'), 'icon is displayed');
    assert.ok(find('.toggle-response.active'), 'icon is active');
  });

  test('response toggle renders properly when inactive', async function(assert) {
    new ReduxDataHelper(setState).meta([['foo', 'bar'], ['fooz', 'ball']]).isTextView().isResponseShown(false).build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.ok(find('.toggle-response'), 'icon is displayed');
    assert.notOk(find('.toggle-response.active'), 'icon is not active');
  });

  test('clicking toggle response toggles icon to inactive', async function(assert) {
    new ReduxDataHelper(setState).meta([['foo', 'bar'], ['fooz', 'ball']]).isTextView().isResponseShown(true).build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.ok(find('.toggle-response.active'), 'icon is active to start');
    await click('.toggle-response');
    assert.notOk(find('.toggle-response.active'), 'icon flips to inactive after toggle');
  });

  // meta toggle

  test('meta toggle renders properly when active', async function(assert) {
    new ReduxDataHelper(setState).meta([['foo', 'bar'], ['fooz', 'ball']]).isTextView().isMetaShown(true).build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.ok(find('.toggle-meta'), 'icon is displayed');
    assert.ok(find('.toggle-meta.active'), 'icon is active');
  });

  test('meta toggle renders properly when inactive', async function(assert) {
    new ReduxDataHelper(setState).meta([['foo', 'bar'], ['fooz', 'ball']]).isTextView().isMetaShown(false).build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.ok(find('.toggle-meta'), 'icon is displayed');
    assert.notOk(find('.toggle-meta.active'), 'icon is not active');
  });

  test('clicking meta toggle toggles icon to inactive', async function(assert) {
    new ReduxDataHelper(setState).meta([['foo', 'bar'], ['fooz', 'ball']]).isTextView().isMetaShown(true).build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.ok(find('.toggle-meta.active'), 'icon is active to start');
    await click('.toggle-meta');
    assert.notOk(find('.toggle-meta.active'), 'icon flips to inactive after toggle');
  });

  // standalone mode

  test('shrink and close icons do not show in standalone mode', async function(assert) {
    new ReduxDataHelper(setState).meta([['foo', 'bar'], ['fooz', 'ball']]).isFileView().isStandalone(true).build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.notOk(find('.rsa-icon-shrink-diagonal-2'), 'icon is not visible');
    assert.notOk(find('.rsa-icon-expand-diagonal-4'), 'icon is not visible');
    assert.notOk(find('.rsa-icon-close'), 'icon is not visible');
  });

  test('shrink and close icons show when not in standalone mode', async function(assert) {
    new ReduxDataHelper(setState)
      .meta([['foo', 'bar'], ['fooz', 'ball']])
      .isFileView()
      .isStandalone(false)
      .isReconExpanded(true)
      .isReconOpen(true)
      .build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.ok(find('.rsa-icon-shrink-diagonal-2'), 'icon is visible');
    assert.ok(find('.rsa-icon-close'), 'icon is visible');
  });

  // shrink/expand button

  test('shrink toggle renders proper expanded state', async function(assert) {
    new ReduxDataHelper(setState).meta([['foo', 'bar'], ['fooz', 'ball']]).isFileView().isReconExpanded(true).build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.ok(find('.rsa-icon-shrink-diagonal-2'), 'icon is visible');
    assert.notOk(find('.rsa-icon-expand-diagonal-4'), 'icon is not visible');
  });

  test('shrink toggle renders proper shrunk state', async function(assert) {
    new ReduxDataHelper(setState).meta([['foo', 'bar'], ['fooz', 'ball']]).isFileView().isReconExpanded(false).build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.notOk(find('.rsa-icon-shrink-diagonal-2'), 'icon is not visible');
    assert.ok(find('.rsa-icon-expand-diagonal-4'), 'icon is visible');
  });

  test('clicking shrink executes action', async function(assert) {
    new ReduxDataHelper(setState).meta([['foo', 'bar'], ['fooz', 'ball']]).isFileView().isReconExpanded(true).build();
    await render(hbs`{{recon-event-titlebar}}`);
    assert.ok(find('.rsa-icon-shrink-diagonal-2'), 'icon is visible');
    await click('.rsa-icon-shrink-diagonal-2');
    assert.notOk(find('.rsa-icon-shrink-diagonal-2'), 'icon is not visible');
    assert.ok(find('.rsa-icon-expand-diagonal-4'), 'icon is visible');
  });

  // close button

  test('clicking close executes action', async function(assert) {
    new ReduxDataHelper(setState).meta([['foo', 'bar'], ['fooz', 'ball']]).isTextView().isReconOpen(true).build();
    await render(hbs`{{recon-event-titlebar}}`);
    await click('.rsa-icon-close');
    const redux = this.owner.lookup('service:redux');
    assert.notOk(redux.getState().recon.visuals.isReconOpen, 'recon close action called');
  });
});
