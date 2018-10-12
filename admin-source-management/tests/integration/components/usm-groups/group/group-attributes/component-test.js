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
    await render(hbs`{{usm-groups/group/group-attributes}}`);
    assert.equal(findAll('.group-attributes').length, 1, 'The component group-attributes appears in the DOM');
    assert.equal(findAll('.attribute').length, 1, 'The selector attribute appears in the DOM');
    assert.equal(findAll('.operator').length, 1, 'The selector operator appears in the DOM');
    assert.equal(findAll('.add-criteria-button').length, 1, 'The add-criteria-button button appears in the DOM');
    // default attribute is osSelector
    assert.equal(findAll('.os-selector').length, 1, 'The osSelector appears in the DOM');
  });

  test('Select group-attributes each attribute', async function(assert) {
    new ReduxDataHelper(setState).groupWiz().build();
    let state = this.owner.lookup('service:redux').getState();
    await render(hbs`{{usm-groups/group/group-attributes}}`);
    // osType
    await selectChoose('.group-attributes .attribute', '.ember-power-select-option', 0);
    state = this.owner.lookup('service:redux').getState();
    assert.equal(state.usm.groupWizard.group.groupCriteria.criteria[0][1], 'IN', 'osType operator field value is not showing or value is not "IN"');
    // osDescription
    await render(hbs`{{usm-groups/group/group-attributes}}`);
    await selectChoose('.group-attributes .attribute', '.ember-power-select-option', 1);
    state = this.owner.lookup('service:redux').getState();
    assert.equal(state.usm.groupWizard.group.groupCriteria.criteria[0][1], 'EQUAL', 'osDescription operator field value is not showing or value is not "EQUAL"');
    // hostname
    await selectChoose('.group-attributes .attribute', '.ember-power-select-option', 2);
    state = this.owner.lookup('service:redux').getState();
    assert.equal(state.usm.groupWizard.group.groupCriteria.criteria[0][1], 'EQUAL', 'hostname operator field value is not showing or value is not "EQUAL"');
    // ipv4
    await selectChoose('.group-attributes .attribute', '.ember-power-select-option', 3);
    state = this.owner.lookup('service:redux').getState();
    assert.equal(state.usm.groupWizard.group.groupCriteria.criteria[0][1], 'BETWEEN', 'ipv4 operator field value is not showing or value is not "EQUAL"');
    // ipv6
    await selectChoose('.group-attributes .attribute', '.ember-power-select-option', 4);
    state = this.owner.lookup('service:redux').getState();
    assert.equal(state.usm.groupWizard.group.groupCriteria.criteria[0][1], 'BETWEEN', 'ipv6 operator field value is not showing or value is not "EQUAL"');
  });

  test('Add Criteria', async function(assert) {
    new ReduxDataHelper(setState).groupWiz().build();
    let state = this.owner.lookup('service:redux').getState();
    await render(hbs`{{usm-groups/group/group-attributes}}`);
    assert.equal(findAll('.add-criteria-button button').length, 1, 'The add-criteria-button button appears in the DOM');
    assert.equal(state.usm.groupWizard.group.groupCriteria.criteria.length, 1, 'A single criteria is present');
    await click('.add-criteria-button button');
    state = this.owner.lookup('service:redux').getState();
    assert.equal(state.usm.groupWizard.group.groupCriteria.criteria.length, 2, 'A new criteria was added');
    await click('.add-criteria-button button');
    await click('.add-criteria-button button');
    await click('.add-criteria-button button');
    await click('.add-criteria-button button');
    await click('.add-criteria-button button');
    await click('.add-criteria-button button');
    await click('.add-criteria-button button');
    await click('.add-criteria-button button');
    state = this.owner.lookup('service:redux').getState();
    assert.equal(state.usm.groupWizard.group.groupCriteria.criteria.length, 10, '8 new criteria were added to a max of ten');
    await click('.add-criteria-button button');
    state = this.owner.lookup('service:redux').getState();
    assert.equal(state.usm.groupWizard.group.groupCriteria.criteria.length, 10, 'No more criteria can be added');
    assert.equal(findAll('.maxTenCriteria').length, 1, 'The message for maxTenCriteria appears in the DOM');
  });

  test('Remove Criteria', async function(assert) {
    new ReduxDataHelper(setState).groupWiz().build();
    let state = this.owner.lookup('service:redux').getState();
    await render(hbs`{{usm-groups/group/group-attributes}}`);
    assert.equal(findAll('.remove-criteria').length, 1, 'A remove-criteria button appears in the DOM');
    assert.equal(state.usm.groupWizard.group.groupCriteria.criteria.length, 1, 'A single criteria is present');
    await click('.remove-criteria');
    state = this.owner.lookup('service:redux').getState();
    assert.equal(state.usm.groupWizard.group.groupCriteria.criteria.length, 0, 'No criteria are present');
  });
});