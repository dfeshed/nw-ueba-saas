import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { blur, find, findAll, fillIn, focus, render, triggerEvent } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import waitForReduxStateChange from '../../../../../helpers/redux-async-helpers';
import { group } from 'admin-source-management/reducers/usm/group-wizard-selectors';

let redux, setState;

module('Integration | Component | usm-groups/group-wizard/identify-group-step', function(hooks) {
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

  test('The component appears in the DOM', async function(assert) {
    assert.expect(3);
    new ReduxDataHelper(setState).build();
    await render(hbs`{{usm-groups/group-wizard/identify-group-step}}`);
    assert.equal(findAll('.identify-group-step').length, 1, 'The component appears in the DOM');
    assert.equal(findAll('.control .group-name input').length, 1, 'Group Name input control appears in the DOM');
    assert.equal(findAll('.control-with-error .group-description textarea').length, 1, 'Group Description input control appears in the DOM');
  });

  test('The component appears in the DOM with correct values', async function(assert) {
    assert.expect(2);
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
    assert.equal(findAll('.identify-group-step').length, 1, 'The component appears in the DOM');
    const field = 'name';
    const expectedValue = 'test name';
    const [el] = findAll('.control .group-name input');
    const onChange = waitForReduxStateChange(redux, `usm.groupWizard.group.${field}`);
    await fillIn(el, expectedValue);
    await triggerEvent(el, 'blur');
    await onChange;
    const actualValue = group(redux.getState());
    assert.equal(actualValue.name, expectedValue, `${field} updated from '' to ${actualValue.name}`);
  });

  test('Typing in the group description control dispatches the editGroup action creator', async function(assert) {
    new ReduxDataHelper(setState).groupWiz().build();
    await render(hbs`{{usm-groups/group-wizard/identify-group-step}}`);
    assert.equal(findAll('.identify-group-step').length, 1, 'The component appears in the DOM');
    const field = 'description';
    const expectedValue = 'test description';
    const [el] = findAll('.control-with-error .group-description textarea');
    const onChange = waitForReduxStateChange(redux, `usm.groupWizard.group.${field}`);
    await fillIn(el, expectedValue);
    await triggerEvent(el, 'blur');
    await onChange;
    const actualValue = group(redux.getState());
    assert.equal(actualValue.description, expectedValue, `${field} updated from '' to ${actualValue.description}`);
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
