import { moduleFor, test } from 'ember-qunit';

moduleFor('controller:login', {
  // Specify the other units that are required for this test.
  // needs: ['controller:foo']
});

// Replace this with your real tests.
test('it exists', function(assert) {
  let controller = this.subject();
  assert.ok(controller);
});

test('login controller test', function(assert) {
  assert.expect(0);
  let controller = this.subject();
  controller.send('authenticate');
});
