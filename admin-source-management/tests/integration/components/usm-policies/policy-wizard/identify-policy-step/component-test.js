import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { blur, findAll, focus, render, settled } from '@ember/test-helpers';
import sinon from 'sinon';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import policyWizardCreators from 'admin-source-management/actions/creators/policy-wizard-creators';

let setState;

const editPolicySpy = sinon.spy(policyWizardCreators, 'editPolicy');

const spys = [
  editPolicySpy
];


module('Integration | Component | usm-policies/policy-wizard/identify-policy-step', function(hooks) {
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

  hooks.afterEach(function() {
    spys.forEach((s) => s.reset());
  });

  hooks.after(function() {
    spys.forEach((s) => s.restore());
  });

  test('The component appears in the DOM', async function(assert) {
    new ReduxDataHelper(setState).build();
    await render(hbs`{{usm-policies/policy-wizard/identify-policy-step}}`);
    assert.equal(findAll('.identify-policy-step').length, 1, 'The component appears in the DOM');

    // power-select does not seem to render added class(es) like power-select-multiple,
    // so .source-type was added to the control wrapper div
    assert.equal(findAll('.control.source-type .ember-power-select-selected-item').length, 1, 'Source Type power-select control appears in the DOM');
    assert.equal(findAll('.control .policy-name input').length, 1, 'Policy Name input control appears in the DOM');
    assert.equal(findAll('.control .policy-description textarea').length, 1, 'Policy Description input control appears in the DOM');
  });

  test('The component appears in the DOM with correct values', async function(assert) {
    const translation = this.owner.lookup('service:i18n');
    const sourceTypeText = translation.t('adminUsm.policyWizard.edrSourceType');
    const testName = 'test name';
    const testDesc = 'test desc';
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizSourceType('edrPolicy') // the ID since it's a power-select
      .policyWizName(testName)
      .policyWizDescription(testDesc)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/identify-policy-step}}`);
    const sourceTypeEl = findAll('.control.source-type .ember-power-select-selected-item')[0]; // eslint-disable-line ember-suave/prefer-destructuring
    assert.equal(sourceTypeEl.innerText, sourceTypeText, `Source Type selection is ${sourceTypeText}`);
    const nameEl = findAll('.control .policy-name input')[0]; // eslint-disable-line ember-suave/prefer-destructuring
    assert.equal(nameEl.value, testName, `Policy Name is ${testName}`);
    const descEl = findAll('.control .policy-description textarea')[0]; // eslint-disable-line ember-suave/prefer-destructuring
    assert.equal(descEl.value, testDesc, `Policy Name is ${testDesc}`);
  });

  test('Typing in the policy name control dispatches the editPolicy action creator', async function(assert) {
    new ReduxDataHelper(setState).policyWiz().build();
    await render(hbs`{{usm-policies/policy-wizard/identify-policy-step}}`);
    const el = findAll('.control .policy-name input')[0]; // eslint-disable-line ember-suave/prefer-destructuring
    await focus(el);
    const testName = el.value = 'test name';
    // await triggerKeyEvent(el, 'keyup', 'e'); // might go back to this with debounce
    await blur(el);
    return settled().then(() => {
      assert.ok(editPolicySpy.calledOnce, 'The editPolicy action was called once');
      assert.ok(editPolicySpy.calledWith('policy.name', testName), `The editPolicy action was called with ${testName}`);
    });
  });

  test('Typing in the policy description control dispatches the editPolicy action creator', async function(assert) {
    new ReduxDataHelper(setState).policyWiz().build();
    await render(hbs`{{usm-policies/policy-wizard/identify-policy-step}}`);
    const el = findAll('.control .policy-description textarea')[0]; // eslint-disable-line ember-suave/prefer-destructuring
    await focus(el);
    const testDesc = el.value = 'test description';
    // await triggerKeyEvent(el, 'keyup', 'e'); // might go back to this with debounce
    await blur(el);
    return settled().then(() => {
      assert.ok(editPolicySpy.calledOnce, 'The editPolicy action was called once');
      assert.ok(editPolicySpy.calledWith('policy.description', testDesc), `The editPolicy action was called with ${testDesc}`);
    });
  });

});
