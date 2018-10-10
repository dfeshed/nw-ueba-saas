import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { blur, find, findAll, focus, render } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../../../helpers/vnext-patch';

let setState;

module('Integration | Component | text-input', function(hooks) {
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

  test('The text-input component appears in the DOM', async function(assert) {
    new ReduxDataHelper(setState).groupWiz().build();
    await render(hbs`{{usm-groups/group/group-attributes/attribute-inputs/text-input value="" criteriaPath=",0"}}`);
    assert.equal(findAll('.text-input').length, 1, 'The text-input component appears in the DOM');
  });

  test('osDescription using text-input', async function(assert) {
    assert.expect(5);
    const translation = this.owner.lookup('service:i18n');
    const expectedMessage = translation.t('adminUsm.groupCriteria.inputValidations.maxLength256');
    new ReduxDataHelper(setState)
      .groupWiz()
      .build();
    const state = this.owner.lookup('service:redux').getState();
    this.set('criteria', state.usm.groupWizard.group.groupCriteria.criteria[0]);
    this.set('criteriaPath', '');
    this.set('index', '0');
    this.set('operator', state.usm.groupWizard.groupAttributesMap.map[1][1][0]);
    await render(hbs`{{usm-groups/group/group-attributes/attribute-inputs/text-input inputField=operator.[1]  value=criteria.[2] validation=operator.[2] criteriaPath=(concat criteriaPath ',' index)}}`);
    const [el] = findAll('.text-input input');
    await focus(el);
    el.value = 'Test osDescription';
    await blur(el);
    assert.equal(findAll('.text-input').length, 1, 'The text-input for osDescription appears in the DOM');
    assert.notOk(find('.input-error'), 'Error is not showing for valid input');

    // invalid blank input
    await focus(el);
    el.value = '';
    await blur(el);
    assert.equal(findAll('.text-input').length, 1, 'The text-input for osDescription appears in the DOM');
    assert.ok(find('.input-error'), 'Error is showing with invalid blank input');
    assert.equal(find('.input-error').textContent.trim(), expectedMessage, 'Correct error message is showing');

  });

  test('Hostname using text-input', async function(assert) {
    assert.expect(8);
    const translation = this.owner.lookup('service:i18n');
    const expectedMessage = translation.t('adminUsm.groupCriteria.inputValidations.validHostname');
    new ReduxDataHelper(setState)
      .groupWiz()
      .build();
    const state = this.owner.lookup('service:redux').getState();
    this.set('criteria', state.usm.groupWizard.group.groupCriteria.criteria[0]);
    this.set('criteriaPath', '');
    this.set('index', '0');
    this.set('operator', state.usm.groupWizard.groupAttributesMap.map[2][1][0]);
    this.set('value', 'test');
    await render(hbs`{{usm-groups/group/group-attributes/attribute-inputs/text-input inputField=operator.[1]  value=criteria.[2] validation=operator.[2] criteriaPath=(concat criteriaPath ',' index)}}`);
    const [el] = findAll('.text-input input');
    await focus(el);
    el.value = 'Host001';
    await blur(el);
    assert.equal(findAll('.text-input').length, 1, 'The text-input for Hostname appears in the DOM');
    assert.notOk(find('.input-error'), 'Error is not showing for valid input');

    // invalid blank input
    await focus(el);
    el.value = '';
    await blur(el);
    assert.equal(findAll('.text-input').length, 1, 'The text-input for Hostname appears in the DOM');
    assert.ok(find('.input-error'), 'Error is showing with invalid blank input');
    assert.equal(find('.input-error').textContent.trim(), expectedMessage, 'Correct error message is showing');

    // invalid blank input
    await focus(el);
    el.value = '--name';
    await blur(el);
    assert.equal(findAll('.text-input').length, 1, 'The text-input for Hostname appears in the DOM');
    assert.ok(find('.input-error'), 'Error is showing with invalid input');
    assert.equal(find('.input-error').textContent.trim(), expectedMessage, 'Correct error message is showing');
  });

});
