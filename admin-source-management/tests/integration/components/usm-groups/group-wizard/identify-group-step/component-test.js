import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { blur, find, findAll, focus, render, settled } from '@ember/test-helpers';
import sinon from 'sinon';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import groupWizardCreators from 'admin-source-management/actions/creators/group-wizard-creators';

let setState;

const editGroupSpy = sinon.spy(groupWizardCreators, 'editGroup');

const spys = [
  editGroupSpy
];


module('Integration | Component | usm-groups/group-wizard/identify-group-step', function(hooks) {
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
    await render(hbs`{{usm-groups/group-wizard/identify-group-step}}`);
    assert.equal(findAll('.identify-group-step').length, 1, 'The component appears in the DOM');

    assert.equal(findAll('.control .group-name input').length, 1, 'Group Name input control appears in the DOM');
    assert.equal(findAll('.control-with-error .group-description textarea').length, 1, 'Group Description input control appears in the DOM');
  });

  test('The component appears in the DOM with correct values', async function(assert) {
    const testName = 'test name';
    const testDesc = 'test desc';
    new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizName(testName)
      .groupWizDescription(testDesc)
      .build();
    await render(hbs`{{usm-groups/group-wizard/identify-group-step}}`);
    const [nameEl] = findAll('.control .group-name input');
    assert.equal(nameEl.value, testName, `Group Name is ${testName}`);
    const [descEl] = findAll('.control-with-error .group-description textarea');
    assert.equal(descEl.value, testDesc, `Group Description is ${testDesc}`);
  });

  test('Typing in the group name control dispatches the editgroup action creator', async function(assert) {
    new ReduxDataHelper(setState).groupWiz().build();
    await render(hbs`{{usm-groups/group-wizard/identify-group-step}}`);
    const [el] = findAll('.control .group-name input');
    await focus(el);
    const testName = el.value = 'test name';
    const expectedTestName = 'test name';
    // await triggerKeyEvent(el, 'keyup', 'e'); // might go back to this with debounce
    await blur(el);
    return settled().then(() => {
      assert.ok(editGroupSpy.calledOnce, 'The editGroup action was called once');
      assert.ok(editGroupSpy.calledWith('group.name', expectedTestName), `The editGroup action was called with trimmed "${testName}"`);
    });
  });

  test('Typing in the group description control dispatches the editGroup action creator', async function(assert) {
    new ReduxDataHelper(setState).groupWiz().build();
    await render(hbs`{{usm-groups/group-wizard/identify-group-step}}`);
    const [el] = findAll('.control-with-error .group-description textarea');
    await focus(el);
    const testDesc = el.value = 'test description';
    const expectedTestDesc = 'test description';
    await blur(el);
    return settled().then(() => {
      assert.ok((editGroupSpy.callCount == 2), 'The editGroup action was called twice');
      assert.ok(editGroupSpy.calledWith('group.description', expectedTestDesc), `The editGroup action was called with trimmed "${testDesc}"`);
    });
  });

  test('Error message for blank name does not appear if the field has not been visited', async function(assert) {
    assert.expect(1);
    const testName = '';
    new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizName(testName)
      .build();
    await render(hbs`{{usm-groups/group-wizard/identify-group-step}}`);
    await assert.notOk(find('.control .group-name .input-error'), 'Error is not showing');
  });

  test('Error message for blank name appears if the field has been visited', async function(assert) {
    assert.expect(2);
    const translation = this.owner.lookup('service:i18n');
    const expectedMessage = translation.t('adminUsm.groupWizard.nameRequired');
    const testName = '';
    const visitedExpected = ['group.name'];
    new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizName(testName)
      .groupWizVisited(visitedExpected)
      .build();
    await render(hbs`{{usm-groups/group-wizard/identify-group-step}}`);
    assert.ok(find('.control .group-name .input-error'), 'Error is showing');
    assert.equal(find('.control .group-name .input-error').textContent.trim(), expectedMessage, 'Correct error message is showing');
  });

  test('Error message for name appears when length is too long', async function(assert) {
    assert.expect(2);
    const translation = this.owner.lookup('service:i18n');
    const expectedMessage = translation.t('adminUsm.groupWizard.nameExceedsMaxLength');
    let testName = '';
    for (let index = 0; index < 10; index++) {
      testName += 'the-name-is-greater-than-256-';
    }
    await render(hbs`{{usm-groups/group-wizard/identify-group-step}}`);
    const [el] = findAll('.control .group-name input');
    await focus(el);
    el.value = testName;
    await blur(el);
    assert.ok(find('.control .group-name .input-error'), 'Error is showing');
    assert.equal(find('.control .group-name .input-error').textContent.trim(), expectedMessage, 'Correct error message is showing');
  });

  test('Error message for description appears when length is too long', async function(assert) {
    assert.expect(2);
    const translation = this.owner.lookup('service:i18n');
    const expectedMessage = translation.t('adminUsm.groupWizard.descriptionExceedsMaxLength');
    let testDesc = '';
    for (let index = 0; index < 220; index++) {
      testDesc += 'the-description-is-greater-than-8000-';
    }
    await render(hbs`{{usm-groups/group-wizard/identify-group-step}}`);
    const [el] = findAll('.control-with-error .group-description textarea');
    await focus(el);
    el.value = testDesc;
    await blur(el);
    assert.ok(find('.group-description-error'), 'Error is showing');
    assert.equal(find('.group-description-error').textContent.trim(), expectedMessage, 'Correct error message is showing');
  });

});
