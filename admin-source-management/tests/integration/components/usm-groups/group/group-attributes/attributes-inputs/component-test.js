import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, findAll } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../../helpers/vnext-patch';

let setState;

module('Integration | Component | attributes-inputs', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });
  test('Component attributes-inputs', async function(assert) {
    new ReduxDataHelper(setState).groupWiz().build();
    const state = this.owner.lookup('service:redux').getState();
    this.set('criteria', state.usm.groupWizard.group.groupCriteria.criteria[0]);
    this.set('criteriaPath', '');
    this.set('index', '0');
    this.set('oprt', state.usm.groupWizard.groupAttributesMap.map[0][1][0]);
    await render(hbs`{{usm-groups/group/group-attributes/attribute-inputs inputField=oprt.[1]  value=criteria.[2] value2=criteria.[3] validation=oprt.[2] criteriaPath=(concat criteriaPath ',' index)}}`);
    assert.equal(findAll('.osSelector').length, 1, 'The osSelector appears in the DOM');

    this.set('oprt', state.usm.groupWizard.groupAttributesMap.map[1][1][0]);
    await render(hbs`{{usm-groups/group/group-attributes/attribute-inputs inputField=oprt.[1]  value=criteria.[2] value2=criteria.[3] validation=oprt.[2] criteriaPath=(concat criteriaPath ',' index)}}`);
    assert.equal(findAll('.text-input-field').length, 1, 'The textInput for osDescription appears in the DOM');

    this.set('oprt', state.usm.groupWizard.groupAttributesMap.map[2][1][0]);
    await render(hbs`{{usm-groups/group/group-attributes/attribute-inputs inputField=oprt.[1]  value=criteria.[2] value2=criteria.[3] validation=oprt.[2] criteriaPath=(concat criteriaPath ',' index)}}`);
    assert.equal(findAll('.text-input-field').length, 1, 'The textInput for hostname appears in the DOM');

    this.set('oprt', state.usm.groupWizard.groupAttributesMap.map[3][1][0]);
    await render(hbs`{{usm-groups/group/group-attributes/attribute-inputs inputField=oprt.[1]  value=criteria.[2] value2=criteria.[3] validation=oprt.[2] criteriaPath=(concat criteriaPath ',' index)}}`);
    assert.equal(findAll('.between-inputs').length, 1, 'The between-inputs for ipv4 appears in the DOM');

    this.set('oprt', state.usm.groupWizard.groupAttributesMap.map[4][1][0]);
    await render(hbs`{{usm-groups/group/group-attributes/attribute-inputs inputField=oprt.[1]  value=criteria.[2] value2=criteria.[3] validation=oprt.[2] criteriaPath=(concat criteriaPath ',' index)}}`);
    assert.equal(findAll('.between-inputs').length, 1, 'The between-inputs for ipv6 appears in the DOM');
  });

  // TODO validation
});
