import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, findAll, click } from '@ember/test-helpers';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import sinon from 'sinon';
import ReduxDataHelper from '../../../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../../../helpers/vnext-patch';
import policyWizardCreators from 'admin-source-management/actions/creators/policy-wizard-creators';
import { selectChoose } from 'ember-power-select/test-support/helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let setState, removeFromSelectedSettingsSpy, updatePolicyPropertySpy;
const spys = [];

module('Integration | Component | usm-policies/policy-wizard/policy-types/windows-log/windows-log-protocol', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.before(function() {
    spys.push(removeFromSelectedSettingsSpy = sinon.spy(policyWizardCreators, 'removeFromSelectedSettings'));
    spys.push(updatePolicyPropertySpy = sinon.spy(policyWizardCreators, 'updatePolicyProperty'));
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  hooks.afterEach(function() {
    spys.forEach((s) => s.resetHistory());
  });

  hooks.after(function() {
    spys.forEach((s) => s.restore());
  });

  test('should render windows-log-protocol component', async function(assert) {
    await render(hbs`{{usm-policies/policy-wizard/policy-types/windows-log/windows-log-protocol}}`);
    assert.equal(findAll('.windows-log-protocol').length, 1, 'expected to have windows-log-protocol root input element in DOM');
  });

  test('It triggers the update policy action creator when the protocol is changed', async function(assert) {
    new ReduxDataHelper(setState)
        .policyWiz('windowsLogPolicy')
        .policyWizWinLogProtocol('TLS')
        .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/windows-log/windows-log-protocol}}`);
    await selectChoose('.windows-log-protocol__list', '.ember-power-select-option', 0);
    assert.equal(updatePolicyPropertySpy.callCount, 1, 'Update policy property action creator was called once');
  });

  test('It triggers the removeFromSelectedSettings policy action creator when the minus icon is clicked', async function(assert) {
    await render(hbs`{{usm-policies/policy-wizard/policy-types/windows-log/windows-log-protocol}}`);
    const minusIcon = document.querySelector('.windows-log-protocol span .rsa-icon');
    await click(minusIcon);
    assert.equal(removeFromSelectedSettingsSpy.callCount, 1, 'Remove from selectedSettings action creator was called once');
  });
});