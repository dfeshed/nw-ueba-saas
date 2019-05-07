import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render } from '@ember/test-helpers';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import edrTestPolicy from '../../../../../data/subscriptions/groups/fetchRankingView/data';
import windowsTestPolicy from '../../../../../data/subscriptions/groups/fetchRankingView/dataWindow';
import fileTestPolicy from '../../../../../data/subscriptions/groups/fetchRankingView/dataFile';

let setState;

module('Integration | Component | group-ranking/inspector | Policy Inspector', function(hooks) {
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
    await render(hbs`{{usm-groups/group-ranking/inspector}}`);
    assert.equal(findAll('.usm-ranking-inspector').length, 1, 'The component appears in the DOM');
  });

  test('It shows the common sections for history and groups for edrPolicy', async function(assert) {
    new ReduxDataHelper(setState)
      .selectedSourceType('edrPolicy')
      .focusedPolicy(edrTestPolicy)
      .build();
    await render(hbs`{{usm-groups/group-ranking/inspector}}`);
    assert.equal(findAll('.usm-ranking-inspector .heading').length, 6, 'expected headings are shown');
    assert.equal(findAll('.usm-ranking-inspector .title').length, 15, 'expected property names are shown');
    assert.equal(findAll('.usm-ranking-inspector .value').length, 42, 'expected value elements are shown');
  });

  test('It shows the common sections for history and groups for windowPolicy', async function(assert) {
    new ReduxDataHelper(setState)
      .selectedSourceType('windowsLogPolicy')
      .focusedPolicy(windowsTestPolicy)
      .build();
    await render(hbs`{{usm-groups/group-ranking/inspector}}`);
    assert.equal(findAll('.usm-ranking-inspector .heading').length, 2, 'expected headings are shown');
    assert.equal(findAll('.usm-ranking-inspector .title').length, 4, 'expected property names are shown');
    assert.equal(findAll('.usm-ranking-inspector .value').length, 12, 'expected value elements are shown');
  });

  test('It shows the common sections for history and groups for filePolicy', async function(assert) {
    new ReduxDataHelper(setState)
      .selectedSourceType('filePolicy')
      .focusedPolicy(fileTestPolicy)
      .build();
    await render(hbs`{{usm-groups/group-ranking/inspector}}`);
    assert.equal(findAll('.usm-ranking-inspector .heading').length, 1, 'expected headings are shown');
    assert.equal(findAll('.usm-ranking-inspector .title').length, 5, 'expected property names are shown');
    assert.equal(findAll('.usm-ranking-inspector .value').length, 12, 'expected value elements are shown');
  });
});