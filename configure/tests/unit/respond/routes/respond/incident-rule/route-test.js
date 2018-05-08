import { moduleFor, test } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

moduleFor('route:respond/incident-rule', 'Unit | Route | respond/incident rule', {
  needs: ['service:accessControl', 'service:contextualHelp', 'service:i18n', 'service:redux'],
  resolver: engineResolverFor('configure')
});

test('it exists', function(assert) {
  const route = this.subject();
  assert.ok(route);
});

