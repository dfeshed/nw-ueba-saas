import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { find, findAll, render } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../../../helpers/vnext-patch';

let setState;

module('Integration | Component | between-text-input', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });
  test('The between-text-input component appears in the DOM', async function(assert) {
    new ReduxDataHelper(setState).groupWiz().build();
    await render(hbs`{{usm-groups/group/group-attributes/attribute-inputs/between-text-input value="" criteriaPath=",0"}}`);
    assert.equal(findAll('.between-text-input').length, 1, 'The between-text-input component appears in the DOM');
  });

  test('IPv4 using between-text-input-blank inputs', async function(assert) {
    assert.expect(6);
    const translation = this.owner.lookup('service:i18n');
    const expectedMessage = translation.t('adminUsm.groupCriteria.inputValidations.validIPv4');
    new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizStepVisited('defineGroupStep', true)
      .build();
    const state = this.owner.lookup('service:redux').getState();
    this.set('criteria', [ 'BETWEEN', 'between-text-input', [''] ]);
    this.set('criteriaPath', '');
    this.set('index', '2');
    this.set('operator', state.usm.groupWizard.groupAttributesMap.map[3][1][0]);
    await render(hbs`{{usm-groups/group/group-attributes/attribute-inputs/between-text-input inputField=operator.[1]  value=criteria.[2] validation=operator.[2] criteriaPath=(concat criteriaPath ',' index)}}`);

    // invalid blank inputs
    assert.equal(findAll('.between-text-input .firstValueInput').length, 1, 'The first between-text-input for IPv4 appears in the DOM');
    assert.ok(find('.between-text-input .firstValueInput .input-error'), 'Error is showing with invalid blank input for first input');
    assert.equal(find('.input-error').textContent.trim(), expectedMessage, 'Correct error message is showing for first input');
    assert.equal(findAll('.between-text-input .secondValueInput').length, 1, 'The second between-text-input for IPv4 appears in the DOM');
    assert.ok(find('.between-text-input .secondValueInput .input-error'), 'Error is showing with invalid blank input for second input');
    assert.equal(find('.input-error').textContent.trim(), expectedMessage, 'Correct error message is showing for second input');
  });

  test('IPv4 using between-text-input-invalid first input', async function(assert) {
    assert.expect(5);
    const translation = this.owner.lookup('service:i18n');
    const expectedMessage = translation.t('adminUsm.groupCriteria.inputValidations.validIPv4');
    new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizStepVisited('defineGroupStep', true)
      .build();
    const state = this.owner.lookup('service:redux').getState();
    this.set('criteria', [ 'BETWEEN', 'between-text-input', [''] ]);
    this.set('criteriaPath', '');
    this.set('index', '2');
    this.set('operator', state.usm.groupWizard.groupAttributesMap.map[3][1][0]);
    await render(hbs`{{usm-groups/group/group-attributes/attribute-inputs/between-text-input inputField=operator.[1]  value=criteria.[2] validation=operator.[2] criteriaPath=(concat criteriaPath ',' index)}}`);

    // invalid first input
    this.set('criteria', [ 'BETWEEN', 'between-text-input', ['1,2.3.4', '2.3.4.5'] ]);
    assert.equal(findAll('.between-text-input .firstValueInput').length, 1, 'The first between-text-input for IPv4 appears in the DOM');
    assert.ok(find('.between-text-input .firstValueInput .input-error'), 'Error is showing with invalid blank input for first input');
    assert.equal(find('.input-error').textContent.trim(), expectedMessage, 'Correct error message is showing for first input');
    assert.equal(findAll('.between-text-input .secondValueInput').length, 1, 'The second between-text-input for IPv4 appears in the DOM');
    assert.notOk(find('.between-text-input .secondValueInput .input-error'), 'Error is not showing with valid input for second input');
  });

  test('IPv4 using between-text-input-invalid second input', async function(assert) {
    assert.expect(5);
    const translation = this.owner.lookup('service:i18n');
    const expectedMessage = translation.t('adminUsm.groupCriteria.inputValidations.validIPv4');
    new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizStepVisited('defineGroupStep', true)
      .build();
    const state = this.owner.lookup('service:redux').getState();
    this.set('criteria', [ 'BETWEEN', 'between-text-input', [''] ]);
    this.set('criteriaPath', '');
    this.set('index', '2');
    this.set('operator', state.usm.groupWizard.groupAttributesMap.map[3][1][0]);
    await render(hbs`{{usm-groups/group/group-attributes/attribute-inputs/between-text-input inputField=operator.[1]  value=criteria.[2] validation=operator.[2] criteriaPath=(concat criteriaPath ',' index)}}`);

    // invalid second input
    this.set('criteria', [ 'BETWEEN', 'between-text-input', ['1.2.3.4', '2-3.4.5'] ]);
    assert.equal(findAll('.between-text-input .firstValueInput').length, 1, 'The first between-text-input for IPv4 appears in the DOM');
    assert.notOk(find('.between-text-input .firstValueInput .input-error'), 'Error is not showing with valid blank input for first input');
    assert.equal(findAll('.between-text-input .secondValueInput').length, 1, 'The second between-text-input for IPv4 appears in the DOM');
    assert.ok(find('.between-text-input .secondValueInput .input-error'), 'Error is showing with invalid input for second input');
    assert.equal(find('.input-error').textContent.trim(), expectedMessage, 'Correct error message is showing for second input');
  });

  test('IPv4 using between-text-input-valid-inputs', async function(assert) {
    assert.expect(4);
    new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizStepVisited('defineGroupStep', true)
      .build();
    const state = this.owner.lookup('service:redux').getState();
    this.set('criteria', [ 'BETWEEN', 'between-text-input', [''] ]);
    this.set('criteriaPath', '');
    this.set('index', '2');
    this.set('operator', state.usm.groupWizard.groupAttributesMap.map[3][1][0]);
    await render(hbs`{{usm-groups/group/group-attributes/attribute-inputs/between-text-input inputField=operator.[1]  value=criteria.[2] validation=operator.[2] criteriaPath=(concat criteriaPath ',' index)}}`);

    // valid inputs
    this.set('criteria', [ 'BETWEEN', 'between-text-input', ['1.2.3.4', '2.3.4.5'] ]);
    assert.equal(findAll('.between-text-input .firstValueInput').length, 1, 'The first between-text-input for IPv4 appears in the DOM');
    assert.notOk(find('.between-text-input .firstValueInput .input-error'), 'Error is not showing with valid blank input for first input');
    assert.equal(findAll('.between-text-input .secondValueInput').length, 1, 'The second between-text-input for IPv4 appears in the DOM');
    assert.notOk(find('.between-text-input .secondValueInput .input-error'), 'Error is not showing with valid input for second input');
  });
});
