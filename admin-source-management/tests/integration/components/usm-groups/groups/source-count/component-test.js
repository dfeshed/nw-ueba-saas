import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import waitForReduxStateChange from '../../../../../helpers/redux-async-helpers';
import { initializeGroups } from 'admin-source-management/actions/creators/groups-creators';

let setState, redux;

module('Integration | Component | usm-groups/groups/source-count', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.beforeEach(function() {
    localStorage.clear();
    initialize(this.owner);
    redux = this.owner.lookup('service:redux');
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = () => {
      redux.dispatch(initializeGroups());
    };
  });

  test('The component appears in the DOM', async function(assert) {
    setState();
    const getItems = waitForReduxStateChange(redux, 'usm.groups.items');
    await getItems;
    await render(hbs`{{usm-groups/groups/source-count}}`);
    assert.equal(findAll('.table-cell-text').length, 1, 'The component appears in the DOM');
  });
});
