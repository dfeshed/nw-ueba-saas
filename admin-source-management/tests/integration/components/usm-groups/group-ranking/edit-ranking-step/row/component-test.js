import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../../helpers/vnext-patch';

let setState;

module('Integration | Component | usm-groups/group-ranking/edit-ranking-step/row', function(hooks) {
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
    new ReduxDataHelper(setState)
      .groupWiz()
      .groupRankingWithData()
      .build();
    await render(hbs`{{usm-groups/group-ranking/edit-ranking-step/row}}`);
    assert.equal(findAll('tr').length, 1, 'The component appears in the DOM');
  });

  test('The component click on the row', async function(assert) {
    new ReduxDataHelper(setState)
      .groupWiz()
      .groupRankingWithData()
      .build();
    await render(hbs`{{usm-groups/group-ranking/edit-ranking-step/row}}`);
    await click('tr');
    assert.equal(findAll('tr.is-selected').length, 1, 'The row is selected');
  });
});
