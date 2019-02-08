import { module, test } from 'qunit';
import Service from '@ember/service';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { computed } from '@ember/object';
import { settled, waitUntil } from '@ember/test-helpers';
import IncidentRoute from 'respond/routes/incident';
import Immutable from 'seamless-immutable';
import { patchReducer } from '../../../helpers/vnext-patch';
import { patchSocket, throwSocket } from '../../../helpers/patch-socket';
import { getServices, isServicesLoading, isServicesRetrieveError } from 'respond/reducers/respond/recon/selectors';

const timeout = 10000;
const getServiceState = (state) => {
  const services = getServices(state);
  const loading = isServicesLoading(state);
  const error = isServicesRetrieveError(state);
  return {
    services,
    loading,
    error
  };
};

let redux, route, transition, hasPermission;

module('Unit | Route | incident', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    transition = null;
    hasPermission = true;
    initialize(this.owner);
    patchReducer(this, Immutable.from({}));
    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'incident'
    }));
    redux = this.owner.lookup('service:redux');
    const accessControl = Service.extend({
      hasRespondIncidentsAccess: computed(function() {
        return hasPermission;
      })
    }).create();
    const PatchedRoute = IncidentRoute.extend({
      redux: computed(function() {
        return redux;
      }),
      accessControl: computed(function() {
        return accessControl;
      }),
      transitionTo(routeName) {
        transition = routeName;
      }
    });
    route = PatchedRoute.create();
  });

  test('it transitions to "index" if the user does not have access', async function(assert) {
    assert.expect(1);

    hasPermission = false;

    await route.beforeModel();
    await settled();
    assert.equal(transition, 'index');
  });

  test('does not transition to "index" if the user has access', async function(assert) {
    assert.expect(1);

    hasPermission = true;

    await route.beforeModel();
    await settled();
    assert.equal(transition, null);
  });

  test('should fetch services and push service data into redux', async function(assert) {
    assert.expect(12);

    let { services, loading, error } = getServiceState(redux.getState());
    assert.equal(loading, undefined);
    assert.equal(error, undefined);
    assert.equal(services, undefined);

    patchSocket((method, modelName, query) => {
      assert.equal(method, 'findAll');
      assert.equal(modelName, 'core-service');
      assert.deepEqual(query, {});
    });

    route.beforeModel();

    ({ services, loading, error } = getServiceState(redux.getState()));
    assert.equal(loading, true);
    assert.equal(error, undefined);
    assert.equal(services, undefined);

    await waitUntil(() => {
      ({ services, loading, error } = getServiceState(redux.getState()));
      return services && Object.keys(services).length === 4;
    }, { timeout });

    assert.equal(loading, false);
    assert.equal(error, false);
    assert.deepEqual(services, {
      '555d9a6fe4b0d37c827d402e': {
        displayName: 'local-concentrator',
        host: '127.0.0.1',
        id: '555d9a6fe4b0d37c827d402e',
        name: 'CONCENTRATOR',
        port: 56005,
        version: '10.6.0.0'
      },
      '555d9a6fe4b0d37c827d4021': {
        displayName: 'loki-broker',
        host: '10.4.61.28',
        id: '555d9a6fe4b0d37c827d4021',
        name: 'BROKER',
        port: 56003,
        version: '11.1.0.0'
      },
      '555d9a6fe4b0d37c827d402d': {
        displayName: 'loki-concentrator',
        host: '10.4.61.33',
        id: '555d9a6fe4b0d37c827d402d',
        name: 'CONCENTRATOR',
        port: 56005,
        version: '11.2.0.0'
      },
      '555d9a6fe4b0d37c827d402f': {
        displayName: 'qamac01-concentrator',
        host: '10.4.61.48',
        id: '555d9a6fe4b0d37c827d402f',
        name: 'CONCENTRATOR',
        port: 56005,
        version: '11.1.0.0'
      }
    });
  });

  test('any failure fetching services will recover gracefully', async function(assert) {
    assert.expect(9);

    let { services, loading, error } = getServiceState(redux.getState());
    assert.equal(loading, undefined);
    assert.equal(error, undefined);
    assert.equal(services, undefined);

    const done = throwSocket({ methodToThrow: 'findAll', modelNameToThrow: 'core-service' });

    route.beforeModel();

    ({ services, loading, error } = getServiceState(redux.getState()));
    assert.equal(loading, true);
    assert.equal(error, undefined);
    assert.deepEqual(services, undefined);

    await waitUntil(() => {
      ({ services, loading, error } = getServiceState(redux.getState()));
      return loading === false;
    });

    assert.equal(loading, false);
    assert.equal(error, true);
    assert.deepEqual(services, undefined);

    return settled().then(() => done());
  });
});
