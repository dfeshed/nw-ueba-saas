import { moduleFor, test } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import sinon from 'sinon';

moduleFor('route:investigate-users', 'Unit | Route | Users', {
  integration: true,
  resolver: engineResolverFor('investigate'),
  beforeEach() {
    this.inject.service('accessControl');
    this.inject.service('i18n');
    initialize(this);
  }
});

test('it transitions to "investigate-events" if the expected access control authority is not set', function(assert) {
  const spy = sinon.spy();
  const route = this.subject({ transitionToExternal: spy });

  route.set('accessControl.authorities', []);
  route.beforeModel();
  assert.equal(spy.calledWith('investigate.investigate-events'), true);
});

test('it transitions to "investigate-events" if the expected access control authority is not set', function(assert) {
  const spy = sinon.spy();
  const route = this.subject({ transitionToExternal: spy });

  route.set('accessControl.authorities', ['UEBA_Analysts']);
  route.beforeModel();
  assert.equal(spy.calledWith('investigate.investigate-events'), false);
});