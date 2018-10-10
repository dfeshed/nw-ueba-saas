import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { find, findAll, render } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../../../helpers/vnext-patch';

let setState;

module('Integration | Component | textarea-input', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });
  test('The textarea-input component appears in the DOM', async function(assert) {
    new ReduxDataHelper(setState).groupWiz().build();
    await render(hbs`{{usm-groups/group/group-attributes/attribute-inputs/textarea-input value="" criteriaPath=",0"}}`);
    assert.equal(findAll('.textarea-input').length, 1, 'The textarea-input component appears in the DOM');
  });

  test('IPv4 using textarea-input', async function(assert) {
    assert.expect(9);
    const translation = this.owner.lookup('service:i18n');
    const expectedMessage = translation.t('adminUsm.groupCriteria.inputValidations.validIPv4List');
    new ReduxDataHelper(setState)
      .groupWiz()
      .build();
    const state = this.owner.lookup('service:redux').getState();
    // this.set('criteria', state.usm.groupWizard.group.groupCriteria.criteria[0]);
    this.set('criteria', [ 'IN', 'textarea-input', '' ]);
    this.set('criteriaPath', '');
    this.set('index', '2');
    this.set('operator', state.usm.groupWizard.groupAttributesMap.map[3][1][2]);
    await render(hbs`{{usm-groups/group/group-attributes/attribute-inputs/textarea-input inputField=operator.[1]  value=criteria.[2] validation=operator.[2] criteriaPath=(concat criteriaPath ',' index)}}`);
    // invalid blank input
    assert.equal(findAll('.textarea-input textarea').length, 1, 'The textarea-input for IPv4 appears in the DOM');
    assert.ok(find('.input-error'), 'Error is showing with invalid blank input');
    assert.equal(find('.input-error').textContent.trim(), expectedMessage, 'Correct error message is showing');
    // valid single input
    this.set('criteria', [ 'IN', 'textarea-input', '1.2.3.4' ]);
    assert.equal(findAll('.textarea-input textarea').length, 1, 'The textarea-input for IPv4 appears in the DOM');
    assert.notOk(find('.input-error'), 'Error is not showing for valid input');
    // invalid multiple inputs
    this.set('criteria', [ 'IN', 'textarea-input', '1.2.3.4, 7,8,9.a' ]);
    assert.equal(findAll('.textarea-input textarea').length, 1, 'The textarea-input for IPv4 appears in the DOM');
    assert.ok(find('.input-error'), 'Error is showing for invalid multiple inputs');
    // valid multiple inputs
    this.set('criteria', [ 'IN', 'textarea-input', '1.2.3.4, 5.6.7.8' ]);
    assert.equal(findAll('.textarea-input textarea').length, 1, 'The textarea-input for IPv4 appears in the DOM');
    assert.notOk(find('.input-error'), 'Error is not showing for valid multiple inputs');
  });

});
