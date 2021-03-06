import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, click, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let setState;

module('Integration | Component | host-table/action-bar', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
  });

  test('it renders host table action bar', async function(assert) {
    await render(hbs`{{host-list/host-table/action-bar}}`);
    assert.equal(document.querySelectorAll('.host-table__toolbar').length, 1, 'host table action bar rendered');
  });

  test('it renders action bar buttons', async function(assert) {
    await render(hbs`{{host-list/host-table/action-bar}}`);
    assert.equal(document.querySelectorAll('.host-table__toolbar-buttons').length, 1, 'host table action bar buttons');
  });

  test('it renders service selector button', async function(assert) {
    await render(hbs`{{host-list/host-table/action-bar}}`);
    assert.equal(document.querySelectorAll('.service-selector').length, 1, 'service selector is rendered');
  });

  test('it renders action bar start button', async function(assert) {
    await render(hbs`{{host-list/host-table/action-bar}}`);
    assert.equal(document.querySelector('.host-table__toolbar .start-scan-button button').textContent.trim(), 'Start Scan', 'action bar start button label');
    assert.equal(document.querySelectorAll('.host-table__toolbar .start-scan-button.is-disabled').length, 1, 'action bar start button is disabled');
  });

  test('it renders action bar stop button', async function(assert) {
    await render(hbs`{{host-list/host-table/action-bar}}`);
    assert.equal(document.querySelector('.host-table__toolbar .stop-scan-button button').textContent.trim(), 'Stop Scan', 'action bar stop button label');
    assert.equal(document.querySelectorAll('.host-table__toolbar .stop-scan-button.is-disabled').length, 1, 'action bar stop button is disabled');
  });

  test('it renders action bar export to csv button', async function(assert) {
    await render(hbs`{{host-list/host-table/action-bar}}`);
    assert.equal(document.querySelectorAll('.host-table__toolbar  div:nth-of-type(5).is-disabled').length, 1, 'action bar export to csv button is disabled by default');
  });

  test('it renders action bar start button when some hosts are selected', async function(assert) {
    new ReduxDataHelper(setState)
      .selectedHostList([{ id: '123', version: '4.4', managed: true, scanStatus: 'idle' }])
      .scanCount(2)
      .build();
    await render(hbs`{{host-list/host-table/action-bar}}`);
    assert.equal(document.querySelector('.host-table__toolbar .start-scan-button button').textContent.trim(), 'Start Scan', 'action bar start button label');
    assert.equal(document.querySelectorAll('.host-table__toolbar .start-scan-button.is-disabled').length, 1, 'action bar start button is enabled');
  });

  test('it renders action bar stop button when some hosts are selected', async function(assert) {
    new ReduxDataHelper(setState)
      .selectedHostList([{ id: '123', version: '11.3', managed: true, scanStatus: 'scanning' }])
      .scanCount(2)
      .build();
    await render(hbs`{{host-list/host-table/action-bar}}`);
    assert.equal(document.querySelector('.host-table__toolbar .stop-scan-button button').textContent.trim(), 'Stop Scan', 'action bar stop button label');
    assert.equal(document.querySelectorAll('.host-table__toolbar .stop-scan-button.is-disabled').length, 1, 'action bar stop button is enabled');
  });

  test('it disables start/stop scan button when selected hosts are migrated', async function(assert) {
    new ReduxDataHelper(setState).selectedHostList([{ id: '123', version: '11.3', managed: false, scanStatus: 'idle' }]).build();
    await render(hbs`{{host-list/host-table/action-bar}}`);
    assert.equal(document.querySelectorAll('.host-table__toolbar .start-scan-button.is-disabled').length, 1, 'start scan button is disabled');
    assert.equal(document.querySelectorAll('.host-table__toolbar .stop-scan-button.is-disabled').length, 1, 'stop scan button is disabled');
  });

  test('it enables start/stop scan button when selected hosts are managed', async function(assert) {
    new ReduxDataHelper(setState).selectedHostList([{ id: '123', version: '11.3', managed: true, scanStatus: 'idle' }]).build();
    await render(hbs`{{host-list/host-table/action-bar}}`);
    assert.equal(document.querySelectorAll('.host-table__toolbar .start-scan-button.is-disabled').length, 0, 'start scan button is enabled');
    assert.equal(document.querySelectorAll('.host-table__toolbar .stop-scan-button.is-disabled').length, 1, 'stop scan button is enabled');
  });

  test('it enables start/stop scan button when selected hosts contains migrated and managed', async function(assert) {
    new ReduxDataHelper(setState).selectedHostList([
      { id: '123', version: '11.3', managed: false, scanStatus: 'idle' },
      { id: '131', version: '11.3', managed: true, scanStatus: 'idle' } ]).build();
    await render(hbs`{{host-list/host-table/action-bar}}`);
    assert.equal(document.querySelectorAll('.host-table__toolbar .start-scan-button.is-disabled').length, 0, 'start scan button is enabled');
    assert.equal(document.querySelectorAll('.host-table__toolbar .stop-scan-button.is-disabled').length, 1, 'stop scan button is disabled');
  });

  test('it disables start/stop scan button when selected hosts are 4.4 agents', async function(assert) {
    new ReduxDataHelper(setState).selectedHostList([{ id: '123', version: '4.4', managed: true, scanStatus: 'idle' }]).build();
    await render(hbs`{{host-list/host-table/action-bar}}`);
    assert.equal(document.querySelectorAll('.host-table__toolbar .start-scan-button.is-disabled').length, 1, 'start scan button is disabled');
    assert.equal(document.querySelectorAll('.host-table__toolbar .stop-scan-button.is-disabled').length, 1, 'stop scan button is disabled');
  });

  test('it enables start/stop scan button when selected hosts contains 4.4 agents and 11.3 agents', async function(assert) {
    new ReduxDataHelper(setState).selectedHostList([
      { id: '123', version: '4.4', managed: true, scanStatus: 'idle' },
      { id: '131', version: '11.3', managed: true, scanStatus: 'idle' } ]).build();
    await render(hbs`{{host-list/host-table/action-bar}}`);
    assert.equal(document.querySelectorAll('.host-table__toolbar .start-scan-button.is-disabled').length, 0, 'start scan button is disabled');
    assert.equal(document.querySelectorAll('.host-table__toolbar .stop-scan-button.is-disabled').length, 1, 'stop scan button is enabled');
  });

  test('On changing the service host properties is closed', async function(assert) {
    assert.expect(2);
    const services = {
      serviceData: [{ id: '1', displayName: 'TEST', name: 'TEST', version: '11.1.0.0' }],
      summaryData: { startTime: 0 },
      isServicesRetrieveError: false
    };
    new ReduxDataHelper(setState)
      .totalItems(3)
      .hostSortField([{ key: 'test', descending: false }])
      .services(services)
      .selectedHostList([])
      .build();
    this.set('closeProperties', function() {
      assert.ok(true);
    });
    await render(hbs`{{host-list/host-table/action-bar closeProperties=closeProperties}}`);
    assert.equal(findAll('.rsa-investigate-query-container__service-selector').length, 1, 'service selector is rendered');
    await click('.rsa-content-tethered-panel-trigger');
    await click('.service-selector-panel li');
  });

  test('Analyze button test for 4.4 hosts', async function(assert) {
    new ReduxDataHelper(setState).selectedHostList([
      { id: '123', version: '4.4', managed: true, scanStatus: 'idle' }]).build();
    await render(hbs`{{host-list/host-table/action-bar}}`);
    assert.equal(document.querySelectorAll('.host-table__toolbar .pivot-to-event-analysis .is-disabled').length, 0, 'analyze events button is enabled');
  });

  test('it render the more actions button', async function(assert) {
    await render(hbs`{{host-list/host-table/action-bar}}`);
    assert.equal(document.querySelector('.host-table__toolbar .host_more_actions button').textContent.trim(), 'More', 'action bar More button label');
    assert.equal(document.querySelectorAll('.host-table__toolbar .host_more_actions .is-disabled').length, 1, 'action bar more button is disabled');
  });

  test('it renders action bar more button when some hosts are selected', async function(assert) {
    new ReduxDataHelper(setState).scanCount(2).build();
    await render(hbs`{{host-list/host-table/action-bar}}`);
    assert.equal(document.querySelector('.host-table__toolbar .host_more_actions button').textContent.trim(), 'More', 'action bar More button label');
    assert.equal(document.querySelectorAll('.host-table__toolbar .host_more_actions .is-disabled').length, 0, 'action bar more button is enabled');
  });
});


