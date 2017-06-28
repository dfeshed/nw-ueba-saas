import { moduleFor, test } from 'ember-qunit';
import engineResolverFor from '../../../helpers/engine-resolver';

moduleFor('route:alerts', 'Unit | Route | alerts', {
  needs: ['service:accessControl', 'service:contextualHelp'],
  resolver: engineResolverFor('respond')
});

test('it exists', function(assert) {
  const route = this.subject();
  assert.ok(route);
});
