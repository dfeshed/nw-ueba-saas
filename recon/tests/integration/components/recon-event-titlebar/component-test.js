import $ from 'jquery';
import wait from 'ember-test-helpers/wait';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import sinon from 'sinon';

import { clickTrigger, selectChoose } from '../../../helpers/ember-power-select';
import { applyPatch, revertPatch } from '../../../helpers/patch-reducer';
import startApp from '../../../helpers/start-app';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import * as VisualCreators from 'recon/actions/visual-creators';
import * as DataCreators from 'recon/actions/data-creators';

const application = startApp();
initialize(application);

let setState;

moduleForComponent('recon-event-titlebar', 'Integration | Component | recon event titlebar', {
  integration: true,
  beforeEach() {
    setState = (state) => {
      applyPatch(state);
      this.inject.service('redux');
    };
  },
  afterEach() {
    revertPatch();
  }
});

// recon view

test('renders log event label and no view selection for log events', function(assert) {
  new ReduxDataHelper(setState).isLogEvent().build();
  this.render(hbs`{{recon-event-titlebar}}`);
  return wait().then(() => {
    assert.equal(this.$('.event-title').text().trim(), 'Log Event Details', 'Title is correct text');
    assert.equal(this.$('.ember-power-select-trigger').length, 0, 'there is not a power select rendered');
  });
});

test('renders endpoint event label and no view selection for endpoint events', function(assert) {
  new ReduxDataHelper(setState).isEndpointEvent().build();
  this.render(hbs`{{recon-event-titlebar}}`);
  return wait().then(() => {
    assert.equal(this.$('.event-title').text().trim(), 'Endpoint Event Details', 'Title is correct text');
    assert.equal(this.$('.ember-power-select-trigger').length, 0, 'there is not a power select rendered');
  });
});

test('renders network event label and view selection for network events', function(assert) {
  new ReduxDataHelper(setState).isPacketView().isNetworkEvent().build();
  this.render(hbs`{{recon-event-titlebar}}`);
  return wait().then(() => {
    assert.equal(this.$('.event-title').text().trim(), 'Network Event Details', 'Title is correct text');
    assert.equal(this.$('.ember-power-select-trigger').text().trim(), 'Packet Analysis', 'power select is populated with correct default');
  });
});

// Stubbing outside the test otherwise tests get confused and jump back and forth
// between failing and passing, probably due to some load order consideration
sinon.stub(DataCreators, 'setNewReconView');

test('when reconstruction view is action is called', function(assert) {
  new ReduxDataHelper(setState).isTextView().build();
  this.render(hbs`{{recon-event-titlebar}}`);
  assert.equal(this.$('.ember-power-select-trigger').text().trim(), 'Text Analysis', 'power select is populated with correct default');
  clickTrigger('.heading-select');
  selectChoose('.heading-select', 'File');
  assert.equal(DataCreators.setNewReconView.calledOnce, true, 'action is called');
  assert.equal(DataCreators.setNewReconView.args[0][0].name, 'FILE', 'right recon view is provided');
  DataCreators.setNewReconView.reset();

  clickTrigger('.heading-select');
  selectChoose('.heading-select', 'Packet');
  assert.equal(DataCreators.setNewReconView.calledOnce, true, 'action is called');
  assert.equal(DataCreators.setNewReconView.args[0][0].name, 'PACKET', 'right recon view is provided');

  DataCreators.setNewReconView.restore();
});

test('all views enabled for network sessions', function(assert) {
  assert.expect(5);
  new ReduxDataHelper(setState).isPacketView().isNetworkEvent().build();
  this.render(hbs`{{recon-event-titlebar}}`);
  return wait().then(() => {
    clickTrigger();
    assert.ok($('.ember-power-select-option:contains("Packet View")').attr('aria-disabled') !== 'true', 'Packet View is enabled');
    assert.ok($('.ember-power-select-option:contains("File View")').attr('aria-disabled') !== 'true', 'File View is enabled');
    assert.ok($('.ember-power-select-option:contains("Text View")').attr('aria-disabled') !== 'true', 'Text View is enabled');
    assert.ok($('.ember-power-select-option:contains("Eamil View")').attr('aria-disabled') !== 'true', 'Email View is enabled');
    assert.ok($('.ember-power-select-option:contains("Web View")').attr('aria-disabled') !== 'true', 'Web View is enabled');
  });
});

test('Show all tabs for network event', function(assert) {
  new ReduxDataHelper(setState).isPacketView().isNetworkEvent().build();
  this.render(hbs`{{recon-event-titlebar}}`);
  assert.equal(this.$('.rsa-nav-tab').length, 5, 'show 5 analysis tabs');
  assert.equal(this.$('.rsa-nav-tab.is-active').text().trim(), 'Packet Analysis');
});

test('Show only text analysis label for log event', function(assert) {
  new ReduxDataHelper(setState).isTextView().isLogEvent().build();
  this.render(hbs`{{recon-event-titlebar}}`);
  assert.equal(this.$('.rsa-nav-tab').length, 0);
  assert.equal(this.$('.tview-label').text().trim(), 'Text Analysis');
});

test('Click on Email tab does not change the tab', function(assert) {
  new ReduxDataHelper(setState).isTextView().build();
  this.render(hbs`{{recon-event-titlebar}}`);
  assert.equal(this.$('.rsa-nav-tab.is-active').text().trim(), 'Text Analysis');
  assert.equal(this.$('.rsa-nav-tab')[3].textContent.trim(), 'Email');
  this.$('.rsa-nav-tab')[3].click();
  return wait().then(() => {
    assert.equal(this.$('.rsa-nav-tab.is-active').text().trim(), 'Text Analysis');
  });
});

test('Click on Web tab opens the web meta in a new tab and does not change the current tab', function(assert) {
  new ReduxDataHelper(setState).isPacketView().build();
  this.render(hbs`{{recon-event-titlebar}}`);
  assert.equal(this.$('.rsa-nav-tab.is-active').text().trim(), 'Packet Analysis');
  assert.equal(this.$('.rsa-nav-tab')[4].textContent.trim(), 'Web');
  this.$('.rsa-nav-tab')[4].click();
  return wait().then(() => {
    assert.equal(this.$('.rsa-nav-tab.is-active').text().trim(), 'Packet Analysis');
  });
});


// header toggle

test('header toggle renders properly when active', function(assert) {
  new ReduxDataHelper(setState).isTextView().isHeaderOpen(true).build();
  this.render(hbs`{{recon-event-titlebar}}`);
  return wait().then(() => {
    assert.equal(this.$('.rsa-icon-layout-6-filled').length, 1, 'icon is displayed');
    assert.equal(this.$('.rsa-icon-layout-6-filled.active').length, 1, 'icon is active');
  });
});

test('header toggle renders properly when inactive', function(assert) {
  new ReduxDataHelper(setState).isTextView().isHeaderOpen(false).build();
  this.render(hbs`{{recon-event-titlebar}}`);
  return wait().then(() => {
    assert.equal(this.$('.rsa-icon-layout-6-filled').length, 1, 'icon is displayed');
    assert.equal(this.$('.rsa-icon-layout-6-filled.active').length, 0, 'icon is not active');
  });
});

test('clicking header toggles header icon being active', function(assert) {
  new ReduxDataHelper(setState).isTextView().isHeaderOpen(true).build();
  this.render(hbs`{{recon-event-titlebar}}`);
  assert.equal(this.$('.rsa-icon-layout-6-filled.active').length, 1, 'icon is active to stat');
  this.$('.rsa-icon-layout-6-filled').click();
  return wait().then(() => {
    assert.equal(this.$('.rsa-icon-layout-6-filled.active').length, 0, 'icon toggles to inactive after toggle');
  });
});

// TODO, move this to a integration/unit test on the action creator, just call the function with a new settting
// and verify the setting gets sent
test('isHeaderOpen toggle should result in call to setPreferences with new setting for flag', function(assert) {
  assert.expect(4);
  new ReduxDataHelper(setState).isTextView().isHeaderOpen(true).build();

  this.render(hbs`{{recon-event-titlebar}}`);
  assert.equal(this.$('.rsa-icon-layout-6-filled').length, 1, 'icon is displayed');
  assert.equal(this.$('.rsa-icon-layout-6-filled.active').length, 1, 'icon is active');
  this.$('.rsa-icon-layout-6-filled.active').click();
  assert.equal(this.$('.rsa-icon-layout-6-filled').length, 1, 'icon is displayed');
  assert.equal(this.$('.rsa-icon-layout-6-filled.active').length, 0, 'icon is not active');

});

// request/response toggle

test('log events do not render request/response toggles', function(assert) {
  new ReduxDataHelper(setState).isTextView().isLogEvent().build();
  this.render(hbs`{{recon-event-titlebar}}`);
  return wait().then(() => {
    assert.equal(this.$('.toggle-request').length, 0, 'request toggle should be missing');
    assert.equal(this.$('.toggle-response').length, 0, 'response toggle should be missing');
  });
});

test('endpoint events do not render request/response toggles', function(assert) {
  new ReduxDataHelper(setState).isTextView().isEndpointEvent().build();
  this.render(hbs`{{recon-event-titlebar}}`);
  return wait().then(() => {
    assert.equal(this.$('.toggle-request').length, 0, 'request toggle should be missing');
    assert.equal(this.$('.toggle-response').length, 0, 'response toggle should be missing');
  });
});

test('network events render request/response button', function(assert) {
  new ReduxDataHelper(setState).isTextView().isNetworkEvent().build();
  this.render(hbs`{{recon-event-titlebar}}`);
  return wait().then(() => {
    assert.equal(this.$('.toggle-request').length, 1, 'request toggle should be present');
    assert.equal(this.$('.toggle-response').length, 1, 'response toggle should be present');
  });
});

// request toggle

test('request toggle renders properly when active', function(assert) {
  new ReduxDataHelper(setState).isTextView().isRequestShown(true).build();
  this.render(hbs`{{recon-event-titlebar}}`);
  return wait().then(() => {
    assert.equal(this.$('.toggle-request').length, 1, 'icon is displayed');
    assert.equal(this.$('.toggle-request.active').length, 1, 'icon is active');
  });
});

test('request toggle renders properly when inactive', function(assert) {
  new ReduxDataHelper(setState).isTextView().isRequestShown(false).build();
  this.render(hbs`{{recon-event-titlebar}}`);
  return wait().then(() => {
    assert.equal(this.$('.toggle-request').length, 1, 'icon is displayed');
    assert.equal(this.$('.toggle-request.active').length, 0, 'icon is not active');
  });
});

test('clicking toggle request toggles icon to inactive', function(assert) {
  new ReduxDataHelper(setState).isTextView().isRequestShown(true).build();
  this.render(hbs`{{recon-event-titlebar}}`);
  assert.equal(this.$('.toggle-request.active').length, 1, 'icon is active to start');
  this.$('.toggle-request').click();
  return wait().then(() => {
    assert.equal(this.$('.toggle-request.active').length, 0, 'icon flips to inactive after toggle');
  });
});

// response toggle

test('response toggle renders properly when active', function(assert) {
  new ReduxDataHelper(setState).isTextView().isResponseShown(true).build();
  this.render(hbs`{{recon-event-titlebar}}`);
  return wait().then(() => {
    assert.equal(this.$('.toggle-response').length, 1, 'icon is displayed');
    assert.equal(this.$('.toggle-response.active').length, 1, 'icon is active');
  });
});

test('response toggle renders properly when inactive', function(assert) {
  new ReduxDataHelper(setState).isTextView().isResponseShown(false).build();
  this.render(hbs`{{recon-event-titlebar}}`);
  return wait().then(() => {
    assert.equal(this.$('.toggle-response').length, 1, 'icon is displayed');
    assert.equal(this.$('.toggle-response.active').length, 0, 'icon is not active');
  });
});

test('clicking toggle response toggles icon to inactive', function(assert) {
  new ReduxDataHelper(setState).isTextView().isResponseShown(true).build();
  this.render(hbs`{{recon-event-titlebar}}`);
  assert.equal(this.$('.toggle-response.active').length, 1, 'icon is active to start');
  this.$('.toggle-response').click();
  return wait().then(() => {
    assert.equal(this.$('.toggle-response.active').length, 0, 'icon flips to inactive after toggle');
  });
});

// meta toggle

test('meta toggle renders properly when active', function(assert) {
  new ReduxDataHelper(setState).isTextView().isMetaShown(true).build();
  this.render(hbs`{{recon-event-titlebar}}`);
  return wait().then(() => {
    assert.equal(this.$('.toggle-meta').length, 1, 'icon is displayed');
    assert.equal(this.$('.toggle-meta.active').length, 1, 'icon is active');
  });
});

test('meta toggle renders properly when inactive', function(assert) {
  new ReduxDataHelper(setState).isTextView().isMetaShown(false).build();
  this.render(hbs`{{recon-event-titlebar}}`);
  return wait().then(() => {
    assert.equal(this.$('.toggle-meta').length, 1, 'icon is displayed');
    assert.equal(this.$('.toggle-meta.active').length, 0, 'icon is not active');
  });
});

test('clicking meta toggle toggles icon to inactive', function(assert) {
  new ReduxDataHelper(setState).isTextView().isMetaShown(true).build();
  this.render(hbs`{{recon-event-titlebar}}`);
  assert.equal(this.$('.toggle-meta.active').length, 1, 'icon is active to start');
  this.$('.toggle-meta').click();
  return wait().then(() => {
    assert.equal(this.$('.toggle-meta.active').length, 0, 'icon flips to inactive after toggle');
  });
});

// standalone mode

test('shrink and close icons do not show in standalone mode', function(assert) {
  new ReduxDataHelper(setState).isFileView().isStandalone(true).build();
  this.render(hbs`{{recon-event-titlebar}}`);
  return wait().then(() => {
    assert.equal(this.$('.rsa-icon-shrink-diagonal-2-filled').length, 0, 'icon is not visible');
    assert.equal(this.$('.rsa-icon-expand-diagonal-4-filled').length, 0, 'icon is not visible');
    assert.equal(this.$('.rsa-icon-close-filled').length, 0, 'icon is not visible');
  });
});

test('shrink and close icons show when not in standalone mode', function(assert) {
  new ReduxDataHelper(setState)
    .isFileView()
    .isStandalone(false)
    .isReconExpanded(true)
    .isReconOpen(true)
    .build();
  this.render(hbs`{{recon-event-titlebar}}`);
  return wait().then(() => {
    assert.equal(this.$('.rsa-icon-shrink-diagonal-2-filled').length, 1, 'icon is visible');
    assert.equal(this.$('.rsa-icon-close-filled').length, 1, 'icon is visible');
  });
});

// shrink/expand button

test('shrink toggle renders proper expanded state', function(assert) {
  new ReduxDataHelper(setState).isFileView().isReconExpanded(true).build();
  this.render(hbs`{{recon-event-titlebar}}`);
  return wait().then(() => {
    assert.equal(this.$('.rsa-icon-shrink-diagonal-2-filled').length, 1, 'icon is visible');
    assert.equal(this.$('.rsa-icon-expand-diagonal-4-filled').length, 0, 'icon is not visible');
  });
});

test('shrink toggle renders proper shrunk state', function(assert) {
  new ReduxDataHelper(setState).isFileView().isReconExpanded(false).build();
  this.render(hbs`{{recon-event-titlebar}}`);
  return wait().then(() => {
    assert.equal(this.$('.rsa-icon-shrink-diagonal-2-filled').length, 0, 'icon is not visible');
    assert.equal(this.$('.rsa-icon-expand-diagonal-4-filled').length, 1, 'icon is visible');
  });
});

test('clicking shrink executes action', function(assert) {
  new ReduxDataHelper(setState).isFileView().isReconExpanded(true).build();
  this.render(hbs`{{recon-event-titlebar}}`);
  assert.equal(this.$('.rsa-icon-shrink-diagonal-2-filled').length, 1, 'icon is visible');
  this.$('.rsa-icon-shrink-diagonal-2-filled').click();
  return wait().then(() => {
    assert.equal(this.$('.rsa-icon-shrink-diagonal-2-filled').length, 0, 'icon is not visible');
    assert.equal(this.$('.rsa-icon-expand-diagonal-4-filled').length, 1, 'icon is visible');
  });
});

// close button

// Stubbing outside the test otherwise tests get confused and jump back and forth
// between failing and passing, probably due to some load order consideration
sinon.stub(VisualCreators, 'closeRecon');

test('clicking close executes action', function(assert) {
  new ReduxDataHelper(setState).isTextView().isReconOpen(true).build();
  this.render(hbs`{{recon-event-titlebar}}`);
  return wait().then(() => {
    this.$('.rsa-icon-close-filled').click();
    assert.equal(VisualCreators.closeRecon.callCount, 1, 'close recon action creator called one time');
    VisualCreators.closeRecon.restore();
  });
});