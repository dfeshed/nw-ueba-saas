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
  componentClass: '.usm-policies-toolbar',
  createNewButton: '.usm-policies-toolbar .policies-create-new-button',
  deleteButton: '.usm-policies-toolbar .policies-delete-button',
  publishButton: '.usm-policies-toolbar .policies-publish-button'
};

let setState;

module('Integration | Component | USM Policies Toolbar', function(hooks) {
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
    assert.expect(7);
    new ReduxDataHelper(setState)
      .build();
    await render(hbs`{{usm-policies/policies/toolbar}}`);
    assert.equal(findAll(selectors.componentClass).length, 1, 'The Toolbar component appears in the DOM');
    assert.equal(findAll(selectors.createNewButton).length, 1, 'Create New policy button is showing');
    assert.equal(findAll(selectors.deleteButton).length, 1, 'Delete policies button is showing');
    assert.equal(findAll(selectors.publishButton).length, 1, 'Publish policies button is showing');

    assert.equal(findAll(`${selectors.createNewButton} .rsa-form-button-wrapper:not(.is-disabled)`).length, 1, 'The Create New button is enabled');
    assert.equal(findAll(`${selectors.deleteButton} .rsa-form-button-wrapper.is-disabled`).length, 1, 'The Delete button is disabled');
    assert.equal(findAll(`${selectors.publishButton} .rsa-form-button-wrapper.is-disabled`).length, 1, 'The Publish button is disabled');
  });

  test('Button state when no items are selected', async function(assert) {
    assert.expect(3);
    new ReduxDataHelper(setState)
      .fetchPolicies()
      .build();
    await render(hbs`{{usm-policies/policies/toolbar}}`);
    assert.equal(findAll(`${selectors.createNewButton} .rsa-form-button-wrapper:not(.is-disabled)`).length, 1, 'The Create New button is enabled');
    assert.equal(findAll(`${selectors.deleteButton} .rsa-form-button-wrapper.is-disabled`).length, 1, 'The Delete button is disabled');
    assert.equal(findAll(`${selectors.publishButton} .rsa-form-button-wrapper.is-disabled`).length, 1, 'The Publish button is disabled');
  });

  test('Button state when non-dirty publish items are selected', async function(assert) {
    assert.expect(3);
    new ReduxDataHelper(setState)
      .fetchPolicies()
      .selectedPolicies(['policy_014'])
      .build();
    await render(hbs`{{usm-policies/policies/toolbar}}`);
    assert.equal(findAll(`${selectors.createNewButton} .rsa-form-button-wrapper:not(.is-disabled)`).length, 1, 'The Create New button is enabled');
    assert.equal(findAll(`${selectors.deleteButton} .rsa-form-button-wrapper:not(.is-disabled)`).length, 1, 'The Delete button is enabled');
    assert.equal(findAll(`${selectors.publishButton} .rsa-form-button-wrapper.is-disabled`).length, 1, 'The Publish button is disabled');
  });

  test('Button state when dirty publish items are selected', async function(assert) {
    assert.expect(3);
    new ReduxDataHelper(setState)
      .fetchPolicies()
      .selectedPolicies(['policy_001'])
      .build();
    await render(hbs`{{usm-policies/policies/toolbar}}`);
    assert.equal(findAll(`${selectors.createNewButton} .rsa-form-button-wrapper:not(.is-disabled)`).length, 1, 'The Create New button is enabled');
    assert.equal(findAll(`${selectors.deleteButton} .rsa-form-button-wrapper:not(.is-disabled)`).length, 1, 'The Delete button is enabled');
    assert.equal(findAll(`${selectors.publishButton} .rsa-form-button-wrapper:not(.is-disabled)`).length, 1, 'The Publish button is enabled');
  });

  test('Button state when default policy is selected with others', async function(assert) {
    assert.expect(3);
    new ReduxDataHelper(setState)
      .fetchPolicies()
      .selectedPolicies(['__default_edr_policy', 'policy_001', 'policy_002'])
      .build();
    await render(hbs`{{usm-policies/policies/toolbar}}`);
    assert.equal(findAll(`${selectors.createNewButton} .rsa-form-button-wrapper:not(.is-disabled)`).length, 1, 'The Create New button is enabled');
    assert.equal(findAll(`${selectors.deleteButton} .rsa-form-button-wrapper:not(.is-disabled)`).length, 1, 'The Delete button is enabled');
    assert.equal(findAll(`${selectors.publishButton} .rsa-form-button-wrapper:not(.is-disabled)`).length, 1, 'The Publish button is enabled');
  });

  test('Button state when only default policy is selected', async function(assert) {
    assert.expect(3);
    new ReduxDataHelper(setState)
      .fetchPolicies()
      .selectedPolicies(['__default_edr_policy'])
      .build();
    await render(hbs`{{usm-policies/policies/toolbar}}`);
    assert.equal(findAll(`${selectors.createNewButton} .rsa-form-button-wrapper:not(.is-disabled)`).length, 1, 'The Create New button is enabled');
    assert.equal(findAll(`${selectors.deleteButton} .rsa-form-button-wrapper.is-disabled`).length, 1, 'The Delete button is disabled');
    assert.equal(findAll(`${selectors.publishButton} .rsa-form-button-wrapper.is-disabled`).length, 1, 'The Publish button is disabled');
  });

  test('Delete confirmation and flash message', async function(assert) {
    const done = assert.async();
    assert.expect(8);
    const translation = this.owner.lookup('service:i18n');
    new ReduxDataHelper(setState)
      .fetchPolicies()
      .selectedPolicies(['policy_001', 'policy_003'])
      .build();
    await render(hbs`{{usm-policies/policies usm-policies/policies/toolbar}}`);
    assert.equal(findAll(`${selectors.deleteButton} .rsa-form-button-wrapper:not(.is-disabled)`).length, 1, 'The Delete button is enabled');
    await click(`${selectors.deleteButton} button`);
    const expectedMessage = translation.t('adminUsm.policies.modals.deletePolicies.confirm', { numItems: '2' });
    assert.ok(find('.confirmation-modal'), 'Modal Confirmation is not showing');
    assert.equal(find('.confirmation-modal .modal-content p').textContent.trim(), expectedMessage, 'Confirm message is incorrect');
    patchSocket((method, modelName, query) => {
      assert.equal(method, 'remove');
      assert.equal(modelName, 'policy');
      assert.deepEqual(query, {
        data: ['policy_001', 'policy_003']
      });
    });
    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedMessage = translation.t('adminUsm.policies.modals.deletePolicies.success', { numItems: '2' });
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
      .fetchPolicies()
      .selectedPolicies(['policy_001', 'policy_003'])
      .build();
    await render(hbs`{{usm-policies/policies usm-policies/policies/toolbar}}`);
    assert.equal(findAll(`${selectors.deleteButton} .rsa-form-button-wrapper:not(.is-disabled)`).length, 1, 'The Delete button is enabled');
    await click(`${selectors.deleteButton} button`);
    throwSocket();
    patchFlash((flash) => {
      const expectedMessage = translation.t('adminUsm.policies.modals.deletePolicies.failure');
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
      .fetchPolicies()
      .selectedPolicies(['policy_001', 'policy_002'])
      .build();
    await render(hbs`{{usm-policies/policies usm-policies/policies/toolbar}}`);
    assert.equal(findAll(`${selectors.deleteButton} .rsa-form-button-wrapper:not(.is-disabled)`).length, 1, 'The Publish button is enabled');
    await click(`${selectors.publishButton} button`);
    const expectedMessage = translation.t('adminUsm.policies.modals.publishPolicies.confirm', { numItems: '2' });
    assert.ok(find('.confirmation-modal'), 'Modal Confirmation is not showing');
    assert.equal(find('.confirmation-modal .modal-content p').textContent.trim(), expectedMessage, 'Confirm message is incorrect');
    patchSocket((method, modelName, query) => {
      assert.equal(method, 'publish');
      assert.equal(modelName, 'policy');
      assert.deepEqual(query, {
        data: ['policy_001', 'policy_002']
      });
    });
    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedMessage = translation.t('adminUsm.policies.modals.publishPolicies.success', { numItems: '2' });
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
      .fetchPolicies()
      .selectedPolicies(['policy_001', 'policy_002'])
      .build();
    await render(hbs`{{usm-policies/policies usm-policies/policies/toolbar}}`);
    assert.equal(findAll(`${selectors.publishButton} .rsa-form-button-wrapper:not(.is-disabled)`).length, 1, 'The Publish button is enabled');
    await click(`${selectors.publishButton} button`);
    throwSocket();
    patchFlash((flash) => {
      const expectedMessage = translation.t('adminUsm.policies.modals.publishPolicies.failure');
      assert.equal(flash.type, 'error');
      assert.equal(flash.message.string, expectedMessage);
      done();
    });
    await click('.modal-footer-buttons .is-primary button');
  });

});
