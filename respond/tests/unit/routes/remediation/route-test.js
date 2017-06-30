import { moduleFor, test } from 'ember-qunit';
import engineResolverFor from '../../../helpers/engine-resolver';

moduleFor('route:remediation', 'Unit | Route | remediation', {
  needs: ['service:accessControl', 'service:contextualHelp', 'service:i18n'],
  resolver: engineResolverFor('respond')
});

test('it exists', function(assert) {
  const route = this.subject();
  assert.ok(route);
});
