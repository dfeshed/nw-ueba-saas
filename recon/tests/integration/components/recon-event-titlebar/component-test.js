import wait from 'ember-test-helpers/wait';
import Ember from 'ember';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { clickTrigger } from '../../../helpers/ember-power-select';
import { patchSocket } from '../../../helpers/patch-socket';
import startApp from '../../../helpers/start-app';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

const application = startApp();
initialize(application);
const { $ } = Ember;

import DataHelper from '../../../helpers/data-helper';

let redux;
moduleForComponent('recon-event-titlebar', 'Integration | Component | recon event titlebar', {
  integration: true,
  beforeEach() {
    this.inject.service('redux');
    this.inject.service('preferences');
    redux = this.get('redux');
  }
});

test('no index or total shows just label for recon type', function(assert) {
  new DataHelper(this.get('redux'))
    .initializeData(initializeData)
    .setViewToText();
  this.render(hbs`{{recon-event-titlebar }}`);
  assert.equal(this.$('.ember-power-select-trigger').text().trim(), 'Text Analysis');
});

test('clicking close executes action', function(assert) {
  new DataHelper(this.get('redux'))
    .setViewToText();
  this.render(hbs`{{recon-event-titlebar}}`);
  assert.equal(redux.getState().recon.visuals.isReconOpen, false);
  this.$('.rsa-icon-close-filled').click();
  assert.equal(redux.getState().recon.visuals.isReconOpen, false);
});

test('clicking shrink executes action', function(assert) {
  new DataHelper(this.get('redux'))
    .setViewToFile();
  this.render(hbs`{{recon-event-titlebar}}`);
  assert.equal(redux.getState().recon.visuals.isReconExpanded, true);
  this.$('.rsa-icon-shrink-diagonal-2-filled').click();
  return wait().then(() => {
    assert.equal(redux.getState().recon.visuals.isReconExpanded, false);
  });
});

test('clicking shrink, then grow, executes multiple actions', function(assert) {
  new DataHelper(this.get('redux'))
    .initializeData(initializeData)
    .setViewToFile();
  this.render(hbs`{{recon-event-titlebar}}`);
  this.$('.rsa-icon-shrink-diagonal-2-filled').click();
  return wait().then(() => {
    assert.equal(redux.getState().recon.visuals.isReconExpanded, false);
    this.$('.rsa-icon-expand-diagonal-4-filled').click();
    return wait().then(() => {
      assert.equal(redux.getState().recon.visuals.isReconExpanded, true);
    });
  });
});

test('calls action when reconstruction view is changed', function(assert) {
  new DataHelper(this.get('redux'))
    .initializeData(initializeData)
    .setViewToText();
  this.render(hbs`{{recon-event-titlebar }}`);
  clickTrigger();
  return wait().then(() => {
    assert.equal(redux.getState().recon.visuals.currentReconView.code, 3);
  });
});

test('clicking header toggle executes action', function(assert) {
  new DataHelper(this.get('redux'))
    .initializeData(initializeData)
    .setViewToText();
  this.render(hbs`{{recon-event-titlebar}}`);
  assert.equal(redux.getState().recon.visuals.isHeaderOpen, true);
  this.$('.rsa-icon-layout-6-filled').click();
  return wait().then(() => {
    assert.equal(redux.getState().recon.visuals.isHeaderOpen, false);
  });
});

/*
 *@private
 *checks if serviceCall for getPreferences is happening successfully
 * checks if whatever the get call returns is set to the state and used succesfully
*/
test('Recon should initialize with default preferences set by user', function(assert) {
  new DataHelper(this.get('redux'))
    .initializeData(initializeData)
    .setViewToText();
  patchSocket((method, modelName) => {
    assert.equal(method, 'getPreferences');
    assert.equal(modelName, 'investigate-events-preferences');
  });
  this.render(hbs`{{recon-event-titlebar}}`);
  assert.equal(redux.getState().recon.visuals.isHeaderOpen, true);
  this.$('.rsa-icon-layout-6-filled').click();
  return wait().then(() => {
    assert.equal(redux.getState().recon.visuals.isHeaderOpen, false);
  });
});

/*
 *@private
 *checks if serviceCall for setPreferences is happening successfully
 * checks if whatever the user sets, same is given to setPreferences
*/
test('Toggle should call setPreferences with complete preferences object', function(assert) {
  new DataHelper(this.get('redux'))
    .initializeData(initializeData)
    .setViewToText();
  const done = assert.async(1);
  this.get('preferences').getPreferences('investigate-events').then((data) => {
    this.render(hbs`{{recon-event-titlebar}}`);
    assert.equal(redux.getState().recon.visuals.isHeaderOpen, true);
    data.userServicePreferences.eventsPreferences.isHeaderOpen = false;
    data.userServicePreferences.eventsPreferences.isReconOpen = false;
    this.$('.rsa-icon-layout-6-filled').click();
    patchSocket((method, modelName, query) => {
      assert.equal(method, 'setPreferences');
      assert.equal(modelName, 'investigate-events-preferences');
      assert.deepEqual(query, {
        data
      });
    });
    done();
    return wait().then(() => {
      assert.equal(redux.getState().recon.visuals.isHeaderOpen, false);
    });
  });
});


test('clicking meta toggle executes actions', function(assert) {
  new DataHelper(this.get('redux'))
    .initializeData(initializeData)
    .setViewToText();
  this.render(hbs`{{recon-event-titlebar}}`);
  assert.equal(redux.getState().recon.visuals.isMetaShown, true);
  this.$('.rsa-icon-layout-2-filled').click();
  return wait().then(() => {
    assert.equal(redux.getState().recon.visuals.isMetaShown, false);
  });
});

test('clicking request toggle executes actions', function(assert) {
  new DataHelper(this.get('redux'))
    .initializeData(initializeData)
     .setViewToText();
  this.render(hbs`{{recon-event-titlebar}}`);
  assert.equal(redux.getState().recon.visuals.isRequestShown, true);
  this.$('.rsa-icon-arrow-circle-right-2-filled').click();
  return wait().then(() => {
    assert.equal(redux.getState().recon.visuals.isRequestShown, false);
  });
});

test('clicking response toggle executes actions', function(assert) {
  new DataHelper(this.get('redux'))
    .initializeData(initializeData)
    .setViewToText();
  this.render(hbs`{{recon-event-titlebar}}`);
  assert.equal(redux.getState().recon.visuals.isResponseShown, true);
  this.$('.rsa-icon-arrow-circle-left-2-filled').click();
  return wait().then(() => {
    assert.equal(redux.getState().recon.visuals.isResponseShown, false);
  });
});

const initializeData = { total: 555, index: 25, meta: [['medium', 1]] };

test('title renders', function(assert) {
  new DataHelper(this.get('redux'))
    .initializeData(initializeData)
    .setViewToText();
  this.render(hbs`{{recon-event-titlebar}}`);
  return wait().then(() => {
    assert.equal(this.$('.ember-power-select-trigger').text().trim(), 'Text Analysis');
  });
});

test('all views enabled for network sessions', function(assert) {
  assert.expect(3);
  new DataHelper(this.get('redux'))
    .setViewToPacket()
    .initializeData({ meta: [['medium', 1]] });
  this.render(hbs`{{recon-event-titlebar}}`);
  return wait().then(() => {
    clickTrigger();
    assert.ok($('.ember-power-select-option:contains("Packet View")').attr('aria-disabled') !== 'true', 'Packet View is enabled');
    assert.ok($('.ember-power-select-option:contains("File View")').attr('aria-disabled') !== 'true', 'File View is enabled');
    assert.ok($('.ember-power-select-option:contains("Text View")').attr('aria-disabled') !== 'true', 'Text View is enabled');
  });
});

test('just text view exists for log events', function(assert) {
  assert.expect(1);
  new DataHelper(this.get('redux'))
    .initializeData({ meta: [['medium', 32]] })
    .setViewToText();
  this.render(hbs`{{recon-event-titlebar}}`);
  return wait().then(() => {
    assert.equal(this.$('.event-title').text().trim(), 'Log Event Details');
  });
});

test('request/response toggles enabled for packets', function(assert) {
  assert.expect(2);
  new DataHelper(this.get('redux'))
    .setViewToPacket()
    .initializeData({ meta: [['medium', 32]] });
  // set to packets by default
  this.render(hbs`{{recon-event-titlebar}}`);
  return wait().then(() => {
    assert.equal(this.$('.toggle-request').hasClass('disabled'), false, 'Request toggle enabled for packets');
    assert.equal(this.$('.toggle-response').hasClass('disabled'), false, 'Response toggle enabled for packets');
  });
});

test('request/response insure that request, response, Top/Bottom and Side by Side are not generated for logs', function(assert) {
  assert.expect(6);
  new DataHelper(this.get('redux'))
    .setViewToText()
    .initializeData({ meta: [['medium', 32]] });
  this.render(hbs`{{recon-event-titlebar}}`);
  return wait().then(() => {
    assert.equal(this.$('.rsa-icon-layout-6-filled').length, true, 'Show/Hide Header button should exist for log');
    assert.equal(this.$('.toggle-request').length, false, 'Request button does not exist for log');
    assert.equal(this.$('.toggle-response').length, false, 'Response button does not exist for log');
    assert.equal(this.$('.rsa-icon-view-agenda-filled').length, false, 'Top/Bottom View button does not exist for log');
    assert.equal(this.$('.rsa-icon-layout-4-filled').length, false, 'Side by Side View button does not exist for log');
    assert.equal(this.$('.toggle-meta').length, true, 'Show/Hide Meta button should exist for log');
  });
});
