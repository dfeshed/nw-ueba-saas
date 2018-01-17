import { moduleFor, test } from 'ember-qunit';
import engineResolverFor from '../../../../../helpers/engine-resolver';

moduleFor('route:respond/notifications', 'Unit | Route | respond/notifications', {
  needs: ['service:accessControl', 'service:contextualHelp', 'service:i18n', 'service:redux', 'service:appVersion'],
  resolver: engineResolverFor('configure')
});

test('it exists', function(assert) {
  const route = this.subject();
  assert.ok(route);
});

test('the contextual-help "module" and "topic" are set on activation and unset on deactivation of the route', function(assert) {
  const route = this.subject();
  assert.equal(route.get('contextualHelp.module'), null, 'The contextual-help module is null by default');
  assert.equal(route.get('contextualHelp.topic'), null, 'The contextual-help topic is null by default');

  route.activate();
  assert.equal(route.get('contextualHelp.module'), route.get('contextualHelp.respondModule'), 'The contextual-help module is updated on activation');
  assert.equal(route.get('contextualHelp.topic'), route.get('contextualHelp.respNotifSetVw'), 'The contextual-help topic is updated on activation');

  route.deactivate();
  assert.equal(route.get('contextualHelp.module'), null, 'The contextual-help module is reverted to null on deactivate');
  assert.equal(route.get('contextualHelp.topic'), null, 'The contextual-help topic is reverted to null on deactivate');
});
