import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { click, render, findAll } from '@ember/test-helpers';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../../../helpers/vnext-patch';

let setState;

module('Integration | Component | usm-policies/policy-wizard/policy-types/shared/usm-radios-with-warning', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (state) => {
      patchReducer(this, state);
    };
  });

  // ====================================================================
  // test usage for network isolation edr settings
  // ====================================================================

  test('Should render edrPolicy isolationEnabled options when isolationEnabled id is passed', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz()
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/shared/usm-radios-with-warning selectedSettingId='isolationEnabled'}}`);
    assert.equal(findAll('.isolationEnabled').length, 1, 'expected to have agentMode component in DOM');
    assert.equal(findAll('.radio-option').length, 2, 'expected to have two radio buttons in dom');
  });

  test('Should render edrPolicy isolationEnabled warning message when option is toggled when the initial value is isolation enabled', async function(assert) {
    const policy = {
      id: '5d91e6fada7bd9033284f6d9',
      policyType: 'edrPolicy',
      name: 'test',
      isolationEnabled: true
    };
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizPolicy(policy)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/shared/usm-radios-with-warning selectedSettingId='isolationEnabled'}}`);

    const radioBtnDisabled = document.querySelector('.isolationEnabled .rsa-form-radio-wrapper:nth-of-type(1) input');
    const radioBtnEnabled = document.querySelector('.isolationEnabled .rsa-form-radio-wrapper:nth-of-type(2) input');

    await click(radioBtnDisabled);
    assert.equal(findAll('.rsa-content-warn-text-box').length, 1, 'Warning message present as user has disabled an enabled policy.');

    await click(radioBtnEnabled);
    assert.equal(findAll('.rsa-content-warn-text-box').length, 0, 'Warning message not present as user has enabled policy.');
  });

  test('Should not render edrPolicy isolationEnabled warning message when option is toggled when the initial value is isolation disabled', async function(assert) {
    const policy = {
      id: '5d91e6fada7bd9033284f6d9',
      policyType: 'edrPolicy',
      name: 'test',
      isolationEnabled: false
    };
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizPolicy(policy)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/shared/usm-radios-with-warning selectedSettingId='isolationEnabled'}}`);

    const radioBtnDisabled = document.querySelector('.isolationEnabled .rsa-form-radio-wrapper:nth-of-type(1) input');
    const radioBtnEnabled = document.querySelector('.isolationEnabled .rsa-form-radio-wrapper:nth-of-type(2) input');

    await click(radioBtnEnabled);
    assert.equal(findAll('.rsa-content-warn-text-box').length, 0, 'Warning message not present as user has enabled policy.');

    await click(radioBtnDisabled);
    assert.equal(findAll('.rsa-content-warn-text-box').length, 0, 'Warning message not present as user has disabled an initially disabled policy.');
  });
});
