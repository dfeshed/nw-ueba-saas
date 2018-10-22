import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';

let setState;

module('Integration | Component | usm-groups/group-ranking/edit-ranking-step', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('The component appears in the DOM', async function(assert) {
    new ReduxDataHelper(setState).build();
    await render(hbs`{{usm-groups/group-ranking/edit-ranking-step}}`);
    assert.equal(findAll('.edit-ranking-step').length, 1, 'The component appears in the DOM');
  });
  test('Show group list', async function(assert) {
    new ReduxDataHelper(setState).groupRankingWithData().build();
    await render(hbs`{{usm-groups/group-ranking/edit-ranking-step}}`);
    assert.equal(findAll('.edit-ranking-step tr').length, 15, 'All 15 groups are showing');
  });
});
