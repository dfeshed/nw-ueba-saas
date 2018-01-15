import { moduleFor, test } from 'ember-qunit';
import engineResolverFor from '../../../../../helpers/engine-resolver';

moduleFor('route:respond/notifications', 'Unit | Route | respond/notifications', {
  needs: ['service:accessControl', 'service:contextualHelp', 'service:i18n', 'service:redux'],
  resolver: engineResolverFor('configure')
});

test('it exists', function(assert) {
  const route = this.subject();
  assert.ok(route);
});
