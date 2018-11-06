import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';

let setState;

module('Integration | Component | usm-groups/group-ranking/group-toolbar', function(hooks) {
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
    const state = new ReduxDataHelper(setState).groupWiz().build();
    this.set('step', state.usm.groupWizard.rankingSteps[0]);
    await render(hbs`{{usm-groups/group-ranking/group-toolbar step=step}}`);
    assert.equal(findAll('.group-wizard-toolbar').length, 1, 'The component appears in the DOM');
  });

  test('Toolbar appearance for choose source step with invalid data', async function(assert) {
    const state = new ReduxDataHelper(setState).groupWiz().build();
    this.set('step', state.usm.groupWizard.rankingSteps[0]);
    await render(hbs`{{usm-groups/group-ranking/group-toolbar step=step}}`);
    assert.equal(findAll('.prev-button').length, 0, 'The Previous button does NOT appear in the DOM');
    assert.equal(findAll('.next-button').length, 1, 'The Next button appears in the DOM');
    assert.equal(findAll('.next-button.is-disabled').length, 1, 'The Next button is disabled');
    assert.equal(findAll('.publish-button').length, 0, 'The Publish button does NOT appear in the DOM');
    assert.equal(findAll('.cancel-button:not(.is-disabled)').length, 1, 'The Cancel button appears in the DOM and is enabled');
  });

  test('The component next button', async function(assert) {
    const state = new ReduxDataHelper(setState).groupWiz().groupRanking('complete').build();
    this.set('step', state.usm.groupWizard.rankingSteps[0]);
    await render(hbs`{{usm-groups/group-ranking/group-toolbar step=step}}`);
    assert.equal(findAll('.next-button.is-disabled').length, 0, 'The Next button is NOT disabled');
    assert.equal(findAll('.prev-button').length, 0, 'The Previous button does NOT appears in the DOM');
    assert.equal(findAll('.cancel-button').length, 1, 'The cancel-button button appears in the DOM');
  });

  test('Toolbar Previous/next button test fron second atep', async function(assert) {
    const state = new ReduxDataHelper(setState).groupWiz().build();
    this.set('step', state.usm.groupWizard.rankingSteps[1]);
    await render(hbs`{{usm-groups/group-ranking/group-toolbar step=step}}`);
    assert.equal(findAll('.prev-button').length, 1, 'The Previous button appears in the DOM');
    assert.equal(findAll('.next-button').length, 0, 'The Next button does NOT appear in the DOM');

    assert.equal(findAll('.reset-ranking-button.is-disabled').length, 1, 'The reset-ranking-button button appears in the DOM and is disabled');
    assert.equal(findAll('.top-ranking-button.is-disabled').length, 1, 'The top-ranking-button button appears in the DOM and is disabled');
    assert.equal(findAll('.publish-button.is-disabled').length, 1, 'The publish-button button appears in the DOM and is disabled');
    assert.equal(findAll('.cancel-button:not(.is-disabled)').length, 1, 'The Cancel button appears in the DOM and is enabled');
  });

  test('Toolbar buttons test fron second atep and data changes', async function(assert) {
    const state = new ReduxDataHelper(setState).groupWiz().groupRankingWithData().build();
    this.set('step', state.usm.groupWizard.rankingSteps[1]);
    await render(hbs`{{usm-groups/group-ranking/group-toolbar step=step}}`);
    assert.equal(findAll('.reset-ranking-button:not(.is-disabled)').length, 1, 'The reset-ranking-button button appears in the DOM and is enabled');
    assert.equal(findAll('.top-ranking-button.is-disabled').length, 1, 'The top-ranking-button button appears in the DOM and is disabled');
    assert.equal(findAll('.publish-button:not(.is-disabled)').length, 1, 'The publish-button button appears in the DOM and is enabled');
  });

  test('Toolbar top ranking button test fron second atep and selected group', async function(assert) {
    const state = new ReduxDataHelper(setState)
      .groupWiz()
      .groupRankingWithData()
      .selectGroupRanking('Zebra 001')
      .build();
    this.set('step', state.usm.groupWizard.rankingSteps[1]);
    await render(hbs`{{usm-groups/group-ranking/group-toolbar step=step}}`);
    assert.equal(findAll('.reset-ranking-button:not(.is-disabled)').length, 1, 'The reset-ranking-button button appears in the DOM and is enabled');
    assert.equal(findAll('.top-ranking-button:not(.is-disabled)').length, 1, 'The top-ranking-button button appears in the DOM and is enabled');
    assert.equal(findAll('.publish-button:not(.is-disabled)').length, 1, 'The publish-button button appears in the DOM and is enabled');
  });


});

