import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { click, find, findAll, render } from '@ember/test-helpers';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { patchFlash } from '../../../../../helpers/patch-flash';
import { patchSocket, throwSocket } from '../../../../../helpers/patch-socket';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

const selectors = {
  componentClass: '.usm-groups-toolbar',
  createNewButton: '.usm-groups-toolbar .groups-create-new-button',
  rankingButton: '.groups-editRanking-button',
  deleteButton: '.usm-groups-toolbar .groups-delete-button',
  applyPoliciesButton: '.usm-groups-toolbar .groups-apply-policies-button',
  publishButton: '.usm-groups-toolbar .groups-publish-button'
};

let setState;

module('Integration | Component | USM Groups Toolbar', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');

    setState = (state) => {
      patchReducer(this, state);
    };
  });

  test('The components appears in the DOM', async function(assert) {
    assert.expect(10);
    new ReduxDataHelper(setState)
      .build();
    await render(hbs`{{usm-groups/groups/toolbar}}`);
    assert.equal(findAll(selectors.componentClass).length, 1, 'The Toolbar component appears in the DOM');
    assert.equal(findAll(selectors.createNewButton).length, 1, 'Create New groups button is showing');
    assert.equal(findAll(selectors.rankingButton).length, 1, 'Ranking button is showing');
    assert.equal(findAll(selectors.deleteButton).length, 1, 'Delete groups button is showing');
    assert.equal(findAll(selectors.applyPoliciesButton).length, 1, 'Apply Policies groups button is showing');
    assert.equal(findAll(selectors.publishButton).length, 1, 'Publish groups button is showing');

    assert.equal(findAll(`${selectors.createNewButton} .rsa-form-button-wrapper:not(.is-disabled)`).length, 1, 'The Create New button is enabled');
    assert.equal(findAll(`${selectors.deleteButton} .rsa-form-button-wrapper.is-disabled`).length, 1, 'The Delete button is disabled');
    assert.equal(findAll(`${selectors.applyPoliciesButton} .rsa-form-button-wrapper.is-disabled`).length, 1, 'The Apply Policies button is disabled');
    assert.equal(findAll(`${selectors.publishButton} .rsa-form-button-wrapper.is-disabled`).length, 1, 'The Publish button is disabled');
  });

  test('Button state when no items are selected', async function(assert) {
    assert.expect(4);
    new ReduxDataHelper(setState)
      .fetchGroups()
      .build();
    await render(hbs`{{usm-groups/groups/toolbar}}`);
    assert.equal(findAll(`${selectors.createNewButton} .rsa-form-button-wrapper:not(.is-disabled)`).length, 1, 'The Create New button is enabled');
    assert.equal(findAll(`${selectors.deleteButton} .rsa-form-button-wrapper.is-disabled`).length, 1, 'The Delete button is disabled');
    assert.equal(findAll(`${selectors.applyPoliciesButton} .rsa-form-button-wrapper.is-disabled`).length, 1, 'The Apply Policies button is disabled');
    assert.equal(findAll(`${selectors.publishButton} .rsa-form-button-wrapper.is-disabled`).length, 1, 'The Publish button is disabled');
  });

  test('Button state when non-dirty publish items are selected', async function(assert) {
    assert.expect(4);
    new ReduxDataHelper(setState)
      .fetchGroups()
      .selectedGroups(['group_001'])
      .build();
    await render(hbs`{{usm-groups/groups/toolbar}}`);
    assert.equal(findAll(`${selectors.createNewButton} .rsa-form-button-wrapper:not(.is-disabled)`).length, 1, 'The Create New button is enabled');
    assert.equal(findAll(`${selectors.deleteButton} .rsa-form-button-wrapper:not(.is-disabled)`).length, 1, 'The Delete button is enabled');
    assert.equal(findAll(`${selectors.applyPoliciesButton} .rsa-form-button-wrapper.is-disabled`).length, 1, 'The Apply Policies button is disabled');
    assert.equal(findAll(`${selectors.publishButton} .rsa-form-button-wrapper.is-disabled`).length, 1, 'The Publish button is disabled');
  });

  test('Button state when dirty publish items are selected', async function(assert) {
    assert.expect(4);
    new ReduxDataHelper(setState)
      .fetchGroups()
      .selectedGroups(['group_001', 'group_002'])
      .build();
    await render(hbs`{{usm-groups/groups/toolbar}}`);
    assert.equal(findAll(`${selectors.createNewButton} .rsa-form-button-wrapper:not(.is-disabled)`).length, 1, 'The Create New button is enabled');
    assert.equal(findAll(`${selectors.deleteButton} .rsa-form-button-wrapper:not(.is-disabled)`).length, 1, 'The Delete button is enabled');
    assert.equal(findAll(`${selectors.applyPoliciesButton} .rsa-form-button-wrapper.is-disabled`).length, 1, 'The Apply Policies button is disabled');
    assert.equal(findAll(`${selectors.publishButton} .rsa-form-button-wrapper:not(.is-disabled)`).length, 1, 'The Publish button is enabled');
  });

  test('Delete confirmation and flash message', async function(assert) {
    const done = assert.async();
    assert.expect(8);
    const translation = this.owner.lookup('service:i18n');
    new ReduxDataHelper(setState)
      .fetchGroups()
      .selectedGroups(['group_001', 'group_003'])
      .build();
    await render(hbs`{{usm-groups/groups usm-groups/groups/toolbar}}`);
    assert.equal(findAll(`${selectors.deleteButton} .rsa-form-button-wrapper:not(.is-disabled)`).length, 1, 'The Delete button is enabled');
    await click(`${selectors.deleteButton} button`);
    const expectedMessage = translation.t('adminUsm.groups.modals.deleteGroups.confirm', { numItems: '2' });
    assert.ok(find('.confirmation-modal'), 'Modal Confirmation is not showing');
    assert.equal(find('.confirmation-modal .modal-content p').textContent.trim(), expectedMessage, 'Confirm message is incorrect');
    patchSocket((method, modelName, query) => {
      assert.equal(method, 'remove');
      assert.equal(modelName, 'groups');
      assert.deepEqual(query, {
        data: ['group_001', 'group_003']
      });
    });
    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedMessage = translation.t('adminUsm.groups.modals.deleteGroups.success', { numItems: '2' });
      assert.equal(flash.type, 'success');
      assert.equal(flash.message.string, expectedMessage);
      done();
    });
    await click('.modal-footer-buttons .is-primary button');
  });

  test('Delete shows an error flash message if the operation fails', async function(assert) {
    const done = assert.async();
    assert.expect(3);
    const translation = this.owner.lookup('service:i18n');
    new ReduxDataHelper(setState)
      .fetchGroups()
      .selectedGroups(['group_001', 'group_003'])
      .build();
    await render(hbs`{{usm-groups/groups usm-groups/groups/toolbar}}`);
    assert.equal(findAll(`${selectors.deleteButton} .rsa-form-button-wrapper:not(.is-disabled)`).length, 1, 'The Delete button is enabled');
    await click(`${selectors.deleteButton} button`);
    throwSocket();
    patchFlash((flash) => {
      const expectedMessage = translation.t('adminUsm.groups.modals.deleteGroups.failure');
      assert.equal(flash.type, 'error');
      assert.equal(flash.message.string, expectedMessage);
      done();
    });
    await click('.modal-footer-buttons .is-primary button');
  });

  test('Publish confirmation and flash message', async function(assert) {
    const done = assert.async();
    assert.expect(8);
    const translation = this.owner.lookup('service:i18n');
    new ReduxDataHelper(setState)
      .fetchGroups()
      .selectedGroups(['group_001', 'group_002'])
      .build();
    await render(hbs`{{usm-groups/groups usm-groups/groups/toolbar}}`);
    assert.equal(findAll(`${selectors.deleteButton} .rsa-form-button-wrapper:not(.is-disabled)`).length, 1, 'The Publish button is enabled');
    await click(`${selectors.publishButton} button`);
    const expectedMessage = translation.t('adminUsm.groups.modals.publishGroups.confirm', { numItems: '1' });
    assert.ok(find('.confirmation-modal'), 'Modal Confirmation is not showing');
    assert.equal(find('.confirmation-modal .modal-content p').textContent.trim(), expectedMessage, 'Confirm message is incorrect');
    patchSocket((method, modelName, query) => {
      assert.equal(method, 'publish');
      assert.equal(modelName, 'groups');
      assert.deepEqual(query, {
        data: ['group_002']
      });
    });
    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedMessage = translation.t('adminUsm.groups.modals.publishGroups.success', { numItems: '2' });
      assert.equal(flash.type, 'success');
      assert.equal(flash.message.string, expectedMessage);
      done();
    });
    await click('.modal-footer-buttons .is-primary button');
  });

  test('Publish shows an error flash message if the operation fails', async function(assert) {
    const done = assert.async();
    assert.expect(3);
    const translation = this.owner.lookup('service:i18n');
    new ReduxDataHelper(setState)
      .fetchGroups()
      .selectedGroups(['group_001', 'group_002'])
      .build();
    await render(hbs`{{usm-groups/groups usm-groups/groups/toolbar}}`);
    assert.equal(findAll(`${selectors.publishButton} .rsa-form-button-wrapper:not(.is-disabled)`).length, 1, 'The Publish button is enabled');
    await click(`${selectors.publishButton} button`);
    throwSocket();
    patchFlash((flash) => {
      const expectedMessage = translation.t('adminUsm.groups.modals.publishGroups.failure');
      assert.equal(flash.type, 'error');
      assert.equal(flash.message.string, expectedMessage);
      done();
    });
    await click('.modal-footer-buttons .is-primary button');
  });

});
