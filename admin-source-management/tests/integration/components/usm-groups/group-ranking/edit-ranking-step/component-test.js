import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render, triggerEvent } from '@ember/test-helpers';
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
    assert.equal(findAll('.edit-ranking-step .group-rank-cell').length, 14, 'All 14 groups are showing');
  });

  test('Show a selected group in the group ranking list', async function(assert) {
    new ReduxDataHelper(setState)
      .groupWiz()
      .groupRankingWithData()
      .selectGroupRanking('Zebra 001')
      .build();
    await render(hbs`{{usm-groups/group-ranking/edit-ranking-step}}`);
    assert.equal(findAll('.edit-ranking-step tr.is-selected').length, 1, 'A group in the group renking list is selected');
  });

  test('Show the wait spinner for group ranking list loading', async function(assert) {
    new ReduxDataHelper(setState)
      .groupWiz()
      .groupRanking('wait')
      .build();
    await render(hbs`{{usm-groups/group-ranking/edit-ranking-step}}`);
    assert.equal(findAll('.edit-ranking-step .loading').length, 1, 'The spinner is showing');
  });

  test('Show the wait spinner for group ranking preview', async function(assert) {
    new ReduxDataHelper(setState)
      .groupWiz()
      .groupRankingPrevListStatus('wait')
      .build();
    await render(hbs`{{usm-groups/group-ranking/edit-ranking-step}}`);
    assert.equal(findAll('.edit-ranking-step .loading-spinner').length, 1, 'The spinner is showing');
  });

  test('Show group list preview toggle and click', async function(assert) {
    new ReduxDataHelper(setState).groupRankingWithData().build();
    await render(hbs`{{usm-groups/group-ranking/edit-ranking-step}}`);
    assert.equal(findAll('.edit-ranking-step .group-preview-cell .x-toggle-container').length, 14, 'All 14 preview toggles are showing');
    await triggerEvent(document.querySelectorAll('.group-preview-cell .x-toggle-container div')[4], 'click');
    assert.equal(findAll('.edit-ranking-step .group-preview-cell .x-toggle-container-checked').length, 1, 'A Toggle is checked');
  });

  test('Show simulation by selected index first', async function(assert) {
    new ReduxDataHelper(setState)
      .groupWiz()
      .groupRankingWithViewData(0)
      .build();
    await render(hbs`{{usm-groups/group-ranking/edit-ranking-step}}`);
    assert.equal(findAll('tr:nth-child(1) .x-toggle-container-checked').length, 1, 'first Toggle is checked');
  });

  test('Show simulation by selected index second', async function(assert) {
    new ReduxDataHelper(setState)
      .groupWiz()
      .groupRankingWithViewData(1)
      .build();
    await render(hbs`{{usm-groups/group-ranking/edit-ranking-step}}`);
    assert.equal(findAll('tr:nth-child(2) .x-toggle-container-checked').length, 1, 'second Toggle is checked');
  });

  test('keypress arrowRight test', async function(assert) {
    const data = [{ name: 'gOne' }, { name: 'gTwo' }];
    const expectedResult = [{ name: 'gOne', isChecked: true }, { name: 'gTwo' }];
    new ReduxDataHelper(setState).groupRankingWithData(data).build();
    await render(hbs`{{usm-groups/group-ranking/edit-ranking-step}}`);
    await triggerEvent(document.querySelectorAll('.group-ranking-table-body tr')[0], 'keydown', { keyCode: 39 });
    const state = this.owner.lookup('service:redux').getState();
    assert.deepEqual(state.usm.groupWizard.groupRanking, expectedResult, 'first group is checked');
  });

  test('keypress arrowLeft test', async function(assert) {
    const data = [{ name: 'gOne', isChecked: true }, { name: 'gTwo' }];
    const expectedResult = [{ name: 'gOne', isChecked: false }, { name: 'gTwo' }];
    new ReduxDataHelper(setState).groupRankingWithData(data).build();
    await render(hbs`{{usm-groups/group-ranking/edit-ranking-step}}`);
    await triggerEvent(document.querySelectorAll('.group-ranking-table-body tr')[0], 'keydown', { keyCode: 37 });
    const state = this.owner.lookup('service:redux').getState();
    assert.deepEqual(state.usm.groupWizard.groupRanking, expectedResult, 'first group is checked to false');
  });

  test('keypress arrowDown test', async function(assert) {
    const data = [{ name: 'gOne' }, { name: 'gTwo' }];
    new ReduxDataHelper(setState).groupRankingWithData(data).build();
    await render(hbs`{{usm-groups/group-ranking/edit-ranking-step}}`);
    await triggerEvent(document.querySelectorAll('.group-ranking-table-body tr')[0], 'keydown', { keyCode: 40 });
    const state = this.owner.lookup('service:redux').getState();
    assert.deepEqual(state.usm.groupWizard.selectedGroupRanking, 'gTwo', 'second group is selected');
  });

  test('keypress arrowUp test', async function(assert) {
    const data = [{ name: 'gOne' }, { name: 'gTwo' }];
    new ReduxDataHelper(setState).groupRankingWithData(data).build();
    await render(hbs`{{usm-groups/group-ranking/edit-ranking-step}}`);
    await triggerEvent(document.querySelectorAll('.group-ranking-table-body tr')[1], 'keydown', { keyCode: 38 });
    const state = this.owner.lookup('service:redux').getState();
    assert.deepEqual(state.usm.groupWizard.selectedGroupRanking, 'gOne', 'first group is selected');
  });

  test('keypress arrowDown + shiftKey test', async function(assert) {
    const data = [{ name: 'gOne' }, { name: 'gTwo' }, { name: 'gThree' }];
    const expectedResult = [{ name: 'gTwo' }, { name: 'gOne' }, { name: 'gThree' }];
    new ReduxDataHelper(setState).groupRankingWithData(data).build();
    await render(hbs`{{usm-groups/group-ranking/edit-ranking-step}}`);
    await triggerEvent(document.querySelectorAll('.group-ranking-table-body tr')[0], 'keydown', { keyCode: 40, shiftKey: true });
    const state = this.owner.lookup('service:redux').getState();
    assert.deepEqual(state.usm.groupWizard.groupRanking, expectedResult, 'first group was moved down to second ranking');
  });

  test('keypress arrowUp + shiftKey test', async function(assert) {
    const data = [{ name: 'gOne' }, { name: 'gTwo' }, { name: 'gThree' }];
    const expectedResult = [{ name: 'gOne' }, { name: 'gThree' }, { name: 'gTwo' }];
    new ReduxDataHelper(setState).groupRankingWithData(data).build();
    await render(hbs`{{usm-groups/group-ranking/edit-ranking-step}}`);
    await triggerEvent(document.querySelectorAll('.group-ranking-table-body tr')[2], 'keydown', { keyCode: 38, shiftKey: true });
    const state = this.owner.lookup('service:redux').getState();
    assert.deepEqual(state.usm.groupWizard.groupRanking, expectedResult, 'third group was moved up to second ranking');
  });

  test('keypress arrowDown + shiftKey + altKey test', async function(assert) {
    const data = [{ name: 'gOne' }, { name: 'gTwo' }, { name: 'gThree' }];
    const expectedResult = [{ name: 'gTwo' }, { name: 'gThree' }, { name: 'gOne' }];
    new ReduxDataHelper(setState).groupRankingWithData(data).build();
    await render(hbs`{{usm-groups/group-ranking/edit-ranking-step}}`);
    // select the first group
    await triggerEvent(document.querySelectorAll('.group-ranking-table-body tr')[1], 'keydown', { keyCode: 38 });
    // moved first group to the bottom
    await triggerEvent(document.querySelectorAll('.group-ranking-table-body tr')[0], 'keydown', { keyCode: 40, shiftKey: true, altKey: true });
    const state = this.owner.lookup('service:redux').getState();
    assert.deepEqual(state.usm.groupWizard.groupRanking, expectedResult, 'first group was moved down to bottom ranking');
  });

  test('keypress arrowUp + shiftKey + altKey test', async function(assert) {
    const data = [{ name: 'gOne' }, { name: 'gTwo' }, { name: 'gThree' }];
    const expectedResult = [{ name: 'gThree' }, { name: 'gOne' }, { name: 'gTwo' }];
    new ReduxDataHelper(setState).groupRankingWithData(data).build();
    await render(hbs`{{usm-groups/group-ranking/edit-ranking-step}}`);
    // select the third group
    await triggerEvent(document.querySelectorAll('.group-ranking-table-body tr')[1], 'keydown', { keyCode: 40 });
    // moved third group to the top
    await triggerEvent(document.querySelectorAll('.group-ranking-table-body tr')[2], 'keydown', { keyCode: 38, shiftKey: true, altKey: true });
    const state = this.owner.lookup('service:redux').getState();
    assert.deepEqual(state.usm.groupWizard.groupRanking, expectedResult, 'third group was moved up to top ranking');
  });

});