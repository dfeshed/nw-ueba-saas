import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, findAll, click } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { selectChoose } from 'ember-power-select/test-support/helpers';

let setState;

module('Integration | Component | group-attributes', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  test('The component group-attributes appears in the DOM', async function(assert) {
    new ReduxDataHelper(setState).groupWiz().build();
    const state = this.owner.lookup('service:redux').getState();
    this.set('groupCriteria', state.usm.groupWizard.group.groupCriteria.criteria);
    await render(hbs`{{usm-groups/group/group-attributes criterias=groupCriteria criteriaPath=''}}`);
    assert.equal(findAll('.group-attributes').length, 1, 'The component group-attributes appears in the DOM');
    assert.equal(findAll('.attribute').length, 1, 'The selector attribute appears in the DOM');
    assert.equal(findAll('.operator').length, 1, 'The selector operator appears in the DOM');
    assert.equal(findAll('.add-criteria-button').length, 1, 'The add-criteria-button button appears in the DOM');
    // default attribute is osSelector
    assert.equal(findAll('.osSelector').length, 1, 'The osSelector appears in the DOM');
  });

  test('Select group-attributes each attribute', async function(assert) {
    new ReduxDataHelper(setState).groupWiz().build();
    let state = this.owner.lookup('service:redux').getState();
    this.set('groupCriteria', state.usm.groupWizard.group.groupCriteria.criteria);
    await render(hbs`{{usm-groups/group/group-attributes criterias=groupCriteria criteriaPath=''}}`);
    // osType
    await selectChoose('.group-attributes .attribute', '.ember-power-select-option', 0);
    state = this.owner.lookup('service:redux').getState();
    assert.equal(state.usm.groupWizard.group.groupCriteria.criteria[0][1], 'IN', 'osType operator field value is not showing or value is not "IN"');
    // osDescription
    await render(hbs`{{usm-groups/group/group-attributes criterias=groupCriteria criteriaPath=''}}`);
    await selectChoose('.group-attributes .attribute', '.ember-power-select-option', 1);
    state = this.owner.lookup('service:redux').getState();
    assert.equal(state.usm.groupWizard.group.groupCriteria.criteria[0][1], 'EQUALS', 'osDescription operator field value is not showing or value is not "EQUALS"');
    // hostname
    await selectChoose('.group-attributes .attribute', '.ember-power-select-option', 2);
    state = this.owner.lookup('service:redux').getState();
    assert.equal(state.usm.groupWizard.group.groupCriteria.criteria[0][1], 'EQUALS', 'hostname operator field value is not showing or value is not "EQUALS"');
    // ipv4
    await selectChoose('.group-attributes .attribute', '.ember-power-select-option', 3);
    state = this.owner.lookup('service:redux').getState();
    assert.equal(state.usm.groupWizard.group.groupCriteria.criteria[0][1], 'BETWEEN', 'ipv4 operator field value is not showing or value is not "EQUALS"');
    // ipv6
    await selectChoose('.group-attributes .attribute', '.ember-power-select-option', 4);
    state = this.owner.lookup('service:redux').getState();
    assert.equal(state.usm.groupWizard.group.groupCriteria.criteria[0][1], 'BETWEEN', 'ipv6 operator field value is not showing or value is not "EQUALS"');
    // agentMode
    await selectChoose('.group-attributes .attribute', '.ember-power-select-option', 5);
    state = this.owner.lookup('service:redux').getState();
    assert.equal(state.usm.groupWizard.group.groupCriteria.criteria[0][1], 'EQUALS', 'agentMode operator field value is not showing or value is not "EQUALS"');
  });

  test('Add Criteria', async function(assert) {
    new ReduxDataHelper(setState).groupWiz().build();
    let state = this.owner.lookup('service:redux').getState();
    this.set('groupCriteria', state.usm.groupWizard.group.groupCriteria.criteria);
    await render(hbs`{{usm-groups/group/group-attributes criterias=groupCriteria criteriaPath=''}}`);
    assert.equal(findAll('.add-criteria-button button').length, 1, 'The add-criteria-button button appears in the DOM');
    assert.equal(state.usm.groupWizard.group.groupCriteria.criteria.length, 1, 'A single criteria is present');
    await click('.add-criteria-button button');
    state = this.owner.lookup('service:redux').getState();
    assert.equal(state.usm.groupWizard.group.groupCriteria.criteria.length, 2, 'A new criteria was added');
  });

  test('Remove Criteria', async function(assert) {
    new ReduxDataHelper(setState).groupWiz().build();
    let state = this.owner.lookup('service:redux').getState();
    this.set('groupCriteria', state.usm.groupWizard.group.groupCriteria.criteria);
    await render(hbs`{{usm-groups/group/group-attributes criterias=groupCriteria criteriaPath=''}}`);
    assert.equal(findAll('.remove-criteria').length, 1, 'A remove-criteria button appears in the DOM');
    assert.equal(state.usm.groupWizard.group.groupCriteria.criteria.length, 1, 'A single criteria is present');
    await click('.remove-criteria');
    state = this.owner.lookup('service:redux').getState();
    assert.equal(state.usm.groupWizard.group.groupCriteria.criteria.length, 0, 'No criteria are present');
  });
});