import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

module('Unit | Component | Pill Meta', function(hooks) {
  setupTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  test('PowerSelect filter function filters properly', function(assert) {
    const comp = this.owner.lookup('component:query-container/pill-meta');
    const m1 = { displayName: 'foo', metaName: 'bar' };
    const m2 = { displayName: 'bar', metaName: 'baz' };
    const m3 = { displayName: 'bar', metaName: 'baz' };

    assert.equal(comp._matcher(m1, 'foo'), 0, 'Did not find item in "displayName"');
    assert.equal(comp._matcher(m1, 'baz'), -1, 'Found item but should not have');
    assert.equal(comp._matcher(m2, 'foo'), -1, 'Found item but should not have');
    assert.equal(comp._matcher(m2, 'baz'), 0, 'Did not find item in "metaName"');
    assert.equal(comp._matcher(m3, '   baz'), 0, 'Did not ignore leading spaces');
  });

  test('Can properly detect an exact meta match', function(assert) {
    const comp = this.owner.lookup('component:query-container/pill-meta');
    const metas = [
      { displayName: 'Foo', metaName: 'foo' },
      { displayName: 'Foo Bar', metaName: 'foo.bar' },
      { displayName: 'Baz', metaName: 'baz' }
    ];
    assert.notOk(comp._hasExactMatch('foobar', metas), 'Erroneously found matching item');
    assert.equal(comp._hasExactMatch('foo', metas), metas[0], 'Did not find matching item');
  });
});