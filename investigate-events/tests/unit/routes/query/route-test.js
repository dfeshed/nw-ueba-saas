import { moduleFor, test } from 'ember-qunit';
import engineResolverFor from '../../../helpers/engine-resolver';

moduleFor('route:query', 'Unit | Route | query', {
  // Specify the other units that are required for this test.
  needs: [
    'service:accessControl',
    'service:contextualHelp',
    'service:redux'
  ],
  resolver: engineResolverFor('investigate-events')
});

test('it exists', function(assert) {
  const route = this.subject();
  assert.ok(route);
});
