import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

module('Unit | Component | Query Pills', function(hooks) {
  setupTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  test('Handle pill creation', function(assert) {
    const comp = this.owner.lookup('component:query-container/query-pills');
    const pillPosition = 0;
    const pillObj = { meta: 'foo', operator: '=', value: 'bar' };
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