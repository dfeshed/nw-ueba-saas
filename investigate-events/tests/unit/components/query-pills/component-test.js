import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../helpers/redux-data-helper';

module('Unit | Component | Query Pills', function(hooks) {
  setupTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('Handle pill creation', function(assert) {
    new ReduxDataHelper((state) => patchReducer(this, state)).language().build();
    const comp = this.owner.lookup('component:query-container/query-pills');
    const pillPosition = 0;
    const pillObj = { meta: 'a', operator: '=', value: 'bar' };
    comp._pillCreated(pillObj, pillPosition);

    // Test for addition to 'filters' array
    const filtersAsArray = comp.get('filters');
    assert.equal(filtersAsArray.length, 1);
    assert.equal(filtersAsArray[0].meta, pillObj.meta, 'Meta does not match');
    assert.equal(filtersAsArray[0].operator, pillObj.operator, 'Operator does not match');
    assert.equal(filtersAsArray[0].value, pillObj.value, 'Value does not match');
    assert.ok(filtersAsArray[0].saved, 'Property "saved" was not set or was false');
  });

});