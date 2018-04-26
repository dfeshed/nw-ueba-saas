import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import engineResolverFor from '../../../helpers/engine-resolver';

module('Unit | Component | Query Pills', function(hooks) {
  setupTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  test('Handle pill initialization', function(assert) {
    const comp = this.owner.lookup('component:query-container/query-pills');
    const pillId = 'pill1';
    comp._pillInitialized(pillId);
    const filterMap = comp.get('_filterMap');
    const filter = filterMap.get(pillId);
    assert.equal(filter, null, 'Null filter was not created');
  });

  test('Handle pill creation', function(assert) {
    const comp = this.owner.lookup('component:query-container/query-pills');
    const pillId = 'pill1';
    const pillObj = { meta: 'foo', operator: '=', value: 'bar' };
    comp._pillCreated(pillId, pillObj);

    // Test for addition to filter map
    const filterMap = comp.get('_filterMap');
    const filter = filterMap.get(pillId);
    assert.deepEqual(filter, pillObj, 'Filter was not created');

    // Test for addition to 'filters' array
    const filtersAsArray = comp.get('filters');
    assert.equal(filtersAsArray.length, 1);
    assert.equal(filtersAsArray[0].meta, pillObj.meta, 'Meta does not match');
    assert.equal(filtersAsArray[0].operator, pillObj.operator, 'Operator does not match');
    assert.equal(filtersAsArray[0].value, pillObj.value, 'Value does not match');
    assert.ok(filtersAsArray[0].saved, 'Property "saved" was not set or was false');
  });
});