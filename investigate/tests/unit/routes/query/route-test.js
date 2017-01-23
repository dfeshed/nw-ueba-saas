import { moduleFor, test } from 'ember-qunit';
import engineResolverFor from '../../../helpers/engine-resolver';

moduleFor('route:query', 'Unit | Route | query', {
  // Specify the other units that are required for this test.
  // needs: ['controller:foo']
  resolver: engineResolverFor('investigate')
});

test('it exists', function(assert) {
  const route = this.subject();
  assert.ok(route);
});
