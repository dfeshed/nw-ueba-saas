import { moduleFor, test } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import * as incidentCreators from 'respond/actions/creators/incidents-creators';
import sinon from 'sinon';

moduleFor('route:incidents', 'Unit | Route | incidents', {
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
  assert.equal(route.titleToken(), 'Incidents');
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
  assert.equal(route.get('contextualHelp.topic'), route.get('contextualHelp.respIncListVw'), 'The contextual-help topic is updated on activation');

  route.deactivate();
  assert.equal(route.get('contextualHelp.topic'), null, 'The contextual-help topic is reverted to null on deactivate');
});

test('ensure the expected action creator is called', function(assert) {
  const initializeIncidents = sinon.spy(incidentCreators, 'initializeIncidents');
  const route = this.subject();
  route.model();
  assert.equal(initializeIncidents.calledOnce, true, 'initializeIncidents is called once');
  initializeIncidents.restore();
});
