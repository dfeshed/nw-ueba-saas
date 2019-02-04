import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../helpers/vnext-patch';
import waitForReduxStateChange from '../../../../helpers/redux-async-helpers';
import { initializeGroup } from 'admin-source-management/actions/creators/group-wizard-creators';

let setState, setStateToo, redux;

module('Integration | Component | usm-groups/group-wizard', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.beforeEach(function() {
    // this one is for ReduxDataHelper
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    redux = this.owner.lookup('service:redux');
    // this one is for real redux calls
    setStateToo = (groupId) => {
      redux.dispatch(initializeGroup(groupId));
    };
  });

  test('The component appears in the DOM', async function(assert) {
    new ReduxDataHelper(setState).build();
    this.set('transitionToGroups', () => {});
    await render(hbs`{{usm-groups/group-wizard transitionToGroups=(action transitionToGroups)}}`);
    assert.equal(findAll('.usm-group-wizard').length, 1, 'The component appears in the DOM');
  });

  test('The component renders the rsa-wizard once the fetching of the group succeeds', async function(assert) {
    assert.expect(3);
    setStateToo('group_001'); // this group id exists in mock data
    const fetchGroup = waitForReduxStateChange(redux, 'usm.groupWizard.groupStatus');
    this.set('transitionToGroups', () => {});
    await render(hbs`{{usm-groups/group-wizard transitionToGroups=(action transitionToGroups)}}`);
    await fetchGroup;
    assert.equal(findAll('.usm-group-wizard').length, 1, 'Group Wizard rendered');
    assert.equal(findAll('.usm-group-wizard .rsa-wizard-container').length, 1, 'rsa-wizard rendered within Group Wizard');
    assert.equal(findAll('.usm-group-wizard .error-page').length, 0, 'error-page NOT rendered within Group Wizard');
  });

  test('The component renders the error-page once the fetching of the group fails', async function(assert) {
    assert.expect(3);
    setStateToo('group_blah'); // no such group id exists
    const fetchGroup = waitForReduxStateChange(redux, 'usm.groupWizard.groupStatus');
    this.set('transitionToGroups', () => {});
    await render(hbs`{{usm-groups/group-wizard transitionToGroups=(action transitionToGroups)}}`);
    await fetchGroup;
    assert.equal(findAll('.usm-group-wizard').length, 1, 'Group Wizard rendered');
    assert.equal(findAll('.usm-group-wizard .error-page').length, 1, 'error-page rendered within Group Wizard');
    assert.equal(findAll('.usm-group-wizard .rsa-wizard-container').length, 0, 'rsa-wizard NOT rendered within Group Wizard');
  });

});
