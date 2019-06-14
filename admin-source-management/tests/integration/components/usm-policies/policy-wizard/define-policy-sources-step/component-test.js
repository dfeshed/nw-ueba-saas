import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render, fillIn, triggerEvent, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { selectChoose } from 'ember-power-select/test-support/helpers';
import policyWizardCreators from 'admin-source-management/actions/creators/policy-wizard-creators';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import sinon from 'sinon';

let setState, updatePolicyPropertySpy;
const spys = [];
const sources = [ { fileType: 'apache', fileEncoding: 'UTF-8', enabled: true, startOfEvents: false, sourceName: 'apache-server-1', exclusionFilters: ['abc', 'def'] } ];

module('Integration | Component | usm-policies/policy-wizard/define-policy-sources-step', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.before(function() {
    spys.push(updatePolicyPropertySpy = sinon.spy(policyWizardCreators, 'updatePolicyProperty'));
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });
  hooks.afterEach(function() {
    spys.forEach((s) => s.resetHistory());
  });

  hooks.after(function() {
    spys.forEach((s) => s.restore());
  });

  test('The component appears in the DOM', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSources([])
      .build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-sources-step}}`);
    assert.equal(findAll('.define-policy-sources-step').length, 1, 'The component appears in the DOM');
  });

  test('should render the root file-source-parent-container component', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSources([])
      .build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-sources-step}}`);
    assert.equal(findAll('.file-source-parent-container').length, 1, 'expected to have file-source-parent-container root input element in DOM');
  });

  test('should not render the body-cell child-source-container component when sources is empty', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSources([])
      .build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-sources-step}}`);
    assert.equal(findAll('.child-source-container').length, 0, 'body cell child-source-container is not rendered when souces is empty');
    assert.equal(findAll('.file-source-type').length, 1, 'file-source-type dropdown is rendered when souces is empty');
    assert.equal(findAll('.add-row').length, 1, 'add-row button is rendered when sources is empty');
  });

  test('should render the body-cell child-source-container component when sources is populated', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSources(sources)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-sources-step}}`);
    assert.equal(findAll('.child-source-container').length, 6, 'body cell child-source-containers are rendered correctly');
    assert.equal(findAll('.file-source-type').length, 1, 'file-source-type dropdown is rendered correctly');
    assert.equal(findAll('.add-row').length, 1, 'add-row button is rendered correctly');
  });

  test('should render the body-cell child-source-container component when except exclusionFilters is empty and other sources are populated', async function(assert) {
    const emptyExF = [ { fileType: 'apache', fileEncoding: 'UTF-8', enabled: true, startOfEvents: false, sourceName: 'apache-server-1', exclusionFilters: [] } ];
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSources(emptyExF)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-sources-step}}`);
    assert.equal(findAll('.child-source-container').length, 6, 'body cell child-source-containers are rendered correctly');
    assert.equal(findAll('.file-source-type').length, 1, 'file-source-type dropdown is rendered correctly');
    assert.equal(findAll('.add-row').length, 1, 'add-row button is rendered correctly');
    assert.equal(findAll('.child-source-container .exclusionFilters').length, 1, 'Exclusion filters rendered correctly when null');
  });

  test('should render the all the components within body-cell child-source-container component', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSources(sources)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-sources-step}}`);
    assert.equal(findAll('.child-source-container').length, 6, 'body cell child-source-containers are rendered correctly');
    assert.equal(findAll('.subheading').length, 5, 'All the subheadings rendered correctly');
    assert.equal(findAll('.rsa-form-radio-group-label').length, 2, 'All the radio labels rendered correctly');
    assert.equal(findAll('.file-type').length, 1, 'file type dropdown rendered correctly');
    assert.equal(findAll('.child-source-container .enabled').length, 1, 'Enable on Agent radio rendered correctly');
    assert.equal(findAll('.child-source-container .startOfEvents').length, 1, 'Data Collection radio rendered correctly');
    assert.equal(findAll('.child-source-container .sourceName').length, 1, 'Source Name input rendered correctly');
    assert.equal(findAll('.child-source-container .exclusionFilters').length, 1, 'Source Name input rendered correctly');
  });

  test('It triggers the update policy action creator when the main fileType is changed', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSourceTypes()
      .policyWizFileSources([])
      .build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-sources-step}}`);
    await selectChoose('.file-source-type__list', '.ember-power-select-option', 2);
    assert.equal(updatePolicyPropertySpy.callCount, 1, 'Update policy property action creator was called once');
  });

  test('It triggers the update policy action creator when the file encoding is changed', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSourceTypes()
      .policyWizFileSources(sources)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-sources-step}}`);
    await selectChoose('.file-encoding', '.ember-power-select-option', 2);
    assert.equal(updatePolicyPropertySpy.callCount, 1, 'Update policy property action creator was called once');
  });

  test('It triggers the update policy action creator when the source name is changed', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSourceTypes()
      .policyWizFileSources(sources)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-sources-step}}`);
    const value = 'new-server';
    const [eventIdEl] = findAll('.source-name input');
    await fillIn(eventIdEl, value);
    await triggerEvent(eventIdEl, 'blur');
    assert.equal(updatePolicyPropertySpy.callCount, 1, 'Update policy property action creator was called once');
  });

  test('It triggers the update policy action creator when the exclusion filter is changed', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSourceTypes()
      .policyWizFileSources(sources)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-sources-step}}`);
    const value = 'filter-1, filter-2';
    const [eventIdEl] = findAll('.exclusion-filters textarea');
    await fillIn(eventIdEl, value);
    await triggerEvent(eventIdEl, 'blur');
    assert.equal(updatePolicyPropertySpy.callCount, 1, 'Update policy property action creator was called once');
  });

  test('It triggers the update policy action creator when the enabled radio button is changed', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSourceTypes()
      .policyWizFileSources(sources)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-sources-step}}`);
    const radioBtn = document.querySelector('.enabled .rsa-form-radio-wrapper:nth-of-type(2) input');
    await click(radioBtn);
    assert.equal(updatePolicyPropertySpy.callCount, 1, 'Update policy property action creator was called once');
  });

  test('It triggers the update policy action creator when the data collection radio button is changed', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSourceTypes()
      .policyWizFileSources(sources)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-sources-step}}`);
    const radioBtn = document.querySelector('.startOfEvents .rsa-form-radio-wrapper:nth-of-type(1) input');
    await click(radioBtn);
    assert.equal(updatePolicyPropertySpy.callCount, 1, 'Update policy property action creator was called once');
  });

  test('It triggers the update policy action creator when a new file type is added', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSourceTypes()
      .policyWizFileSources(sources)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-sources-step}}`);
    const btn = document.querySelector('.file-source-type__list .add-row button');
    await click(btn);
    assert.equal(updatePolicyPropertySpy.callCount, 1, 'Update policy property action creator was called once');
  });

  test('It triggers the update policy action creator when an existing file type is removed', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSourceTypes()
      .policyWizFileSources(sources)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-sources-step}}`);
    const btn = document.querySelector('.remove-setting .rsa-icon');
    await click(btn);
    assert.equal(updatePolicyPropertySpy.callCount, 1, 'Update policy property action creator was called once');
  });
});
