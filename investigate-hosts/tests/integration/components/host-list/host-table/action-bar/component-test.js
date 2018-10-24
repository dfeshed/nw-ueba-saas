import { module, skip, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, settled, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { patchFlash } from '../../../../../helpers/patch-flash';
import Immutable from 'seamless-immutable';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let setState;

module('Integration | Component | host table action bar', function(hooks) {
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
    assert.equal(document.querySelector('.host-table__toolbar .host-start-scan-button button').textContent.trim(), 'Start Scan', 'action bar start button label');
    assert.equal(document.querySelectorAll('.host-table__toolbar .host-start-scan-button > .is-disabled').length, 1, 'action bar start button is disabled');
  });

  test('it renders action bar stop button', async function(assert) {
    await render(hbs`{{host-list/host-table/action-bar}}`);
    assert.equal(document.querySelector('.host-table__toolbar .stop-scan-button button').textContent.trim(), 'Stop Scan', 'action bar stop button label');
    assert.equal(document.querySelectorAll('.host-table__toolbar .stop-scan-button > .is-disabled').length, 1, 'action bar stop button is disabled');
  });

  test('it renders action bar delete button', async function(assert) {
    await render(hbs`{{host-list/host-table/action-bar}}`);
    assert.equal(document.querySelector('.host-table__toolbar .delete-host-button button').textContent.trim(), 'Delete', 'action bar stop button label');
    assert.equal(document.querySelectorAll('.host-table__toolbar .delete-host-button > .is-disabled').length, 1, 'action bar delete button is disabled');
  });

  test('it renders action bar export to csv button', async function(assert) {
    await render(hbs`{{host-list/host-table/action-bar}}`);
    assert.equal(document.querySelectorAll('.host-table__toolbar  div:nth-of-type(5) .is-disabled').length, 1, 'action bar export to csv button is disabled by default');
  });

  test('it renders action bar delete button', async function(assert) {
    await render(hbs`{{host-list/host-table/action-bar}}`);
    assert.equal(document.querySelector('.host-table__toolbar-buttons div:nth-child(5) button').textContent.trim(), 'Delete', 'action bar delete button label');
    assert.equal(document.querySelectorAll('.host-table__toolbar div:nth-of-type(4) .is-disabled').length, 1, 'action bar delete button is disabled');
  });

  test('it renders action bar start button when some hosts are selected', async function(assert) {
    new ReduxDataHelper(setState).scanCount(2).build();
    await render(hbs`{{host-list/host-table/action-bar}}`);
    assert.equal(document.querySelector('.host-table__toolbar .host-start-scan-button button').textContent.trim(), 'Start Scan', 'action bar start button label');
    assert.equal(document.querySelectorAll('.host-table__toolbar .host-start-scan-button > .is-disabled').length, 0, 'action bar start button is enabled');
  });

  test('it renders action bar stop button when some hosts are selected', async function(assert) {
    new ReduxDataHelper(setState).scanCount(2).build();
    await render(hbs`{{host-list/host-table/action-bar}}`);
    assert.equal(document.querySelector('.host-table__toolbar .stop-scan-button button').textContent.trim(), 'Stop Scan', 'action bar stop button label');
    assert.equal(document.querySelectorAll('.host-table__toolbar .stop-scan-button > .is-disabled').length, 0, 'action bar stop button is enabled');
  });

  test('it renders action bar delete button when some hosts are selected', async function(assert) {
    new ReduxDataHelper(setState).scanCount(2).build();
    await render(hbs`{{host-list/host-table/action-bar}}`);
    assert.equal(document.querySelector('.host-table__toolbar .delete-host-button button').textContent.trim(), 'Delete', 'action bar stop button label');
    assert.equal(document.querySelectorAll('.host-table__toolbar .delete-host-button > .is-disabled').length, 0, 'action bar delete button is enabled');
  });

  test('it renders confirmation modal when delete button is clicked with some hosts selected', async function(assert) {
    new ReduxDataHelper(setState).scanCount(2).build();
    await render(hbs`{{host-list/host-table/action-bar}}`);
    assert.equal(document.querySelectorAll('.host-table__toolbar .delete-host-button > .is-disabled').length, 0, 'action bar delete button is enabled');
    assert.equal(document.querySelectorAll('#modalDestination .confirmation-modal').length, 0, 'confirmation modal is not present'); // Modal content not be added to dom
    this.$('.host-table__toolbar .delete-host-button button').trigger('click');
    return settled().then(() => {
      assert.equal(document.querySelectorAll('#modalDestination.active .confirmation-modal').length, 1, 'delete hosts confirmation modal');
      assert.equal(document.querySelector('#modalDestination .confirmation-modal h3').textContent.trim(), 'Delete 2 host(s)', 'confirmation modal title');
      assert.equal(document.querySelector('#modalDestination .confirmation-modal .modal-footer-buttons div:last-child button').textContent.trim(), 'Yes', 'Yes button label');
      click('.confirmation-modal .modal-footer-buttons div:last-child button');
      return settled().then(() => {
        assert.equal(document.querySelectorAll('#modalDestination.active .confirmation-modal').length, 0, 'delete hosts confirmation modal is closed');
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
      const translation = this.owner.lookup('service:i18n');
      const expectedMsg = translation.t('investigateHosts.hosts.moreActions.notAnEcatAgent');
      assert.equal(flash.type, 'error');
      assert.equal(flash.message.string, expectedMsg);
    });
    this.render(hbs`{{host-list/host-table/action-bar}}`);
    click('.host-table__toolbar-buttons div:nth-child(4) button');
  });
});


