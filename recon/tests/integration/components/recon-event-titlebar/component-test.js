import wait from 'ember-test-helpers/wait';
import Ember from 'ember';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import sinon from 'sinon';
import { clickTrigger, nativeMouseUp } from '../../../helpers/ember-power-select';

import { RECON_VIEW_TYPES_BY_NAME } from 'recon/utils/reconstruction-types';
import * as ACTION_TYPES from 'recon/actions/types';
import DataActions from 'recon/actions/data-creators';
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
  assert.equal(this.$('.ember-power-select-trigger').text().trim(), RECON_VIEW_TYPES_BY_NAME.PACKET.label);
});

test('clicking close executes action', function(assert) {
  this.render(hbs`{{recon-event-titlebar}}`);
  this.$().find('.rsa-icon-close').click();
  assert.equal(dispatchSpy.args[0][0].type, ACTION_TYPES.CLOSE_RECON);
});

test('clicking expand executes action', function(assert) {
  this.render(hbs`{{recon-event-titlebar}}`);
  this.$().find('.rsa-icon-arrow-left-9').click();
  assert.equal(dispatchSpy.args[0][0].type, ACTION_TYPES.TOGGLE_EXPANDED);
});

test('clicking shrink executes multiple actions', function(assert) {
  this.render(hbs`{{recon-event-titlebar}}`);
  this.$().find('.rsa-icon-arrow-left-9').click();
  this.$().find('.rsa-icon-arrow-right-9').click();
  assert.ok(dispatchSpy.calledTwice);
  assert.equal(dispatchSpy.args[0][0].type, ACTION_TYPES.TOGGLE_EXPANDED);
  assert.equal(dispatchSpy.args[1][0].type, ACTION_TYPES.TOGGLE_EXPANDED);
});

test('calls action when reconstruction view is changed', function(assert) {
  const actionSpy = sinon.spy(DataActions, 'setNewReconView');
  this.render(hbs`{{recon-event-titlebar}}`);
  clickTrigger();
  nativeMouseUp('.ember-power-select-option:contains("File View")');
  assert.ok(dispatchSpy.calledOnce);
  assert.equal(actionSpy.args[0][0].code, 2);
  actionSpy.reset();
});

test('clicking header toggle executes action', function(assert) {
  this.render(hbs`{{recon-event-titlebar}}`);
  this.$().find('.rsa-icon-layout-6').click();
  assert.equal(dispatchSpy.args[0][0].type, ACTION_TYPES.TOGGLE_HEADER);
});

test('clicking meta toggle executes actions', function(assert) {
  const actionSpy = sinon.spy(DataActions, 'toggleMetaData');
  this.render(hbs`{{recon-event-titlebar}}`);
  this.$().find('.rsa-icon-layout-2').click();
  assert.ok(dispatchSpy.calledOnce);
  assert.ok(actionSpy.calledOnce);
  actionSpy.reset();
});

const initializeData = { total: 555, index: 25, meta: [['medium', 1]] };

test('title renders', function(assert) {
  new DataHelper(this.get('redux')).initializeData(initializeData);
  this.render(hbs`{{recon-event-titlebar}}`);
  return wait().then(() => {
    assert.equal(this.$('.ember-power-select-trigger').text().trim(), `${RECON_VIEW_TYPES_BY_NAME.PACKET.label} (26 of 555)`);
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

test('everything but text is disabled for logs', function(assert) {
  assert.expect(3);
  new DataHelper(this.get('redux')).setEventTypeToLog();
  this.render(hbs`{{recon-event-titlebar}}`);
  return wait().then(() => {
    clickTrigger();
    assert.ok($('.ember-power-select-option:contains("Packet View")').attr('aria-disabled') === 'true', 'Packet View is disabled');
    assert.ok($('.ember-power-select-option:contains("File View")').attr('aria-disabled') === 'true', 'File View is disabled');
    assert.ok($('.ember-power-select-option:contains("Text View")').attr('aria-disabled') !== 'true', 'Text View is enabled');
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

test('request/response toggles disabled for logs', function(assert) {
  assert.expect(2);
  new DataHelper(this.get('redux')).setEventTypeToLog();
  this.render(hbs`{{recon-event-titlebar}}`);
  return wait().then(() => {
    assert.equal(this.$('.toggle-request').hasClass('disabled'), true, 'Request toggle disabled for log');
    assert.equal(this.$('.toggle-response').hasClass('disabled'), true, 'Response toggle disabled for log');
  });
});


