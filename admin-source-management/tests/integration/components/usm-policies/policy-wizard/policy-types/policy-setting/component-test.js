import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { click, find, findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
// import Immutable from 'seamless-immutable';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../../helpers/vnext-patch';

const cpuMaxSetting = {
  // ====================================================================
  class: 'cpu-max', // FOR CONVENIENCE ONLY - NOT part of a setting
  // ====================================================================
  index: 5,
  id: 'cpuMax',
  label: 'CPU Max', // 'adminUsm.policyWizard.edrPolicy.cpuMax',
  isEnabled: true,
  isGreyedOut: false,
  parentId: 'scanType',
  component: 'usm-policies/policy-wizard/policy-types/edr/cpu-max',
  defaults: [
    {
      field: 'cpuMax',
      value: 25
    }
  ]
};

let setState;

module('Integration | Component | usm-policies/policy-wizard/policy-types/policy-setting', function(hooks) {
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

  test('The policy-setting wrapper AND setting components appear in the DOM', async function(assert) {
    assert.expect(2);
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizCpuMax(75)
      .build();

    this.set('removeFromSelectedSettings', () => {});
    this.set('settingComponent', cpuMaxSetting.component);
    this.set('selectedSettingId', cpuMaxSetting.id);
    this.set('policyType', 'edrPolicy');
    this.set('isDefaultPolicy', false);
    this.set('label', cpuMaxSetting.label);
    this.set('tooltip', cpuMaxSetting.label);

    await render(hbs`{{usm-policies/policy-wizard/policy-types/policy-setting
      removeFromSelectedSettings=(action removeFromSelectedSettings)
      settingComponent=settingComponent
      selectedSettingId=selectedSettingId
      policyType=policyType
      isDefaultPolicy=isDefaultPolicy
      label=label
      tooltip=tooltip}}`
    );

    // policy-setting wrapper should have a class consisting of the setting id + '-setting'...
    // ex.- given an id of 'cpuMax', we should find the class 'cpuMax-setting'
    const wrapperClass = `.${cpuMaxSetting.id}-setting`;
    assert.equal(findAll(wrapperClass).length, 1, `policy-setting wrapper (${wrapperClass}) appears in the DOM`);
    // also check for the setting
    const settingClass = `.${cpuMaxSetting.class}`;
    assert.equal(findAll(settingClass).length, 1, `given setting (${settingClass}) appears in DOM`);
  });

  test('for a default policy, appropriate class is set for the remove-circle icon', async function(assert) {
    assert.expect(2);
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizCpuMax(75)
      .build();

    this.set('removeFromSelectedSettings', () => {});
    this.set('settingComponent', cpuMaxSetting.component);
    this.set('selectedSettingId', cpuMaxSetting.id);
    this.set('policyType', 'edrPolicy');
    this.set('isDefaultPolicy', true);
    this.set('label', cpuMaxSetting.label);
    this.set('tooltip', cpuMaxSetting.label);

    // isDefaultPolicy === true
    await render(hbs`{{usm-policies/policy-wizard/policy-types/policy-setting
      removeFromSelectedSettings=(action removeFromSelectedSettings)
      settingComponent=settingComponent
      selectedSettingId=selectedSettingId
      policyType=policyType
      isDefaultPolicy=isDefaultPolicy
      label=label
      tooltip=tooltip}}`
    );
    assert.equal(findAll('.header .remove-setting.is-greyed-out').length, 1, 'expected to have remove-circle icon greyed out for a default policy');

    // isDefaultPolicy === false
    this.set('isDefaultPolicy', false);
    await render(hbs`{{usm-policies/policy-wizard/policy-types/policy-setting
      removeFromSelectedSettings=(action removeFromSelectedSettings)
      settingComponent=settingComponent
      selectedSettingId=selectedSettingId
      policyType=policyType
      isDefaultPolicy=isDefaultPolicy
      label=label
      tooltip=tooltip}}`
    );
    assert.equal(findAll('.header .remove-setting.not-greyed-out').length, 1, 'expected to have remove-circle icon enabled for a non-default policy');
  });

  test('It triggers the removeFromSelectedSettings policy action creator when the minus icon is clicked', async function(assert) {
    assert.expect(1);
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizCpuMax(75)
      .build();

    this.set('removeFromSelectedSettings', () => {
      assert.ok(true, 'removeFromSelectedSettings() was properly triggered');
    });
    this.set('settingComponent', cpuMaxSetting.component);
    this.set('selectedSettingId', cpuMaxSetting.id);
    this.set('policyType', 'edrPolicy');
    this.set('isDefaultPolicy', false);
    this.set('label', cpuMaxSetting.label);
    this.set('tooltip', cpuMaxSetting.label);

    await render(hbs`{{usm-policies/policy-wizard/policy-types/policy-setting
      removeFromSelectedSettings=(action removeFromSelectedSettings)
      settingComponent=settingComponent
      selectedSettingId=selectedSettingId
      policyType=policyType
      isDefaultPolicy=isDefaultPolicy
      label=label
      tooltip=tooltip}}`
    );
    const minusIcon = find('.header .remove-setting .rsa-icon');
    await click(minusIcon);
  });

});
