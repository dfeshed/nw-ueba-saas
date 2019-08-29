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
    assert.equal(findAll('.reset-ranking-button.is-disabled').length, 1, 'The reset-ranking-button button appears in the DOM and is disabled');
    assert.equal(findAll('.top-ranking-button.is-disabled').length, 1, 'The top-ranking-button button appears in the DOM and is disabled');
    assert.equal(findAll('.edit-ranking-step').length, 1, 'The component appears in the DOM');
  });

  test('Show group list', async function(assert) {
    new ReduxDataHelper(setState).groupRankingWithData().build();
    await render(hbs`{{usm-groups/group-ranking/edit-ranking-step}}`);
    assert.equal(findAll('.edit-ranking-step .group-rank-cell').length, 15, 'All 15 groups are showing');
  });

  test('Show a top selected group in the group ranking list', async function(assert) {
    new ReduxDataHelper(setState)
      .groupWiz()
      .groupRankingWithData()
      .selectGroupRanking('Zebra 001')
      .build();
    await render(hbs`{{usm-groups/group-ranking/edit-ranking-step}}`);
    assert.equal(findAll('.edit-ranking-step tr.is-selected').length, 1, 'A group in the group renking list is selected');
    assert.equal(findAll('.top-ranking-button.is-disabled').length, 1, 'The top-ranking-button button is disabled due to Zebra 001 is top group');
  });

  test('Show a none top selected group in the group ranking list', async function(assert) {
    new ReduxDataHelper(setState)
      .groupWiz()
      .groupRankingWithData()
      .selectGroupRanking('Awesome! 012')
      .build();
    await render(hbs`{{usm-groups/group-ranking/edit-ranking-step}}`);
    assert.equal(findAll('.edit-ranking-step tr.is-selected').length, 1, 'A group in the group renking list is selected');
    assert.equal(findAll('.top-ranking-button.is-disabled').length, 0, 'The top-ranking-button button is enabled due to Awesome! 012 is second from top group');
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
    assert.equal(findAll('.edit-ranking-step .group-preview-cell .float-toggle').length, 14, 'All 14 preview toggles are showing');
    await triggerEvent(document.querySelectorAll('.group-preview-cell .float-toggle')[4], 'click');
    assert.equal(findAll('.edit-ranking-step .group-preview-cell .simulate-true').length, 1, 'A Toggle is checked');
  });

  test('Show simulation by selected index first', async function(assert) {
    new ReduxDataHelper(setState)
      .groupWiz()
      .groupRankingWithViewData(0)
      .build();
    await render(hbs`{{usm-groups/group-ranking/edit-ranking-step}}`);
    assert.equal(findAll('tr:nth-child(1) .simulate-true').length, 1, 'first Toggle is checked');
  });

  test('Show simulation by selected index second', async function(assert) {
    new ReduxDataHelper(setState)
      .groupWiz()
      .groupRankingWithViewData(1)
      .build();
    await render(hbs`{{usm-groups/group-ranking/edit-ranking-step}}`);
    assert.equal(findAll('tr:nth-child(2) .simulate-true').length, 1, 'second Toggle is checked');
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
    assert.equal(findAll('.top-ranking-button.is-disabled').length, 0, 'The top-ranking-button button is enabled due to gTwo is second from top group');
  });

  test('keypress arrowUp test', async function(assert) {
    const data = [{ name: 'gOne' }, { name: 'gTwo' }];
    new ReduxDataHelper(setState).groupRankingWithData(data).build();
    await render(hbs`{{usm-groups/group-ranking/edit-ranking-step}}`);
    await triggerEvent(document.querySelectorAll('.group-ranking-table-body tr')[1], 'keydown', { keyCode: 38 });
    const state = this.owner.lookup('service:redux').getState();
    assert.deepEqual(state.usm.groupWizard.selectedGroupRanking, 'gOne', 'first group is selected');
    assert.equal(findAll('.top-ranking-button.is-disabled').length, 1, 'The top-ranking-button button is disabled due to gOne top group');
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
    assert.equal(findAll('.reset-ranking-button.is-disabled').length, 0, 'The reset-ranking-button is enabled due to ranking change');
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

  test('Focus on sourceTooltip', async function(assert) {
    const translation = this.owner.lookup('service:i18n');
    const data = [{ name: 'gOne' }, { name: 'gTwo' }];
    new ReduxDataHelper(setState).groupRankingWithData(data).build();
    await render(hbs`{{usm-groups/group-ranking/edit-ranking-step}}`);
    const expectedToolTip = translation.t('adminUsm.groupRankingWizard.sourceTooltip');
    await triggerEvent(document.querySelectorAll('.tooltip-text')[0], 'focusIn');
    assert.equal(document.querySelectorAll('.tool-tip-value')[0].innerText.trim(), expectedToolTip.string.trim(), 'Tool tip was activated for sourceTooltip via tab/focusIn');
  });

  test('Focus on previewTooltip', async function(assert) {
    const translation = this.owner.lookup('service:i18n');
    const data = [{ name: 'gOne' }, { name: 'gTwo' }];
    new ReduxDataHelper(setState).groupRankingWithData(data).build();
    await render(hbs`{{usm-groups/group-ranking/edit-ranking-step}}`);
    const expectedToolTip = translation.t('adminUsm.groupRankingWizard.previewTooltip');
    await triggerEvent(document.querySelectorAll('.tooltip-text')[1], 'focusIn');
    assert.equal(document.querySelectorAll('.tool-tip-value')[1].innerText.trim(), expectedToolTip.string.trim(), 'Tool tip was activated for previewTooltip via tab/focusIn');
  });
});