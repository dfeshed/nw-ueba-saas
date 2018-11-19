import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, findAll, click } from '@ember/test-helpers';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { setFlatpickrDate } from 'ember-flatpickr/test-support/helpers';
import sinon from 'sinon';
import ReduxDataHelper from '../../../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import policyWizardCreators from 'admin-source-management/actions/creators/policy-wizard-creators';

let setState, removeFromSelectedSettingsSpy, updatePolicyPropertySpy;
const spys = [];

module('Integration | Component | usm-policies/policy-wizard/policy-types/edr/start-time', function(hooks) {
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
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  hooks.afterEach(function() {
    spys.forEach((s) => s.resetHistory());
  });

  hooks.after(function() {
    spys.forEach((s) => s.restore());
  });

  test('should render the start-time component', async function(assert) {
    new ReduxDataHelper(setState).policyWiz().build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/start-time}}`);
    assert.equal(findAll('.start-time').length, 1, 'expected to have root element in DOM');
  });

  test('should trigger the updatePolicyProperty action creator on time change', async function(assert) {
    new ReduxDataHelper(setState).policyWiz().build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/start-time}}`);
    assert.equal(updatePolicyPropertySpy.callCount, 0, 'Update policy property action creator has not been called when the time stays the same');
    const inputEl = document.querySelector('.schedule-time input');
    setFlatpickrDate(inputEl, '123456789');
    assert.equal(updatePolicyPropertySpy.callCount, 1, 'Update policy property action creator was called on the time change');
  });

  test('It triggers the removeFromSelectedSettings policy action creator when the minus icon is clicked', async function(assert) {
    new ReduxDataHelper(setState).policyWiz().build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/edr/start-time}}`);
    const minusIcon = document.querySelector('.start-time span .rsa-icon');
    await click(minusIcon);
    assert.equal(removeFromSelectedSettingsSpy.callCount, 1, 'Remove from selectedSettings action creator was called once');
  });
});
