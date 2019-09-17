import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

module('Unit | Component | Pill Value', function(hooks) {
  setupTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  test('Properly determine if input is considered "empty"', function(assert) {
    const comp = this.owner.lookup('component:query-container/pill-value');
    assert.ok(comp._isInputEmpty([]), 'Empty array should be empty');
  });

  test('PowerSelect filter function filters properly', function(assert) {
    const comp = this.owner.lookup('component:query-container/pill-value');
    const m1 = { displayName: 'foo', description: 'Query Filter' };
    const m2 = { displayName: 'bar' };
    const m3 = { displayName: 'barfoo' };
    const m4 = { displayName: 'zoom' };
    const m5 = { displayName: 'gibberish', description: 'TEST' };

    assert.equal(comp._matcher(m1, 'foo'), 0, 'Did not match the default option');
    assert.equal(comp._matcher(m2, 'barr'), -1, 'Found item but should not have');
    assert.equal(comp._matcher(m3, 'barfoo'), 0, 'Did not find item');
    assert.equal(comp._matcher(m4, 'zo'), 0, 'Did not find the item');
    assert.equal(comp._matcher(m5, 'tes'), 0, 'Should have matched aliases');
  });

  test('_highlighter returns nothing if no text is present in the input', function(assert) {
    const comp = this.owner.lookup('component:query-container/pill-value');
    const powerSelectAPI = {
      searchText: '',
      results: ['foo']
    };
    assert.notOk(comp._highlighter(powerSelectAPI), 'Should have highlighted nothing');
  });

  test('_highlighter returns the first result if text is present in the input', function(assert) {
    const comp = this.owner.lookup('component:query-container/pill-value');
    const powerSelectAPI = {
      searchText: 'fo',
      results: ['foo']
    };
    assert.equal(comp._highlighter(powerSelectAPI), powerSelectAPI.results[0], 'Should have highlighted the first option');
  });
});