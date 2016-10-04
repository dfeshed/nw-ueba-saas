import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import sinon from 'sinon';

import { TYPES_BY_NAME } from 'recon/utils/reconstruction-types';

import * as ACTION_TYPES from 'recon/actions/types';

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
  this.render(hbs`{{recon-event-titlebar reconstructionType=reconstructionType}}`);
  assert.equal(this.$('.prompt').text().trim(), TYPES_BY_NAME.PACKET.label);
});

test('title renders', function(assert) {
  this.set('total', 555);
  this.set('index', 25);
  this.render(hbs`{{recon-event-titlebar index=index total=total}}`);
  assert.equal(this.$('.prompt').text().trim(), `${TYPES_BY_NAME.PACKET.label} (26 of 555)`);
});

test('clicking close executes action', function(assert) {
  this.render(hbs`{{recon-event-titlebar}}`);
  this.$().find('.rsa-icon-close').click();
  assert.equal(dispatchSpy.args[0][0].type, ACTION_TYPES.CLOSE_RECON);
});

test('clicking expand executes action', function(assert) {
  this.render(hbs`{{recon-event-titlebar}}`);
  this.$().find('.rsa-icon-arrow-left-9').click();
  assert.equal(dispatchSpy.args[0][0].type, ACTION_TYPES.TOGGLE_RECON_EXPANDED);
});

test('clicking shrink executes multiple actions', function(assert) {
  this.render(hbs`{{recon-event-titlebar}}`);
  this.$().find('.rsa-icon-arrow-left-9').click();
  this.$().find('.rsa-icon-arrow-right-9').click();
  assert.ok(dispatchSpy.calledTwice);
  assert.equal(dispatchSpy.args[0][0].type, ACTION_TYPES.TOGGLE_RECON_EXPANDED);
  assert.equal(dispatchSpy.args[1][0].type, ACTION_TYPES.TOGGLE_RECON_EXPANDED);
});

test('calls action when reconstruction view is changed', function(assert) {
  this.render(hbs`{{recon-event-titlebar}}`);
  this.$().find('.prompt').click();
  this.$().find('select').val('2').trigger('change');
  assert.equal(dispatchSpy.args[0][0].type, ACTION_TYPES.CHANGE_RECON_VIEW);
});

test('clicking header toggle executes action', function(assert) {
  this.render(hbs`{{recon-event-titlebar}}`);
  this.$().find('.rsa-icon-layout-6').click();
  assert.equal(dispatchSpy.args[0][0].type, ACTION_TYPES.TOGGLE_HEADER);
});

test('clicking meta toggle executes actions', function(assert) {
  this.render(hbs`{{recon-event-titlebar}}`);
  this.$().find('.rsa-icon-layout-2').click();
  assert.equal(dispatchSpy.args[0][0].type, ACTION_TYPES.TOGGLE_META);
});
