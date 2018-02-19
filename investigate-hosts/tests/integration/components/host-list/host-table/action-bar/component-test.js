import { moduleForComponent, test, skip } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import engineResolverFor from '../../../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../../../helpers/patch-reducer';
import { patchFlash } from '../../../../../helpers/patch-flash';
import wait from 'ember-test-helpers/wait';
import { getOwner } from '@ember/application';
import $ from 'jquery';

let setState;
moduleForComponent('host-list/host-table/action-bar', 'Integration | Component | host table action bar', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    this.inject.service('flash-messages');
    this.inject.service('flash-message');
    setState = (state) => {
      applyPatch(state);
      this.inject.service('redux');
    };
  },
  afterEach() {
    revertPatch();
  }
});

test('it renders host table action bar', function(assert) {
  this.render(hbs`{{host-list/host-table/action-bar}}`);
  assert.equal(this.$('.host-table__toolbar').length, 1, 'host table action bar rendered');
});

test('it renders action bar buttons', function(assert) {
  this.render(hbs`{{host-list/host-table/action-bar}}`);
  assert.equal(this.$('.host-table__toolbar-buttons').length, 1, 'host table action bar buttons');
});

test('it renders action bar label', function(assert) {
  this.render(hbs`{{host-list/host-table/action-bar}}`);
  assert.equal(this.$('.host-table__toolbar > label').text().trim(), 'Hosts  (0)', 'host table action bar label');
});

test('it renders action bar label when totalItems is greater than zero', function(assert) {
  new ReduxDataHelper(setState)
  .totalHostItems(5).build();
  this.render(hbs`{{host-list/host-table/action-bar}}`);
  assert.equal(this.$('.host-table__toolbar > label').text().trim(), 'Hosts  (5)', 'host table action bar label');
});

test('it renders action bar start button', function(assert) {
  this.render(hbs`{{host-list/host-table/action-bar}}`);
  assert.equal(this.$('.host-table__toolbar .host-start-scan-button button').text().trim(), 'Start Scan', 'action bar start button label');
  assert.equal(this.$('.host-table__toolbar .host-start-scan-button > .is-disabled').length, 1, 'action bar start button is disabled');
});

test('it renders action bar stop button', function(assert) {
  this.render(hbs`{{host-list/host-table/action-bar}}`);
  assert.equal(this.$('.host-table__toolbar .stop-scan-button button').text().trim(), 'Stop Scan', 'action bar stop button label');
  assert.equal(this.$('.host-table__toolbar .stop-scan-button > .is-disabled').length, 1, 'action bar stop button is disabled');
});

test('it renders action bar delete button', function(assert) {
  this.render(hbs`{{host-list/host-table/action-bar}}`);
  assert.equal(this.$('.host-table__toolbar .delete-host-button button').text().trim(), 'Delete', 'action bar stop button label');
  assert.equal(this.$('.host-table__toolbar .delete-host-button > .is-disabled').length, 1, 'action bar delete button is disabled');
});

test('it renders action bar export to csv button', function(assert) {
  this.render(hbs`{{host-list/host-table/action-bar}}`);
  assert.equal(this.$('.host-table__toolbar-buttons div:nth-child(3) button').text().trim(), 'Export to CSV', 'action bar export to csv button label');
  assert.equal(this.$('.host-table__toolbar  div:nth-child(3)s > .is-disabled').length, 0, 'action bar export to csv button is enabled');
});

test('it renders action bar pivot to endpoint button', function(assert) {
  this.render(hbs`{{host-list/host-table/action-bar}}`);
  assert.equal(this.$('.host-table__toolbar-buttons div:nth-child(4) button').text().trim(), 'Pivot to Endpoint', 'action bar pivot to endpoint button label');
  assert.equal(this.$('.host-table__toolbar  div:nth-child(4) > .is-disabled').length, 0, 'action bar pivot to endpoint button is enabled');
});

test('it renders action bar start button when some hosts are selected', function(assert) {
  new ReduxDataHelper(setState).scanCount(2).build();
  this.render(hbs`{{host-list/host-table/action-bar}}`);
  assert.equal(this.$('.host-table__toolbar .host-start-scan-button button').text().trim(), 'Start Scan', 'action bar start button label');
  assert.equal(this.$('.host-table__toolbar .host-start-scan-button > .is-disabled').length, 0, 'action bar start button is enabled');
});

test('it renders action bar stop button when some hosts are selected', function(assert) {
  new ReduxDataHelper(setState).scanCount(2).build();
  this.render(hbs`{{host-list/host-table/action-bar}}`);
  assert.equal(this.$('.host-table__toolbar .stop-scan-button button').text().trim(), 'Stop Scan', 'action bar stop button label');
  assert.equal(this.$('.host-table__toolbar .stop-scan-button > .is-disabled').length, 0, 'action bar stop button is enabled');
});

test('it renders action bar delete button when some hosts are selected', function(assert) {
  new ReduxDataHelper(setState).scanCount(2).build();
  this.render(hbs`{{host-list/host-table/action-bar}}`);
  assert.equal(this.$('.host-table__toolbar .delete-host-button button').text().trim(), 'Delete', 'action bar stop button label');
  assert.equal(this.$('.host-table__toolbar .delete-host-button > .is-disabled').length, 0, 'action bar delete button is enabled');
});

test('it renders confirmation modal when delete button is clicked with some hosts selected', function(assert) {
  new ReduxDataHelper(setState).scanCount(2).build();
  this.render(hbs`{{host-list/host-table/action-bar}}`);
  assert.equal(this.$('.host-table__toolbar .delete-host-button > .is-disabled').length, 0, 'action bar delete button is enabled');
  assert.equal($('#modalDestination .confirmation-modal').length, 0, 'confirmation modal is not present'); // Modal content not be added to dom
  this.$('.host-table__toolbar .delete-host-button button').trigger('click');
  return wait().then(() => {
    assert.equal($('#modalDestination.active .confirmation-modal').length, 1, 'delete hosts confirmation modal');
    assert.equal($('#modalDestination .confirmation-modal h3').text().trim(), 'Delete 2 host(s)', 'confirmation modal title');
    assert.equal($('#modalDestination .confirmation-modal .modal-footer-buttons div:last-child button').text().trim(), 'Yes', 'Yes button label');
    this.$('.confirmation-modal .modal-footer-buttons div:last-child button').trigger('click');
    return wait().then(() => {
      assert.equal($('#modalDestination.active .confirmation-modal').length, 0, 'delete hosts confirmation modal is closed');
    });
  });
});

skip('it renders flash message when pivot to endpoint is clicked with not 4.4 host selected', function(assert) {
  assert.expect(2);
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
  this.render(hbs`{{host-list/host-table/action-bar}}`);
  this.$('.host-table__toolbar-buttons div:nth-child(4) button').trigger('click');
});


