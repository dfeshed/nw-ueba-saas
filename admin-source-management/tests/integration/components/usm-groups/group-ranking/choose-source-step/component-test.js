import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';

let setState;

module('Integration | Component | usm-groups/group-ranking/choose-source-step', function(hooks) {
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
    await render(hbs`{{usm-groups/group-ranking/choose-source-step}}`);
    assert.equal(findAll('.choose-source-step').length, 1, 'The component appears in the DOM');
    assert.equal(findAll('.source-type-selector').length, 1, 'The source-type-selector appears in the DOM');
    assert.equal(findAll('.loading').length, 1, 'The loading section appears in the DOM');
  });
  test('The component wait', async function(assert) {
    new ReduxDataHelper(setState).groupWiz().groupRanking('wait').build();
    await render(hbs`{{usm-groups/group-ranking/choose-source-step}}`);
    assert.equal(findAll('.loading-spinner').length, 1, 'The spinner appears in the DOM');
  });
  test('The component error', async function(assert) {
    new ReduxDataHelper(setState).groupWiz().groupRanking('error').build();
    await render(hbs`{{usm-groups/group-ranking/choose-source-step}}`);
    assert.equal(findAll('.loading-error').length, 1, 'The error appears in the DOM');
  });
});
