import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { find, findAll, render } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../../../helpers/vnext-patch';

let setState;

module('Integration | Component | os-selector', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });
  test('The component appears in the DOM', async function(assert) {
    new ReduxDataHelper(setState).groupWiz().build();
    await render(hbs`{{usm-groups/group/group-attributes/attribute-inputs/os-selector selectedValues="" criteriaPath=",0"}}`);
    assert.equal(findAll('.os-selector').length, 1, 'The os-selector component appears in the DOM');
  });

  test('osSelector using os-selector', async function(assert) {
    assert.expect(5);
    const translation = this.owner.lookup('service:i18n');
    const expectedMessage = translation.t('adminUsm.groupCriteria.inputValidations.notEmpty');
    new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizStepShowErrors('defineGroupStep', true)
      .build();
    const state = this.owner.lookup('service:redux').getState();
    this.set('criteria', [ 'IN', 'os-selector', [] ]);
    this.set('criteriaPath', '');
    this.set('index', '2');
    this.set('operator', state.usm.groupWizard.groupAttributesMap.map[0][1][0]);
    await render(hbs`{{usm-groups/group/group-attributes/attribute-inputs/os-selector inputField=operator.[1]  value=criteria.[2] validation=operator.[2] criteriaPath=(concat criteriaPath ',' index)}}`);
    // invalid blank input
    assert.equal(findAll('.ember-power-select-multiple-options').length, 1, 'The os-selector for osSelector appears in the DOM');
    assert.ok(find('.selector-error'), 'Error is showing with invalid blank input');
    assert.equal(find('.input-error').textContent.trim(), expectedMessage, 'Correct error message is showing');
    // valid input
    this.set('criteria', [ 'IN', 'os-selector', ['Linux'] ]);
    assert.equal(findAll('.ember-power-select-multiple-options').length, 1, 'The os-selector for osSelector appears in the DOM');
    assert.notOk(find('.selector-error'), 'Error is not showing for valid input');
  });

});
