import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { render, findAll, fillIn, triggerEvent } from '@ember/test-helpers';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../../../helpers/vnext-patch';
import waitForReduxStateChange from '../../../../../../../helpers/redux-async-helpers';
import { customConfig } from 'admin-source-management/reducers/usm/policy-wizard/edrPolicy/edr-selectors';

let redux, setState;

module('Integration | Component | usm-policies/policy-wizard/policy-types/edr/custom-config', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (state) => {
      patchReducer(this, state);
      redux = this.owner.lookup('service:redux');
    };
  });

  test('should render customConfig component when id is customConfig', async function(assert) {
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/custom-config selectedSettingId='customConfig'}}`);
    assert.equal(findAll('.custom-config').length, 1, 'expected to have customConfig root input element in DOM');
  });

  test('It triggers the update policy action creator when the custom setting value is changed', async function(assert) {
    const initCustomConfig = '{ "foo": "bar" }';
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizCustomConfig(initCustomConfig)
      .build();
    const selectedSettingId = 'customConfig';
    this.set('selectedSettingId', selectedSettingId);
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/custom-config selectedSettingId=selectedSettingId}}`);
    const expectedCustomConfig = '{ "foo": "bar", "footwo": "baz" }';
    const [textareaEl] = findAll('.custom-config__textarea textarea');
    const onChange = waitForReduxStateChange(redux, `usm.policyWizard.policy.${selectedSettingId}`);
    await fillIn(textareaEl, expectedCustomConfig);
    await triggerEvent(textareaEl, 'blur');
    await onChange;
    const actualCustomConfig = customConfig(redux.getState(), selectedSettingId);
    assert.equal(actualCustomConfig, expectedCustomConfig, `${selectedSettingId} updated from ${initCustomConfig} to ${actualCustomConfig}`);
  });

  test('It shows/hides the error message when the custom setting is invalid/valid', async function(assert) {
    const translation = this.owner.lookup('service:i18n');
    const expectedMessage = translation.t('adminUsm.policyWizard.edrPolicy.customConfigInvalidMsg');

    const initCustomConfig = '';
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizCustomConfig(initCustomConfig)
      .build();
    const selectedSettingId = 'customConfig';
    this.set('selectedSettingId', selectedSettingId);

    // invalid - empty
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/custom-config selectedSettingId=selectedSettingId}}`);
    const [textareaEl] = findAll('.custom-config__textarea textarea');
    assert.equal(findAll('.is-error').length, 1, 'Error is showing');
    assert.equal(findAll('.input-error')[0].innerText, expectedMessage, `Correct error message is showing for blank value: ${expectedMessage}`);

    // valid
    let onChange = waitForReduxStateChange(redux, `usm.policyWizard.policy.${selectedSettingId}`);
    await fillIn(textareaEl, '{ "trackingConfig": { "uniqueFilterSeconds": 28800, "beaconStdDev": 2.0 } }');
    await triggerEvent(textareaEl, 'blur');
    await onChange;
    assert.equal(findAll('.is-error').length, 0, 'Error is NOT showing');
    assert.equal(findAll('.input-error')[0].innerText, '', 'No error message when valid input');

    // invalid - greater than 4k
    let testSetting = '';
    for (let index = 0; index < 110; index++) {
      testSetting += 'the-description-is-greater-than-4000-';
    }
    onChange = waitForReduxStateChange(redux, `usm.policyWizard.policy.${selectedSettingId}`);
    await fillIn(textareaEl, testSetting);
    await triggerEvent(textareaEl, 'blur');
    await onChange;
    assert.equal(findAll('.is-error').length, 1, 'Error is showing');
    assert.equal(findAll('.input-error')[0].innerText, expectedMessage, `Correct error message is showing for too long: ${expectedMessage}`);
  });

});