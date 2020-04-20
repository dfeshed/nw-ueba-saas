import enhance from 'respond/utils/enhance-users';
import { module, test } from 'qunit';

module('Unit | Utils | enhance-users', function() {

  test('enhance-users skips undefined', function(assert) {
    assert.equal(enhance(undefined, 'foobar'), undefined, 'no enhancement for undefined');
  });

  test('enhance-users skips if username not found', function(assert) {
    const input = [{ id: '1', name: 'a' }, { id: '2', name: 'b' }];
    assert.equal(enhance(input, 'foobar'), input, 'no enhancement for missing username');
  });

  test('enhance-users works if username found', function(assert) {
    const input = [{ id: '1', name: 'a' }, { id: 'foobar', name: 'b' }];
    assert.deepEqual(enhance(input, 'foobar'), [{ id: 'foobar', name: 'Myself (b)' }, { id: '1', name: 'a' }], 'enhancement works');
  });
});

