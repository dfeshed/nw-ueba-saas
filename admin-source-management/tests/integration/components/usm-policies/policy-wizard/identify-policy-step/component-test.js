import { module, test, skip } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { blur, find, findAll, focus, render, settled } from '@ember/test-helpers';
import sinon from 'sinon';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import policyWizardCreators from 'admin-source-management/actions/creators/policy-wizard-creators';

let setState;

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

  test('The component appears in the DOM', async function(assert) {
    assert.expect(4);
    new ReduxDataHelper(setState).build();
    await render(hbs`{{usm-policies/policy-wizard/identify-policy-step}}`);
    assert.equal(findAll('.identify-policy-step').length, 1, 'The component appears in the DOM');

    // power-select does not seem to render added class(es) like power-select-multiple,
    // so .source-type was added to the control wrapper div
    assert.equal(findAll('.control.source-type .ember-power-select-selected-item').length, 1, 'Source Type power-select control appears in the DOM');
    assert.equal(findAll('.control .policy-name input').length, 1, 'Policy Name input control appears in the DOM');
    assert.equal(findAll('.control-with-error .policy-description textarea').length, 1, 'Policy Description input control appears in the DOM');
  });

  test('The component appears in the DOM with correct values', async function(assert) {
    assert.expect(3);
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
    const [sourceTypeEl] = findAll('.control.source-type .ember-power-select-selected-item');
    assert.equal(sourceTypeEl.innerText, sourceTypeText, `Source Type selection is ${sourceTypeText}`);
    const [nameEl] = findAll('.control .policy-name input');
    assert.equal(nameEl.value, testName, `Policy Name is ${testName}`);
    const [descEl] = findAll('.control-with-error .policy-description textarea');
    assert.equal(descEl.value, testDesc, `Policy Description is ${testDesc}`);
  });

  // TODO skipping this as it suddenly fails most of the time - the spy props are not getting set
  skip('Typing in the policy name control dispatches the editPolicy action creator', async function(assert) {
    const done = assert.async();
    assert.expect(4);
    const actionSpy = sinon.spy(policyWizardCreators, 'editPolicy');
    new ReduxDataHelper(setState).policyWiz().build();
    await render(hbs`{{usm-policies/policy-wizard/identify-policy-step}}`);
    assert.equal(findAll('.control.source-type .ember-power-select-selected-item').length, 1, 'Source Type power-select control appears in the DOM');
    assert.equal(findAll('.control .policy-name input').length, 1, 'Policy Name input control appears in the DOM');
    const [el] = findAll('.control .policy-name input');
    await focus(el);
    el.value = 'test name';
    const expectedTestName = 'test name';
    await blur(el);
    return settled().then(() => {
      assert.ok(actionSpy.callCount > 0, 'The editPolicy action was called');
      assert.ok(actionSpy.calledWith('policy.name', expectedTestName), `The editPolicy action was called with trimmed "${expectedTestName}"`);
      actionSpy.restore();
      done();
    });
  });

  // TODO skipping this as it suddenly fails most of the time - the spy props are not getting set
  skip('Typing in the policy description control dispatches the editPolicy action creator', async function(assert) {
    const done = assert.async();
    assert.expect(3);
    const actionSpy = sinon.spy(policyWizardCreators, 'editPolicy');
    new ReduxDataHelper(setState).policyWiz().build();
    await render(hbs`{{usm-policies/policy-wizard/identify-policy-step}}`);
    assert.equal(findAll('.control .policy-name input').length, 1, 'Policy Name input control appears in the DOM');
    const [el] = findAll('.control-with-error .policy-description textarea');
    await focus(el);
    const expectedTestDesc = 'test description';
    el.value = expectedTestDesc;
    await blur(el);
    return settled().then(() => {
      assert.ok(actionSpy.callCount > 0, 'The editPolicy action was called');
      assert.ok(actionSpy.calledWith('policy.description', expectedTestDesc), `The editPolicy action was called with trimmed "${expectedTestDesc}"`);
      actionSpy.restore();
      done();
    });
  });

  test('Error message for blank name does not appear if the field has not been visited', async function(assert) {
    assert.expect(1);
    const testName = '';
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizName(testName)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/identify-policy-step}}`);
    await assert.notOk(find('.control .policy-name .input-error'), 'Error is not showing');
  });

  test('Error message for blank name appears if the field has been visited', async function(assert) {
    assert.expect(2);
    const translation = this.owner.lookup('service:i18n');
    const expectedMessage = translation.t('adminUsm.policyWizard.nameRequired');
    const testName = '';
    const visitedExpected = ['policy.name'];
    new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizName(testName)
      .policyWizVisited(visitedExpected)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/identify-policy-step}}`);
    assert.ok(find('.control .policy-name .input-error'), 'Error is showing');
    assert.equal(find('.control .policy-name .input-error').textContent.trim(), expectedMessage, 'Correct error message is showing');
  });

  test('Error message for name appears when length is too long', async function(assert) {
    assert.expect(2);
    const translation = this.owner.lookup('service:i18n');
    const expectedMessage = translation.t('adminUsm.policyWizard.nameExceedsMaxLength');
    let testName = '';
    for (let index = 0; index < 10; index++) {
      testName += 'the-name-is-greater-than-256-';
    }
    await render(hbs`{{usm-policies/policy-wizard/identify-policy-step}}`);
    const [el] = findAll('.control .policy-name input');
    await focus(el);
    el.value = testName;
    await blur(el);
    assert.ok(find('.control .policy-name .input-error'), 'Error is showing');
    assert.equal(find('.control .policy-name .input-error').textContent.trim(), expectedMessage, 'Correct error message is showing');
  });

  test('Error message for description appears when length is too long', async function(assert) {
    assert.expect(2);
    const translation = this.owner.lookup('service:i18n');
    const expectedMessage = translation.t('adminUsm.policyWizard.descriptionExceedsMaxLength');
    let testDesc = '';
    for (let index = 0; index < 220; index++) {
      testDesc += 'the-description-is-greater-than-8000-';
    }
    await render(hbs`{{usm-policies/policy-wizard/identify-policy-step}}`);
    const [el] = findAll('.control-with-error .policy-description textarea');
    await focus(el);
    el.value = testDesc;
    await blur(el);
    assert.ok(find('.policy-description-error'), 'Error is showing');
    assert.equal(find('.policy-description-error').textContent.trim(), expectedMessage, 'Correct error message is showing');
  });

});
