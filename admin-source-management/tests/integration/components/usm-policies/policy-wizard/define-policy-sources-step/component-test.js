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

let setState, updatePolicyPropertySpy, addPolicyFileSourceSpy;
const spys = [];
const sources = [ { fileType: 'apache', fileEncoding: 'UTF-8 / ASCII', enabled: true, startOfEvents: true, sourceName: 'apache-server-1', exclusionFilters: ['abc', 'def'], paths: ['path1', 'path2'] } ];

module('Integration | Component | usm-policies/policy-wizard/define-policy-sources-step', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.before(function() {
    spys.push(updatePolicyPropertySpy = sinon.spy(policyWizardCreators, 'updatePolicyProperty'));
    spys.push(addPolicyFileSourceSpy = sinon.spy(policyWizardCreators, 'addPolicyFileSource'));
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

  test('Add Selected File Type should be disabled when sources array is empty', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSources([])
      .build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-sources-step}}`);
    assert.equal(findAll('.file-source-type').length, 1, 'file-source-type dropdown is rendered when sources are empty');
  });

  test('should render the body-cell child-source-container component when sources is populated', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSources(sources)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-sources-step}}`);
    assert.equal(findAll('.child-source-container').length, 6, '6 (5 body cell & 1 advanced cell) child-source-containers are rendered correctly');
    assert.equal(findAll('.file-source-type').length, 1, 'file-source-type dropdown is rendered correctly');
    assert.equal(findAll('.add-row').length, 1, 'add-row button is rendered correctly');
  });

  test('should render the body-cell child-source-container component when except exclusionFilters is empty and other sources are populated', async function(assert) {
    const emptyExF = [ { fileType: 'apache', fileEncoding: 'UTF-8 / ASCII', enabled: true, startOfEvents: false, sourceName: 'apache-server-1', exclusionFilters: [] } ];
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSources(emptyExF)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-sources-step}}`);
    assert.equal(findAll('.child-source-container').length, 6, '6 (5 body cell & 1 advanced cell) child-source-containers are rendered correctly');
    assert.equal(findAll('.file-source-type').length, 1, 'file-source-type dropdown is rendered correctly');
    assert.equal(findAll('.add-row').length, 1, 'add-row button is rendered correctly');
    assert.equal(findAll('.child-source-container.exclusionFilters').length, 1, 'Exclusion filters rendered correctly when null');
  });

  test('should render the all the components within body-cell child-source-container component', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSources(sources)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-sources-step}}`);
    assert.equal(findAll('.child-source-container').length, 6, 'body cell 6 (5 body cell & 1 advanced cell) child-source-containers are rendered correctly');
    assert.equal(findAll('.subheading').length, 6, 'All the subheadings rendered correctly');
    assert.equal(findAll('.rsa-form-radio-group-label').length, 2, 'All the radio labels rendered correctly');
    assert.equal(findAll('.file-type').length, 1, 'file type dropdown rendered correctly');
    assert.equal(findAll('.child-source-container.enabled').length, 1, 'Enable on Agent radio rendered correctly');
    assert.equal(findAll('.child-source-container.startOfEvents').length, 1, 'Data Collection radio rendered correctly');
    assert.equal(findAll('.child-source-container.exclusionFilters').length, 1, 'Source Name input rendered correctly');
    assert.equal(findAll('.child-source-container.paths').length, 1, 'Directory paths rendered correctly');
    assert.equal(findAll('.child-source-container .sourceName').length, 1, 'Source Name input rendered correctly');
    assert.equal(findAll('.child-source-container .fileEncoding').length, 1, 'Source Name input rendered correctly');
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

  test('It shows the correct error message when the source name is invalid', async function(assert) {
    const translation = this.owner.lookup('service:i18n');
    const expectedMessage = translation.t('adminUsm.policyWizard.filePolicy.invalidSourceName');
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSourceTypes()
      .policyWizFileSources(sources)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-sources-step}}`);
    let value = 'invalidsourceName$';
    const [eventIdEl] = findAll('.source-name input');
    await fillIn(eventIdEl, value);
    await triggerEvent(eventIdEl, 'blur');
    const isErrorClass = findAll('.source-name .is-error');
    assert.equal(isErrorClass.length, 1, 'is-error class is rendered');
    // await pauseTest();
    assert.equal(findAll('.source-name .input-error')[0].innerText, expectedMessage, `Correct error message is showing: ${expectedMessage}`);

    // valid source name
    value = 'validsourcename';
    await fillIn(eventIdEl, value);
    await triggerEvent(eventIdEl, 'blur');
    assert.equal(document.querySelectorAll('.source-name .is-error').length, 0, 'Error is not showing for valid source name');
    assert.equal(document.querySelectorAll('.source-name .input-error').length, 0, 'Error message is not showing for valid source name');
  });

  test('It shows the correct error message when the directory path is invalid', async function(assert) {
    const translation = this.owner.lookup('service:i18n');
    const expectedMessage = translation.t('adminUsm.policyWizard.filePolicy.invalidDirPath');
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSourceTypes()
      .policyWizFileSources(sources)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-sources-step}}`);
    let value = '';
    const [eventIdEl] = findAll('.directory-path input');
    await fillIn(eventIdEl, value);
    await triggerEvent(eventIdEl, 'blur');
    const isErrorClass = findAll('.directory-path .is-error');
    assert.equal(isErrorClass.length, 1, 'is-error class is rendered');
    assert.equal(findAll('.directory-path .input-error')[0].innerText, expectedMessage, `Correct error message is showing: ${expectedMessage}`);

    // valid source name
    value = 'path1';
    await fillIn(eventIdEl, value);
    await triggerEvent(eventIdEl, 'blur');
    assert.equal(document.querySelectorAll('.directory-path .is-error').length, 0, 'Error is not showing for valid source name');
    assert.equal(document.querySelectorAll('.directory-path .input-error').length, 0, 'Error message is not showing for valid source name');
  });

  test('It shows the correct error message when the directory path has angle brackets', async function(assert) {
    const invalidSource = [ { fileType: 'apache', fileEncoding: 'UTF-8 / ASCII', enabled: true, startOfEvents: true, sourceName: 'apache-server-1', exclusionFilters: ['[', 'def'], paths: ['<invalid>', 'path2'] } ];
    const translation = this.owner.lookup('service:i18n');
    const expectedMessage = translation.t('adminUsm.policyWizard.filePolicy.invalidPathAngleBrackets');
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSourceTypes()
      .policyWizFileSources(invalidSource)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-sources-step}}`);
    const isErrorClass = findAll('.directory-path .is-error');
    assert.equal(isErrorClass.length, 1, 'is-error class is rendered');
    assert.equal(findAll('.directory-path .input-error')[0].innerText, expectedMessage, `Correct error message is showing: ${expectedMessage}`);
  });

  test('It shows the correct error message when the exclusion filter is invalid', async function(assert) {
    const invalidSource = [ { fileType: 'apache', fileEncoding: 'UTF-8 / ASCII', enabled: true, startOfEvents: true, sourceName: 'apache-server-1', exclusionFilters: ['[', 'def'], paths: ['path1', 'path2'] } ];
    const translation = this.owner.lookup('service:i18n');
    const expectedMessage = translation.t('adminUsm.policyWizard.filePolicy.exclusionFiltersSyntaxError');
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSourceTypes()
      .policyWizFileSources(invalidSource)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-sources-step}}`);
    const isErrorClass = findAll('.exclusion-filters .is-error');
    assert.equal(isErrorClass.length, 1, 'is-error class is rendered');
    assert.equal(findAll('.exclusion-filter-error')[0].innerText, `${expectedMessage}1`, `Correct error message is showing: ${expectedMessage}`);
  });

  test('It shows the correct error message when the exclusion filter has empty lines', async function(assert) {
    const invalidSource = [ { fileType: 'apache', fileEncoding: 'UTF-8 / ASCII', enabled: true, startOfEvents: true, sourceName: 'apache-server-1', exclusionFilters: ['abc', '', 'def'], paths: ['path1', 'path2'] } ];
    const translation = this.owner.lookup('service:i18n');
    const expectedMessage = translation.t('adminUsm.policyWizard.filePolicy.exclusionFiltersEmptyLines');
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSourceTypes()
      .policyWizFileSources(invalidSource)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-sources-step}}`);
    const isErrorClass = findAll('.exclusion-filters .is-error');
    assert.equal(isErrorClass.length, 1, 'is-error class is rendered');
    assert.equal(findAll('.exclusion-filter-error')[0].innerText, expectedMessage, `Correct error message is showing: ${expectedMessage}`);
  });

  test('It shows the correct error message when the number of exclusion filters are invalid', async function(assert) {
    const invalidSource = [ { fileType: 'apache', fileEncoding: 'UTF-8 / ASCII', enabled: true, startOfEvents: true, sourceName: 'apache-server-1', exclusionFilters: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16', '17'], paths: ['path1', 'path2'] } ];
    const translation = this.owner.lookup('service:i18n');
    const expectedMessage = translation.t('adminUsm.policyWizard.filePolicy.exclusionFiltersLengthError');
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSourceTypes()
      .policyWizFileSources(invalidSource)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-sources-step}}`);
    const isErrorClass = findAll('.exclusion-filters .is-error');
    assert.equal(isErrorClass.length, 1, 'is-error class is rendered');
    assert.equal(findAll('.exclusion-filter-error')[0].innerText, expectedMessage, `Correct error message is showing: ${expectedMessage}`);
  });

  test('It does not show the error message when the exclusion filter is valid', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSourceTypes()
      .policyWizFileSources(sources)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-sources-step}}`);
    assert.equal(findAll('.exclusion-filter-error')[0].getAttribute('value'), null, 'Error is not showing for a valid filter');
  });

  test('It shows the correct error message when the directory path array does not have atleast one path', async function(assert) {
    const newSource = [ { fileType: 'apache', fileEncoding: 'UTF-8 / ASCII', enabled: true, startOfEvents: true, sourceName: 'apache-server-1', exclusionFilters: ['abc', 'def'], paths: [] } ];
    const translation = this.owner.lookup('service:i18n');
    const expectedMessage = translation.t('adminUsm.policyWizard.filePolicy.dirPathEmpty');
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSourceTypes()
      .policyWizFileSources(newSource)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-sources-step}}`);
    assert.equal(findAll('.paths .no-path')[0].innerText, expectedMessage, `Correct error message is showing: ${expectedMessage}`);
  });

  test('It triggers the addPolicyFileSource action creator when a new file type is added', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSourceTypes()
      .policyWizFileSources(sources)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-sources-step}}`);
    const btn = document.querySelector('.file-source-type__list .add-row button');
    await click(btn);
    assert.equal(addPolicyFileSourceSpy.callCount, 1, 'Update policy property action creator was called once');
  });

});
