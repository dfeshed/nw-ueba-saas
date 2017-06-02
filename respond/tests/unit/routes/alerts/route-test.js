import { moduleFor, test } from 'ember-qunit';
import engineResolverFor from '../../../helpers/engine-resolver';

moduleFor('route:alerts', 'Unit | Route | alerts', {
  // Specify the other units that are required for this test.
  // needs: ['controller:foo']
  resolver: engineResolverFor('respond')
});

test('it exists', function(assert) {
  const route = this.subject();
  assert.ok(route);
});
