import wait from 'ember-test-helpers/wait';
import Ember from 'ember';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import sinon from 'sinon';
import { clickTrigger, nativeMouseUp } from '../../../helpers/ember-power-select';

import * as ACTION_TYPES from 'recon/actions/types';
const { $ } = Ember;

import DataHelper from '../../../helpers/data-helper';

let dispatchSpy;

moduleForComponent('recon-event-titlebar', 'Integration | Component | recon event titlebar', {
  integration: true,
  beforeEach() {
    this.inject.service('redux');
    const redux = this.get('redux');
    dispatchSpy = sinon.spy(redux, 'dispatch');
  },
  afterEach() {
    dispatchSpy.reset();
  }
});

test('no index or total shows just label for recon type', function(assert) {
  this.render(hbs`{{recon-event-titlebar }}`);
  assert.equal(this.$('.ember-power-select-trigger').text().trim(), 'Text Analysis');
});

test('clicking close executes action', function(assert) {
  this.render(hbs`{{recon-event-titlebar}}`);
  this.$('.rsa-icon-close').click();
  assert.equal(dispatchSpy.args[0][0].type, ACTION_TYPES.CLOSE_RECON);
});

test('clicking shrink executes action', function(assert) {
  this.render(hbs`{{recon-event-titlebar}}`);
  this.$('.rsa-icon-arrow-right-9').click();
  assert.equal(dispatchSpy.args[0][0].type, ACTION_TYPES.TOGGLE_EXPANDED);
});

test('clicking shrink, then grow, executes multiple actions', function(assert) {
  this.render(hbs`{{recon-event-titlebar}}`);
  this.$('.rsa-icon-arrow-right-9').click();
  this.$('.rsa-icon-arrow-left-9').click();

  assert.equal(dispatchSpy.args[0][0].type, ACTION_TYPES.TOGGLE_EXPANDED);
  assert.equal(dispatchSpy.args[1][0].type, ACTION_TYPES.TOGGLE_EXPANDED);
});

test('calls action when reconstruction view is changed', function(assert) {
  this.render(hbs`{{recon-event-titlebar}}`);
  clickTrigger();
  nativeMouseUp('.ember-power-select-option:contains("File Analysis")');
  assert.ok(dispatchSpy.calledOnce);
  assert.ok(typeof dispatchSpy.args[0][0] === 'function', 'Dispatch called with function (thunk)');
});

test('clicking header toggle executes action', function(assert) {
  this.render(hbs`{{recon-event-titlebar}}`);
  this.$('.rsa-icon-layout-6').click();
  assert.equal(dispatchSpy.args[0][0].type, ACTION_TYPES.TOGGLE_HEADER);
});

test('clicking meta toggle executes actions', function(assert) {
  this.render(hbs`{{recon-event-titlebar}}`);
  this.$('.rsa-icon-layout-2').click();
  assert.ok(dispatchSpy.calledOnce, 'Dispatch called once');
  assert.ok(typeof dispatchSpy.args[0][0] === 'function', 'Dispatch called with function (thunk)');
});

const initializeData = { total: 555, index: 25, meta: [['medium', 1]] };

test('title renders', function(assert) {
  new DataHelper(this.get('redux')).initializeData(initializeData);
  this.render(hbs`{{recon-event-titlebar}}`);
  return wait().then(() => {
    assert.equal(this.$('.ember-power-select-trigger').text().trim(), 'Text Analysis');
  });
});


test('all views enabled for network sessions', function(assert) {
  assert.expect(3);
  new DataHelper(this.get('redux')).initializeData({ meta: [['medium', 1]] });
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
  new DataHelper(this.get('redux')).initializeData({ meta: [['medium', 32]] }).setViewToText();
  this.render(hbs`{{recon-event-titlebar}}`);
  return wait().then(() => {
    assert.equal(this.$('.event-title').text().trim(), 'Log Event Details');
  });
});

test('request/response toggles enabled for packets', function(assert) {
  assert.expect(2);
  // set to packets by default
  this.render(hbs`{{recon-event-titlebar}}`);
  return wait().then(() => {
    assert.equal(this.$('.toggle-request').hasClass('disabled'), false, 'Request toggle enabled for packets');
    assert.equal(this.$('.toggle-response').hasClass('disabled'), false, 'Response toggle enabled for packets');
  });
});

test('request/response insure that request, response, Top/Bottom and Side by Side are not generated for logs', function(assert) {
  assert.expect(6);
  new DataHelper(this.get('redux')).initializeData({ meta: [['medium', 32]] });
  this.render(hbs`{{recon-event-titlebar}}`);
  return wait().then(() => {
    assert.equal(this.$('.rsa-icon-layout-6').length, true, 'Show/Hide Header button should exist for log');
    assert.equal(this.$('.toggle-request').length, false, 'Request button does not exist for log');
    assert.equal(this.$('.toggle-response').length, false, 'Response button does not exist for log');
    assert.equal(this.$('.rsa-icon-view-agenda').length, false, 'Top/Bottom View button does not exist for log');
    assert.equal(this.$('.rsa-icon-layout-4').length, false, 'Side by Side View button does not exist for log');
    assert.equal(this.$('.toggle-meta').length, true, 'Show/Hide Meta button should exist for log');
  });
});


