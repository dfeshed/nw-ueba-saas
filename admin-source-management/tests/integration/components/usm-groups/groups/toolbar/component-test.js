import Immutable from 'seamless-immutable';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { click, find, findAll, render } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import RSVP from 'rsvp';
import { patchSocket, throwSocket } from '../../../../../helpers/patch-socket';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { patchFlash } from '../../../../../helpers/patch-flash';
import { initialState as _initialState } from 'admin-source-management/reducers/usm/groups-reducers';
import { getGroups } from 'admin-source-management/actions/creators/groups-creators';
import { merge } from '@ember/polyfills';

const initialState = {
  ..._initialState
};

const groupsState = {
  items: [
    {
      id: 'group_001',
      name: 'Group 001',
      description: 'Group 001 description',
      dirty: false
    },
    {
      id: 'group_002',
      name: 'Group 002',
      description: 'Group 002 description',
      dirty: false
    },
    {
      id: 'group_003',
      name: 'Group 003',
      description: 'Group 003 description',
      dirty: false
    }
  ],
  itemsStatus: 'complete',
  itemsSelected: [],
  isSelectAll: false,
  itemsTotal: 3
};

let init, setState;

module('Integration | Component | USM Groups Toolbar', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');

    setState = (state = initialState) => {
      const fullState = {
        usm: {
          groups: {
            ...state
          }
        }
      };
      patchReducer(this, Immutable.from(fullState));
      // initialize all of the required data into redux app state
      const redux = this.owner.lookup('service:redux');
      init = RSVP.allSettled([
        redux.dispatch(getGroups())
      ]);
    };

  });

  const selectors = {
    componentClass: '.usm-groups-toolbar',
    createNewButton: '.usm-groups-toolbar .groups-create-new-button',
    deleteButton: '.usm-groups-toolbar .groups-delete-button',
    applyPoliciesButton: '.usm-groups-toolbar .groups-apply-policies-button',
    publishButton: '.usm-groups-toolbar .groups-publish-button'
  };

  test('The components appears in the DOM', async function(assert) {
    setState({ ...initialState });
    await init;
    await render(hbs`{{usm-groups/groups/toolbar}}`);
    assert.equal(findAll(selectors.componentClass).length, 1, 'The Toolbar component appears in the DOM');
    assert.equal(findAll(selectors.createNewButton).length, 1, 'Create New groups button is showing');
    assert.equal(findAll(selectors.deleteButton).length, 1, 'Delete groups button is showing');
    assert.equal(findAll(selectors.applyPoliciesButton).length, 1, 'Apply Policies groups button is showing');
    assert.equal(findAll(selectors.publishButton).length, 1, 'Publish groups button is showing');

    assert.equal(findAll(`${selectors.createNewButton} .rsa-form-button-wrapper:not(.is-disabled)`).length, 1, 'The Create New button is enabled');
    assert.equal(findAll(`${selectors.deleteButton} .rsa-form-button-wrapper.is-disabled`).length, 1, 'The Delete button is disabled');
    assert.equal(findAll(`${selectors.applyPoliciesButton} .rsa-form-button-wrapper.is-disabled`).length, 1, 'The Apply Policies button is disabled');
    assert.equal(findAll(`${selectors.publishButton} .rsa-form-button-wrapper.is-disabled`).length, 1, 'The Publish button is disabled');

  });

  test('Button state when no items are selected', async function(assert) {
    const testState = merge(initialState, groupsState);
    setState(...testState);
    await init;
    await render(hbs`{{usm-groups/groups/toolbar itemsSelected=itemsSelected}}`);
    assert.equal(findAll(`${selectors.createNewButton} .rsa-form-button-wrapper:not(.is-disabled)`).length, 1, 'The Create New button is enabled');
    assert.equal(findAll(`${selectors.deleteButton} .rsa-form-button-wrapper.is-disabled`).length, 1, 'The Delete button is disabled');
    assert.equal(findAll(`${selectors.applyPoliciesButton} .rsa-form-button-wrapper.is-disabled`).length, 1, 'The Apply Policies button is disabled');
    assert.equal(findAll(`${selectors.publishButton} .rsa-form-button-wrapper.is-disabled`).length, 1, 'The Publish button is disabled');
  });

  test('Button state when non-dirty publish items are selected', async function(assert) {
    const testState = merge(merge(initialState, groupsState), {
      itemsSelected: ['group_001', 'group_003']
    });
    setState(...testState);
    await init;
    await render(hbs`{{usm-groups/groups usm-groups/groups/toolbar}}`);
    assert.equal(findAll(`${selectors.createNewButton} .rsa-form-button-wrapper:not(.is-disabled)`).length, 1, 'The Create New button is enabled');
    assert.equal(findAll(`${selectors.deleteButton} .rsa-form-button-wrapper:not(.is-disabled)`).length, 1, 'The Delete button is enabled');
    assert.equal(findAll(`${selectors.applyPoliciesButton} .rsa-form-button-wrapper.is-disabled`).length, 1, 'The Apply Policies button is disabled');
    assert.equal(findAll(`${selectors.publishButton} .rsa-form-button-wrapper.is-disabled`).length, 1, 'The Publish button is disabled');
  });

  test('Delete confirmation and flash message', async function(assert) {
    const translation = this.owner.lookup('service:i18n');
    const testState = merge(merge(initialState, groupsState), {
      itemsSelected: ['group_001', 'group_003']
    });
    setState(...testState);
    await init;
    await render(hbs`{{usm-groups/groups usm-groups/groups/toolbar}}`);
    assert.equal(findAll(`${selectors.deleteButton} .rsa-form-button-wrapper:not(.is-disabled)`).length, 1, 'The Delete button is enabled');
    await click('.groups-delete-button button');
    const expectedMessage = translation.t('adminUsm.groups.modals.deleteGroups.confirm', { numItems: '2' });
    assert.ok(find('.groups-delete-button .confirmation-modal'), 'Modal Confirmation is not showing');
    assert.equal(find('.groups-delete-button .confirmation-modal .modal-content p').textContent.trim(), expectedMessage, 'Confirm message is incorrect');
    patchSocket((method, modelName, query) => {
      assert.equal(method, 'remove');
      assert.equal(modelName, 'groups');
      assert.deepEqual(query, {
        data: { groupIds: ['group_001', 'group_003'] }
      });
    });
    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedMessage = translation.t('adminUsm.groups.modals.deleteGroups.success', { numItems: '2' });
      assert.equal(flash.type, 'success');
      assert.equal(flash.message.string, expectedMessage);
    });
    await click('.modal-footer-buttons .is-primary button');
  });

  test('Delete shows an error flash message if the operation fails', async function(assert) {
    const testState = merge(merge(initialState, groupsState), {
      itemsSelected: ['group_001', 'group_003']
    });
    setState(...testState);
    await init;
    await render(hbs`{{usm-groups/groups usm-groups/groups/toolbar}}`);
    assert.equal(findAll(`${selectors.deleteButton} .rsa-form-button-wrapper:not(.is-disabled)`).length, 1, 'The Delete button is enabled');
    await click('.groups-delete-button button');
    throwSocket();
    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedMessage = translation.t('adminUsm.groups.modals.deleteGroups.failure');
      assert.equal(flash.type, 'error');
      assert.equal(flash.message.string, expectedMessage);
    });
    await click('.modal-footer-buttons .is-primary button');
  });

});
