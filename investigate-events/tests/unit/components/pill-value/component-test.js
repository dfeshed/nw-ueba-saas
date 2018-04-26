import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import engineResolverFor from '../../../helpers/engine-resolver';

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
    assert.notOk(comp._isInputEmpty('foo"bar"baz'), 'Text with inner double quotes should not be empty');
  });
});