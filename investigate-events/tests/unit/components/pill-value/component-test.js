import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

module('Unit | Component | Pill Value', function(hooks) {
  setupTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  test('Properly determine if input is considered "empty"', function(assert) {
    const comp = this.owner.lookup('component:query-container/pill-value');
    assert.ok(comp._isInputEmpty(''), 'Empty string should be empty');
    assert.ok(comp._isInputEmpty('\'\''), 'Single quotes should be empty');
    assert.ok(comp._isInputEmpty('""'), 'Double quotes should be empty');
    assert.ok(comp._isInputEmpty('   '), 'Spaces should be empty');
    assert.ok(comp._isInputEmpty('\'   \''), 'Single quoted spaces should be empty');
    assert.ok(comp._isInputEmpty('"   "'), 'Double quoted spaces should be empty');
    assert.ok(comp._isInputEmpty('  \'\'  '), 'Single quotes with padding should be empty');
    assert.ok(comp._isInputEmpty('  ""   '), 'Double quotes with padding should be empty');
    assert.ok(comp._isInputEmpty('  \'   \'  '), 'Single quoted spaces with padding should be empty');
    assert.ok(comp._isInputEmpty('  "   "  '), 'Double quoted spaces with padding should be empty');
    assert.notOk(comp._isInputEmpty('foo'), 'Text should not be empty');
    assert.notOk(comp._isInputEmpty('\'foo\''), 'Single quoted text should not be empty');
    assert.notOk(comp._isInputEmpty('"foo"'), 'Double quoted text should not be empty');
    assert.notOk(comp._isInputEmpty('  foo  '), 'Space padded text should not be empty');
    assert.notOk(comp._isInputEmpty('  \'foo\'  '), 'Space padded single quoted text should not be empty');
    assert.notOk(comp._isInputEmpty('  "foo"  '), 'Space padded double quoted text should not be empty');
    assert.notOk(comp._isInputEmpty('foo\'bar'), 'Text with inner single quote should not be empty');
    assert.notOk(comp._isInputEmpty('foo"bar'), 'Text with inner double quote should not be empty');
    assert.notOk(comp._isInputEmpty('foo\'bar\'baz'), 'Text with inner single quotes should not be empty');
  });

  test('PowerSelect filter function filters properly', function(assert) {
    const comp = this.owner.lookup('component:query-container/pill-value');
    const m1 = { displayName: 'foo', description: 'Query Filter' };
    const m2 = { displayName: 'bar' };
    const m3 = { displayName: 'barfoo' };

    assert.equal(comp._matcher(m1, 'foo'), 0, 'Did not match the default option');
    assert.equal(comp._matcher(m2, 'barr'), -1, 'Found item but should not have');
    assert.equal(comp._matcher(m3, 'barfoo'), 0, 'Did not find item');
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