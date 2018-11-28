import { module, test } from 'qunit';
import Service from '@ember/service';
import { setupTest } from 'ember-qunit';
import { patchSocket, throwSocket } from '../../helpers/patch-socket';
import { patchReducer } from '../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import { computed } from '@ember/object';
import { getServices, isServicesLoading, isServicesRetrieveError } from 'respond/reducers/respond/recon/selectors';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { settled, waitUntil } from '@ember/test-helpers';
import ApplicationRoute from 'respond/routes/application';

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

let route, redux;

module('Unit | Route | application', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
    patchReducer(this, Immutable.from({}));
    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'application'
    }));
    redux = this.owner.lookup('service:redux');
    const PatchedRoute = ApplicationRoute.extend({
      redux: computed(function() {
        return redux;
      })
    });
    route = PatchedRoute.create();
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

    route.model();

    ({ services, loading, error } = getServiceState(redux.getState()));
    assert.equal(loading, true);
    assert.equal(error, undefined);
    assert.equal(services, undefined);

    await waitUntil(() => {
      ({ services, loading, error } = getServiceState(redux.getState()));
      return services && Object.keys(services).length === 4;
    }, { timeout: 8000 });

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

    route.model();

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
