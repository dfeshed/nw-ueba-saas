import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

module('Unit | Component | Query Pill', function(hooks) {
  setupTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  test('Handles messages from children properly', function(assert) {
    const comp = this.owner.lookup('component:query-container/query-pill');
    comp._metaSelected('a');
    assert.equal(comp.get('selectedMeta'), 'a', 'Wrong meta value');
    assert.notOk(comp.get('isMetaActive'), 'Should be false');
    assert.ok(comp.get('isOperatorActive'), 'Should be true');
    assert.notOk(comp.get('isValueActive'), 'Should be false');
  });

  test('Gets proper value from source map', function(assert) {
    const comp = this.owner.lookup('component:query-container/query-pill');
    const fn = comp._getStringFromSource({
      'a': 'a',
      'b': 'b'
    });
    assert.equal(fn('a'), 'a', 'is "a"');
    assert.equal(fn('b'), 'b', 'is "b"');
    assert.notEqual(fn('c'), 'c', 'is not "c"');
  });

  test('Properly generates a Free-Form string', function(assert) {
    const comp = this.owner.lookup('component:query-container/query-pill');
    comp.setProperties({
      selectedMeta: { metaName: 'a' },
      selectedOperator: { displayName: 'b' }
    });
    assert.equal(comp._getFreeFormString('a', 'meta'), 'a', 'Meta generates proper string');
    assert.equal(comp._getFreeFormString('b', 'operator'), 'a b', 'Operator generates proper string');
    assert.equal(comp._getFreeFormString('c', 'value', 'a', 'b'), 'a b c', 'Value generates proper string');
    assert.equal(comp._getFreeFormString('d', 'foo', 'a', 'b'), null, 'Value is null because "foo" is an invalid key');
  });
});