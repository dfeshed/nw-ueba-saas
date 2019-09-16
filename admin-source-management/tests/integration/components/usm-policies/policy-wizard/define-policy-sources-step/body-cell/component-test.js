import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { click, find, findAll, render, fillIn, triggerEvent } from '@ember/test-helpers';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../../helpers/vnext-patch';
import waitForReduxStateChange, { waitForReduxStateToEqual } from '../../../../../../helpers/redux-async-helpers';
import { encodingOptions } from 'admin-source-management/components/usm-policies/policy-wizard/define-policy-sources-step/cell-settings';
import {
  fileSources,
  fileSourceById,
  fileSourceExclusionFilters
} from 'admin-source-management/reducers/usm/policy-wizard/filePolicy/file-selectors';

let redux, setState;

const itemId = 0; // really the array index (for now anyway)
const sources = [{
  fileType: 'apache',
  fileTypePrettyName: 'Apache Web Server',
  enabled: true,
  startOfEvents: false,
  fileEncoding: 'UTF-8 / ASCII', // Local Encoding
  paths: ['path-1', 'path-2'],
  sourceName: 'apache-server-1',
  exclusionFilters: ['filter-1', 'filter-2']
}];

module('Integration | Component | usm-policies/policy-wizard/define-policy-sources-step/body-cell', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    setState = (state) => {
      patchReducer(this, state);
      redux = this.owner.lookup('service:redux');
    };
  });

  test('it renders define-policy-sources-step/body-cell component', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSources(sources)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-sources-step/body-cell}}`);
    assert.equal(findAll('.child-source-container').length, 1, 'Expected to define-policy-sources-step/body-cell element in DOM.');
  });

  test('enabled (enableOnAgent) is true, and triggers updatePolicyFileSourceProperty action creator when changed', async function(assert) {
    const initialState = new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSourceTypes()
      .policyWizFileSources(sources)
      .build();

    const column = {
      field: 'enabled',
      title: 'adminUsm.policyWizard.filePolicy.enableOnAgent',
      width: '30vw',
      displayType: 'enabledRadio',
      component: 'usm-policies/policy-wizard/define-policy-sources-step/body-cell'
    };

    this.setProperties({
      column,
      encodingOptions,
      itemId
    });

    await render(hbs`
      {{usm-policies/policy-wizard/define-policy-sources-step/body-cell
        column=column
        encodingOptions=encodingOptions
        itemId=itemId
      }}
    `);

    const field = 'enabled';
    const initialValue = fileSourceById(initialState, itemId)[field];
    // initial
    assert.equal(findAll('.enabled').length, 1, 'enabled radios container rendered');
    assert.equal(findAll('.enabled input').length, 2, 'enabled radio buttons rendered');
    assert.equal(initialValue, true, 'enabled is initially true');
    assert.equal(findAll('.enabled input')[0].checked, initialValue, `initial enabled radio is ${initialValue}`);
    assert.equal(findAll('.enabled input')[1].checked, !initialValue, `initial disabled radio is ${!initialValue}`);
    // updated
    const expectedValue = false;
    const [, disableRadio] = findAll('.enabled input');
    // const onChange = waitForReduxStateChange(redux, `usm.policyWizard.policy.sources.${itemId}.${field}`);
    // using waitForReduxStateToEqual instead as waitForReduxStateChange doesn't play nice with falsy value
    const onChange = waitForReduxStateToEqual(this.owner, `usm.policyWizard.policy.sources.${itemId}.${field}`, expectedValue);
    await click(disableRadio);
    await onChange;
    const actualValue = fileSourceById(redux.getState(), itemId)[field];
    assert.equal(actualValue, expectedValue, `enabled is updated to ${expectedValue}`);
    assert.equal(findAll('.enabled input')[0].checked, expectedValue, `updated enabled radio is ${expectedValue}`);
    assert.equal(findAll('.enabled input')[1].checked, !expectedValue, `updated disabled radio is ${!expectedValue}`);
  });

  test('startOfEvents (dataCollection) is false, and triggers updatePolicyFileSourceProperty action creator when changed', async function(assert) {
    const initialState = new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSourceTypes()
      .policyWizFileSources(sources)
      .build();

    const column = {
      field: 'startOfEvents',
      title: 'adminUsm.policyWizard.filePolicy.dataCollection',
      width: '30vw',
      displayType: 'eventsRadio',
      component: 'usm-policies/policy-wizard/define-policy-sources-step/body-cell'
    };

    this.setProperties({
      column,
      encodingOptions,
      itemId
    });

    await render(hbs`
      {{usm-policies/policy-wizard/define-policy-sources-step/body-cell
        column=column
        encodingOptions=encodingOptions
        itemId=itemId
      }}
    `);

    const field = 'startOfEvents';
    const initialValue = fileSourceById(initialState, itemId)[field];
    // initial
    assert.equal(findAll('.startOfEvents').length, 1, 'startOfEvents radios container rendered');
    assert.equal(findAll('.startOfEvents input').length, 2, 'startOfEvents radio buttons rendered');
    assert.equal(initialValue, false, 'startOfEvents is initially false');
    assert.equal(findAll('.startOfEvents input')[0].checked, !initialValue, `new only radio is ${!initialValue}`);
    assert.equal(findAll('.startOfEvents input')[1].checked, initialValue, `historical & new radio is ${initialValue}`);
    // updated
    const expectedValue = true;
    const [, historicalAndNewRadio] = findAll('.startOfEvents input');
    const onChange = waitForReduxStateChange(redux, `usm.policyWizard.policy.sources.${itemId}.${field}`);
    await click(historicalAndNewRadio);
    await onChange;
    const actualValue = fileSourceById(redux.getState(), itemId)[field];
    assert.equal(actualValue, expectedValue, `startOfEvents is updated to ${expectedValue}`);
    assert.equal(findAll('.startOfEvents input')[0].checked, !expectedValue, `updated new only radio is ${!expectedValue}`);
    assert.equal(findAll('.startOfEvents input')[1].checked, expectedValue, `updated historical & new radio is ${expectedValue}`);
  });

  test('directory paths is displayed in the container, and triggers updatePolicyFileSourceProperty action creator when changed', async function(assert) {
    const initialState = new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSourceTypes()
      .policyWizFileSources(sources)
      .build();

    const column = {
      field: 'paths',
      title: 'adminUsm.policyWizard.filePolicy.logFilePath',
      width: '100%',
      displayType: 'paths',
      component: 'usm-policies/policy-wizard/define-policy-sources-step/body-cell'
    };

    this.setProperties({
      column,
      encodingOptions,
      itemId
    });

    await render(hbs`
      {{usm-policies/policy-wizard/define-policy-sources-step/body-cell
        column=column
        encodingOptions=encodingOptions
        itemId=itemId
      }}
    `);

    const field = 'paths';
    const [initialValue0, initialValue1] = fileSourceById(initialState, itemId)[field];
    // initial
    assert.equal(findAll('.paths').length, 1, 'Directory paths are rendered');
    assert.equal(findAll('.paths .directory-path input')[0].value, initialValue0, `initial paths[0] is ${initialValue0}`);
    assert.equal(findAll('.paths .directory-path input')[1].value, initialValue1, `initial paths[1] is ${initialValue1}`);
    // updated
    const expectedValue0 = `${initialValue0}/Updated/0`;
    const expectedValue1 = `${initialValue1}/Updated/1`;
    const [input0, input1] = findAll('.paths .directory-path input');
    // wait on the 2nd change
    let onChange = waitForReduxStateChange(redux, `usm.policyWizard.policy.sources.${itemId}.${field}.1`);
    await fillIn(input0, expectedValue0);
    await triggerEvent(input0, 'blur');
    await fillIn(input1, expectedValue1);
    await triggerEvent(input1, 'blur');
    await onChange;
    const [actualValue0, actualValue1] = fileSourceById(redux.getState(), itemId)[field];
    assert.equal(actualValue0, expectedValue0, `${field}[0] updated from ${initialValue0} to ${actualValue0}`);
    assert.equal(actualValue1, expectedValue1, `${field}[1] updated from ${initialValue1} to ${actualValue1}`);
    assert.equal(findAll('.paths .directory-path input')[0].value, expectedValue0, `updated paths[0] is ${expectedValue0}`);
    assert.equal(findAll('.paths .directory-path input')[1].value, expectedValue1, `updated paths[1] is ${expectedValue1}`);
    // add a path
    const addPath = find('.paths .add-path .add-directory-path');
    onChange = waitForReduxStateChange(redux, `usm.policyWizard.policy.sources.${itemId}.${field}`);
    await click(addPath);
    await onChange;
    let updatedPaths = fileSourceById(redux.getState(), itemId)[field];
    assert.equal(updatedPaths.length, 3, `added a path... ${field}.length is 3`);
    assert.equal(findAll('.paths .directory-path input').length, 3, `added a path... 3 ${field} rendered`);
    // remove a path
    const [,, deletePath] = findAll('.paths .delete-button');
    onChange = waitForReduxStateChange(redux, `usm.policyWizard.policy.sources.${itemId}.${field}`);
    await click(deletePath);
    await onChange;
    updatedPaths = fileSourceById(redux.getState(), itemId)[field];
    assert.equal(updatedPaths.length, 2, `removed a path... ${field}.length is 2`);
    assert.equal(findAll('.paths .directory-path input').length, 2, `removed a path... 2 ${field} rendered`);
  });

  test('exclusion filter is displayed in the text area container, and triggers updatePolicyFileSourceProperty action creator when changed', async function(assert) {
    const initialState = new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSourceTypes()
      .policyWizFileSources(sources)
      .build();

    const column = {
      field: 'exclusionFilters',
      title: 'adminUsm.policyWizard.filePolicy.exclusionFilters',
      width: '30vw',
      displayType: 'exclusionFilters',
      component: 'usm-policies/policy-wizard/define-policy-sources-step/body-cell'
    };

    this.setProperties({
      column,
      itemId
    });

    await render(hbs`
      {{usm-policies/policy-wizard/define-policy-sources-step/body-cell
        column=column
        sourceUpdated=sourceUpdated
        itemId=itemId
      }}
    `);

    const field = 'exclusionFilters';
    const initialValue = fileSourceExclusionFilters(initialState, itemId);
    // initial
    assert.equal(findAll('.exclusion-filters').length, 1);
    assert.equal(findAll('.exclusion-filters textarea')[0].value, initialValue, `initial exclusionFilters are ${initialValue}`);
    // updated
    const expectedValue = `${initialValue}\nfilter-3\nfilter-4`;
    const [inputEl] = findAll('.exclusion-filters textarea');
    const onChange = waitForReduxStateChange(redux, `usm.policyWizard.policy.sources.${itemId}.${field}`);
    await fillIn(inputEl, expectedValue);
    await triggerEvent(inputEl, 'blur');
    await onChange;
    const actualValue = fileSourceExclusionFilters(redux.getState(), itemId);
    assert.equal(actualValue, expectedValue, `${field} updated from ${initialValue} to ${actualValue}`);
    assert.equal(findAll('.exclusion-filters textarea')[0].value, expectedValue, `updated exclusionFilters are ${expectedValue}`);
  });

  test('file type is displayed in the container', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSourceTypes()
      .policyWizFileSources(sources)
      .build();

    const column = {
      field: 'fileType',
      title: 'adminUsm.policyWizard.filePolicy.logFileType',
      width: '30vw',
      displayType: 'fileTypeInput',
      component: 'usm-policies/policy-wizard/define-policy-sources-step/body-cell'
    };

    this.setProperties({
      column,
      encodingOptions,
      itemId
    });

    await render(hbs`
      {{usm-policies/policy-wizard/define-policy-sources-step/body-cell
        column=column
        encodingOptions=encodingOptions
        itemId=itemId
      }}
    `);
    assert.equal(find('.ember-power-select-selected-item').textContent.trim(), 'Apache Web Server', 'selected item matches apache');
    assert.equal(findAll('.file-type .ember-power-select-trigger[aria-disabled=true]').length, 1, 'File type power-select control appears in the DOM and is disabled');
  });

  test('remove button triggers removePolicyFileSource action creator when clicked', async function(assert) {
    const initialState = new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSourceTypes()
      .policyWizFileSources(sources)
      .build();

    // using fileType because the remove button is rendered with it
    const column = {
      field: 'fileType',
      title: 'adminUsm.policyWizard.filePolicy.logFileType',
      width: '30vw',
      displayType: 'fileTypeInput',
      component: 'usm-policies/policy-wizard/define-policy-sources-step/body-cell'
    };

    this.setProperties({
      column,
      encodingOptions,
      itemId
    });

    await render(hbs`
      {{usm-policies/policy-wizard/define-policy-sources-step/body-cell
        column=column
        encodingOptions=encodingOptions
        itemId=itemId
      }}
    `);

    const initialValue = fileSources(initialState);
    // initial
    assert.equal(initialValue.length, sources.length, `initial sources length ${initialValue.length}`);
    assert.deepEqual(initialValue, sources, 'initial sources are as expected');
    // updated
    const expectedValue = [];
    const [removeBtn] = findAll('.remove-setting');
    const onChange = waitForReduxStateChange(redux, 'usm.policyWizard.policy.sources');
    await click(removeBtn);
    await onChange;
    const actualValue = fileSources(redux.getState());
    assert.equal(actualValue.length, expectedValue.length, `updated sources length ${actualValue.length}`);
    assert.deepEqual(actualValue, expectedValue, 'updated sources are as expected');
  });

});
