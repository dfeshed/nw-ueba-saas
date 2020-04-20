import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { click, findAll, render, triggerEvent } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import fetchPolicyList from '../../../../../data/subscriptions/policy/fetchPolicyList/index';

let setState;
const policyListPayload = fetchPolicyList.message().data;

module('Integration | Component | usm-groups/group-ranking/choose-source-step', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n', 'service:features');
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

  test('The source type select control should have the correct options', async function(assert) {
    const features = this.owner.lookup('service:features');
    new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizPolicyList(policyListPayload)
      .groupWizPolicyListStatus('complete')
      .build();

    // allowFilePolicies enabled so filePolicy type should be enabled
    features.setFeatureFlags({ 'rsa.usm.allowFilePolicies': true });
    await render(hbs`{{usm-groups/group-ranking/choose-source-step}}`);
    await click('.source-type-selector .ember-power-select-trigger');
    let sourceTypesAll = findAll('.ember-power-select-option');
    let sourceTypesDisabled = findAll('.ember-power-select-option[aria-disabled=true]');
    assert.equal(sourceTypesAll.length, 3, 'All source types rendered');
    assert.equal(sourceTypesDisabled.length, 0, 'All source types enabled');

    // allowFilePolicies disabled so filePolicy type should be disabled
    features.setFeatureFlags({ 'rsa.usm.allowFilePolicies': false });
    await render(hbs`{{usm-groups/group-ranking/choose-source-step}}`);
    await click('.source-type-selector .ember-power-select-trigger');
    sourceTypesAll = findAll('.ember-power-select-option');
    sourceTypesDisabled = findAll('.ember-power-select-option[aria-disabled=true]');
    assert.equal(sourceTypesAll.length, 3, 'All source types rendered');
    assert.equal(sourceTypesDisabled.length, 1, '2 source types enabled, and 1 source type disabled');
    // the filePolicy option should have a tooltip to show why it is disabled
    const expectedFilePolicyDisabledTooltip = 'Endpoint servers need to be on version 11.4 or above to configure log file collection.';
    await triggerEvent('.ember-power-select-option[aria-disabled=true] .tooltip-text', 'mouseover');
    const actualFilePolicyDisabledTooltip = findAll('.tool-tip-value')[0].innerText.trim();
    assert.equal(actualFilePolicyDisabledTooltip, expectedFilePolicyDisabledTooltip, 'disabled filePolicy option tooltip is as expected');
  });

});
