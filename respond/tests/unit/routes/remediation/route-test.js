import { moduleFor, test } from 'ember-qunit';
import engineResolverFor from '../../../helpers/engine-resolver';

moduleFor('route:tasks', 'Unit | Route | tasks', {
  needs: ['service:accessControl', 'service:contextualHelp', 'service:i18n'],
  resolver: engineResolverFor('respond')
});

test('it exists', function(assert) {
  const route = this.subject();
  assert.ok(route);
});
