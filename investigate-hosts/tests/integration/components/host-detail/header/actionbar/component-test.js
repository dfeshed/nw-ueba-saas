import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import engineResolverFor from '../../../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../../../helpers/patch-reducer';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import sinon from 'sinon';
import { clickTrigger, selectChoose } from 'ember-power-select/test-support/helpers';
import { waitFor } from 'ember-wait-for-test-helper/wait-for';
import $ from 'jquery';
import hostDetails from 'investigate-hosts/actions/data-creators/details';
import wait from 'ember-test-helpers/wait';
import { patchFlash } from '../../../../../helpers/patch-flash';
import { getOwner } from '@ember/application';
import {
  snapShot
} from '../../../../../data/data';

let setState;
moduleForComponent('host-detail/header/actionbar', 'Integration | Component | host detail actionbar', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),
  beforeEach() {
    initialize(this);
    setState = (state) => {
      applyPatch(state);
      this.inject.service('redux');
    };
    this.inject.service('flash-messages');
    this.inject.service('flash-message');
    this.registry.injection('component', 'i18n', 'service:i18n');
  },
  afterEach() {
    revertPatch();
  }
});

test('snapshot power select renders appropriate items', function(assert) {
  new ReduxDataHelper(setState)
    .snapShot(snapShot)
    .build();
  this.render(hbs`{{host-detail/header/actionbar}}`);
  assert.equal(this.$('.actionbar .ember-power-select-trigger').length, 1, 'should render the power-select trigger');
  clickTrigger();
  return waitFor('.ember-power-select-options').then(function() {
    assert.equal($('.ember-power-select-option').length, 4, 'dropdown  rendered with available snapShots');
  });
});

sinon.stub(hostDetails, 'initializeAgentDetails');
sinon.stub(hostDetails, 'setTransition');
test('on selecting snapshot initializes the agent details input', function(assert) {
  new ReduxDataHelper(setState)
    .snapShot(snapShot)
    .scanTime('2017-08-29T10:23:49.452Z')
    .agentId(1)
    .build();
  this.render(hbs`{{host-detail/header/actionbar}}`);
  return wait().then(() => {
    clickTrigger();
    selectChoose('.actionbar', '.ember-power-select-option', 3);
    assert.ok(hostDetails.initializeAgentDetails.calledOnce);
    assert.equal(hostDetails.initializeAgentDetails.args[0][0].agentId, 1);
    hostDetails.initializeAgentDetails.restore();
    assert.ok(hostDetails.setTransition.calledOnce);
    assert.equal(hostDetails.setTransition.args[0][0], 'toUp');
    hostDetails.setTransition.reset();
  });
});

test('with scan time earlier than snapshot time, snapshot transitions down', function(assert) {
  new ReduxDataHelper(setState)
    .snapShot(snapShot)
    .scanTime('2017-01-01T10:23:49.452Z')
    .agentId(1)
    .build();
  this.render(hbs`{{host-detail/header/actionbar}}`);
  return wait().then(() => {
    clickTrigger();
    selectChoose('.actionbar', '.ember-power-select-option', 3);
    assert.ok(hostDetails.setTransition.calledOnce);
    assert.equal(hostDetails.setTransition.args[0][0], 'toDown');
    hostDetails.setTransition.restore();
  });
});

test('it opens 4.4 agent into thick client', function(assert) {
  const actionSpy = sinon.spy(window, 'open');
  const host = {
    id: 'FE22A4B3-31B8-4E6B-86D3-BF02B8366C3B',
    'machine': {
      'machineAgentId': 'FE22A4B3-31B8-4E6B-86D3-BF02B8366C3B',
      'agentVersion': '4.4'
    }
  };
  new ReduxDataHelper(setState)
    .host(host)
    .build();
  this.render(hbs `{{host-detail/header/actionbar}}`);
  this.$('.ecatUI .rsa-form-button-wrapper').trigger('click');
  assert.ok(actionSpy.calledOnce);
  assert.equal(actionSpy.args[0][0], 'ecatui:///machines/FE22A4B3-31B8-4E6B-86D3-BF02B8366C3B');
  actionSpy.restore();
});

test('Flash message when trying to open non 4.4 agent in thick client', function(assert) {
  const host = {
    id: 'FE22A4B3-31B8-4E6B-86D3-BF02B8366C3B',
    'machine': {
      'machineAgentId': 'FE22A4B3-31B8-4E6B-86D3-BF02B8366C3B',
      'agentVersion': '11.1'
    }
  };
  new ReduxDataHelper(setState)
    .host(host)
    .build();

  patchFlash((flash) => {
    const translation = getOwner(this).lookup('service:i18n');
    const expectedMsg = translation.t('investigateHosts.hosts.moreActions.notAnEcatAgent');
    assert.equal(flash.type, 'error');
    assert.equal(flash.message.string, expectedMsg);
  });
  this.render(hbs `{{host-detail/header/actionbar}}`);
  this.$('.ecatUI .rsa-form-button-wrapper').trigger('click');
});

test('test for start scan button', function(assert) {
  this.render(hbs `{{host-detail/header/actionbar}}`);
  assert.equal($('.host-start-scan-button').length, 1, 'scan-command renders giving the start scan button');
});

sinon.stub(hostDetails, 'exportFileContext');
test('test for Export to JSON', function(assert) {
  new ReduxDataHelper(setState)
    .scanTime('2017-01-01T10:23:49.452Z')
    .agentId(1)
    .build();
  this.render(hbs `{{host-detail/header/actionbar}}`);
  this.$('.host-action-buttons .action-button:nth-child(3) .rsa-form-button-wrapper').trigger('click');
  return wait().then(() => {
    assert.ok(hostDetails.exportFileContext.calledOnce);
    assert.deepEqual(hostDetails.exportFileContext.args[0][0], { 'agentId': 1, 'categories': ['AUTORUNS'], 'scanTime': '2017-01-01T10:23:49.452Z' });
    hostDetails.exportFileContext.restore();
  });
});

test('test for Export to JSON disabled', function(assert) {
  new ReduxDataHelper(setState)
    .snapShot([])
    .build();
  this.render(hbs `{{host-detail/header/actionbar}}`);
  assert.equal($('.host-action-buttons .action-button:nth-child(3) .rsa-form-button-wrapper').hasClass('is-disabled'), true, 'Export to JSON disabled when no snapshots available');
});

test('test when Export to JSON is in download status', function(assert) {
  new ReduxDataHelper(setState)
    .isJsonExportCompleted(false)
    .build();
  this.render(hbs `{{host-detail/header/actionbar}}`);
  assert.equal($('.host-action-buttons .action-button:nth-child(3) .rsa-form-button-wrapper').hasClass('is-disabled'), true, 'Export to JSON disabled when in downloading state');
  assert.equal($('.host-action-buttons .action-button:nth-child(3) .rsa-form-button-wrapper').text().trim(), 'Downloading', 'Export to JSON is in downloading state and button is disabled');
});

test('when JSON export is completed', function(assert) {
  new ReduxDataHelper(setState)
    .snapShot(snapShot)
    .build();
  this.render(hbs `{{host-detail/header/actionbar}}`);
  assert.equal($('.host-action-buttons .action-button:nth-child(3) .rsa-form-button-wrapper').text().trim(), 'Export to JSON', 'In initial state and when previous export is completed, button is active');
});