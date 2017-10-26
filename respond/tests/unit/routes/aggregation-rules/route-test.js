import { moduleFor, test } from 'ember-qunit';
import engineResolverFor from '../../../helpers/engine-resolver';

moduleFor('route:aggregation-rules', 'Unit | Route | aggregation-rules', {
  needs: ['service:accessControl', 'service:contextualHelp', 'service:i18n', 'service:redux'],
  resolver: engineResolverFor('respond')
});

test('it exists', function(assert) {
  const route = this.subject();
  assert.ok(route);
});
