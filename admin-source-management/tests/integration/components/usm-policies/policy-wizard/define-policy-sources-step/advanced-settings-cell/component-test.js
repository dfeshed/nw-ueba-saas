import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { find, findAll, render, fillIn, triggerEvent } from '@ember/test-helpers';
import { clickTrigger, selectChoose } from 'ember-power-select/test-support/helpers';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../../helpers/vnext-patch';
import waitForReduxStateChange from '../../../../../../helpers/redux-async-helpers';
import { DEFAULT_ENCODING, encodingOptions } from 'admin-source-management/components/usm-policies/policy-wizard/define-policy-sources-step/cell-settings';
import {
  fileSourceById
} from 'admin-source-management/reducers/usm/policy-wizard/filePolicy/file-selectors';

let redux, setState;

const itemId = 0; // really the array index (for now anyway)
const sources = [{
  fileType: 'apache',
  fileTypePrettyName: 'Apache Web Server',
  enabled: true,
  startOfEvents: false,
  fileEncoding: DEFAULT_ENCODING, // 'UTF-8 / ASCII',
  paths: ['path-1', 'path-2'],
  sourceName: 'apache-server-1',
  exclusionFilters: ['filter-1', 'filter-2']
}];
const column = {
  field: 'advancedSettingsCell', title: 'adminUsm.policyWizard.filePolicy.advancedSettings', width: '30vw', component: 'usm-policies/policy-wizard/define-policy-sources-step/advanced-settings-cell',
  config: [
    { field: 'sourceName', title: 'adminUsm.policyWizard.filePolicy.sourceName', width: '30vw', displayType: 'sourceNameInput' },
    { field: 'fileEncoding', title: 'adminUsm.policyWizard.filePolicy.fileEncoding', width: '30vw', displayType: 'fileEncoding' }
  ]
};

module('Integration | Component | usm-policies/policy-wizard/define-policy-sources-step/advanced-settings-cell', function(hooks) {
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

  test('it renders define-policy-sources-step/advanced-settings-cell component', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSourceTypes()
      .policyWizFileSources(sources)
      .build();
    await render(hbs`
      {{usm-policies/policy-wizard/define-policy-sources-step/advanced-settings-cell
        column=column
        encodingOptions=encodingOptions
        itemId=itemId
      }}
    `);
    assert.equal(findAll('.child-source-container').length, 1, 'Expected to define-policy-sources-step/advanced-settings-cell element in DOM.');
  });

  test('there should be 16 dropdown options available for file encoding', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSourceTypes()
      .policyWizFileSources(sources)
      .build();

    this.setProperties({
      column,
      encodingOptions,
      itemId
    });

    await render(hbs`
      {{usm-policies/policy-wizard/define-policy-sources-step/advanced-settings-cell
        column=column
        encodingOptions=encodingOptions
        itemId=itemId
      }}
    `);
    await clickTrigger('.file-encoding');
    assert.equal(findAll('.ember-power-select-option').length, 16, 'Dropdown is rendered with correct number of items');
  });

  test('UTF-8 / ASCII should be selected in the file encoding dropdown', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSourceTypes()
      .policyWizFileSources(sources)
      .build();

    this.setProperties({
      column,
      encodingOptions,
      itemId
    });

    await render(hbs`
      {{usm-policies/policy-wizard/define-policy-sources-step/advanced-settings-cell
        column=column
        encodingOptions=encodingOptions
        itemId=itemId
      }}
    `);
    assert.equal(find('.ember-power-select-selected-item').textContent.trim(), 'UTF-8 / ASCII');
  });

  test('changing the file encoding triggers updatePolicyFileSourceProperty action creator', async function(assert) {
    const initialState = new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSourceTypes()
      .policyWizFileSources(sources)
      .build();

    this.setProperties({
      column,
      encodingOptions,
      itemId
    });

    await render(hbs`
      {{usm-policies/policy-wizard/define-policy-sources-step/advanced-settings-cell
        column=column
        encodingOptions=encodingOptions
        itemId=itemId
      }}
    `);

    const field = 'fileEncoding';
    const initialValue = fileSourceById(initialState, itemId)[field];
    const expectedValue = 'Local Encoding';
    const onChange = waitForReduxStateChange(redux, `usm.policyWizard.policy.sources.${itemId}.${field}`);
    await selectChoose('.file-encoding', expectedValue);
    await onChange;
    const actualValue = fileSourceById(redux.getState(), itemId)[field];
    assert.equal(actualValue, expectedValue, `${field} updated from ${initialValue} to ${actualValue}`);
    assert.equal(find('.ember-power-select-selected-item').textContent.trim(), expectedValue, `selected item matches ${expectedValue}`);
  });

  test('source name is displayed in the container, and triggers updatePolicyFileSourceProperty action creator when changed', async function(assert) {
    const initialState = new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSourceTypes()
      .policyWizFileSources(sources)
      .build();

    this.setProperties({
      column,
      encodingOptions,
      itemId
    });

    await render(hbs`
      {{usm-policies/policy-wizard/define-policy-sources-step/advanced-settings-cell
        column=column
        encodingOptions=encodingOptions
        itemId=itemId
      }}
    `);

    const field = 'sourceName';
    const initialValue = fileSourceById(initialState, itemId)[field];
    // initial
    assert.equal(findAll('.source-name').length, 1);
    assert.equal(findAll('.source-name input')[0].value, initialValue, `initial sourceName is ${initialValue}`);
    // updated
    const expectedValue = `${initialValue}-Updated`;
    const [inputEl] = findAll('.source-name input');
    const onChange = waitForReduxStateChange(redux, `usm.policyWizard.policy.sources.${itemId}.${field}`);
    await fillIn(inputEl, expectedValue);
    await triggerEvent(inputEl, 'blur');
    await onChange;
    const actualValue = fileSourceById(redux.getState(), itemId)[field];
    assert.equal(actualValue, expectedValue, `${field} updated from ${initialValue} to ${actualValue}`);
    assert.equal(findAll('.source-name input')[0].value, expectedValue, `updated sourceName is ${expectedValue}`);
  });

  test('advanced accordion is collapsed when sourceName & fileEncoding have default settings', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSourceTypes()
      .policyWizFileSources(
        [{
          ...sources[0],
          sourceName: '',
          fileEncoding: DEFAULT_ENCODING
        }]
      )
      .build();

    this.setProperties({
      column,
      encodingOptions,
      itemId
    });

    await render(hbs`
      {{usm-policies/policy-wizard/define-policy-sources-step/advanced-settings-cell
        column=column
        encodingOptions=encodingOptions
        itemId=itemId
      }}
    `);

    assert.equal(findAll('.rsa-content-accordion.is-collapsed').length, 1, 'advanced accordion is collapsed');
    assert.equal(findAll('.source-name').length, 0, 'sourceName not rendered');
    assert.equal(findAll('.file-encoding').length, 0, 'fileEncoding not rendered');
  });

});
