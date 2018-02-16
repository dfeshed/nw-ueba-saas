import { moduleFor, test } from 'ember-qunit';
import engineResolverFor from '../../../helpers/engine-resolver';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import sinon from 'sinon';

moduleFor('route:alerts', 'Unit | Route | alerts', {
  integration: true,
  resolver: engineResolverFor('respond'),
  beforeEach() {
    this.inject.service('accessControl');
    initialize(this);
  }
});

test('it resolves the proper title token for the route', function(assert) {
  const route = this.subject();
  assert.ok(route);
  assert.equal(route.titleToken(), 'Alerts');
});

test('it transitions to "index" if the expected access control role is not set', function(assert) {
  const spy = sinon.spy();
  const route = this.subject({ transitionTo: spy });

  route.set('accessControl.roles', []);
  route.beforeModel();
  assert.equal(spy.calledWith('index'), true);
});

test('the contextual-help "topic" are set on activation and unset on deactivation of the route', function(assert) {
  const route = this.subject();
  assert.equal(route.get('contextualHelp.topic'), null, 'The contextual-help topic is null by default');

  route.activate();
  assert.equal(route.get('contextualHelp.topic'), route.get('contextualHelp.respAlrtListVw'), 'The contextual-help topic is updated on activation');

  route.deactivate();
  assert.equal(route.get('contextualHelp.topic'), null, 'The contextual-help topic is reverted to null on deactivate');
});
