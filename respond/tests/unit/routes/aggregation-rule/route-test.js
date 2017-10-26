import { moduleFor, test } from 'ember-qunit';
import engineResolverFor from '../../../helpers/engine-resolver';

moduleFor('route:aggregation-rule', 'Unit | Route | aggregation-rule', {
  needs: ['service:accessControl', 'service:contextualHelp', 'service:i18n', 'service:redux'],
  resolver: engineResolverFor('respond')
});

test('it exists', function(assert) {
  const route = this.subject();
  assert.ok(route);
});
