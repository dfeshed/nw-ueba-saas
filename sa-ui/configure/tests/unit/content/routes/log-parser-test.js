import { moduleFor, test } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import sinon from 'sinon';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import * as logParserCreators from 'configure/actions/creators/content/log-parser-rule-creators';

moduleFor('route:content/log-parser-rules', 'Unit | Route | log parser rules', {
  integration: true,
  resolver: engineResolverFor('configure'),
  beforeEach() {
    this.inject.service('accessControl');
    initialize(this);
  }
});

test('it resolves the proper title token for the route', function(assert) {
  const route = this.subject();
  assert.ok(route);
  assert.equal(route.titleToken(), 'Log Parsers');
});

test('it transitions to "index" if the expected access control role is not set', function(assert) {
  const spy = sinon.spy();
  const route = this.subject();
  route.transitionToExternal = spy;
  route.set('accessControl.roles', []);
  route.beforeModel();
  assert.equal(spy.calledWith('protected'), true);
});

test('ensure the expected action creator is called', function(assert) {
  const initParserRules = sinon.spy(logParserCreators, 'initializeLogParserRules');
  const route = this.subject();
  route.model();
  assert.equal(initParserRules.calledOnce, true, 'initializeLogParserRules is called once');
  initParserRules.restore();
});
