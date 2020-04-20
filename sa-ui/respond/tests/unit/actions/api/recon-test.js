import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { recon } from 'respond/actions/api';
import { bindActionCreators } from 'redux';
import { getServices, isServicesLoading, isServicesRetrieveError } from 'respond/reducers/respond/recon/selectors';
import Immutable from 'seamless-immutable';
import { patchReducer } from '../../../helpers/vnext-patch';
import { patchSocket, revertPatch } from '../../../helpers/patch-socket';
import { later } from '@ember/runloop';
import { waitUntil } from '@ember/test-helpers';
import { Promise } from 'rsvp';

const timeout = 10000;

module('Unit | Actions | API | Recon', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);

    const serviceState = Immutable.from({
      serviceData: undefined,
      isServicesLoading: undefined,
      isServicesRetrieveError: undefined
    });

    patchReducer(this, {
      respond: {
        recon: serviceState
      }
    });
  });

  test('getServices will not query for core services when already present in redux', async function(assert) {
    assert.expect(6);

    const redux = this.owner.lookup('service:redux');
    const getServicesApi = bindActionCreators(recon.getServices, redux.dispatch.bind(redux));

    let state = redux.getState();
    assert.equal(isServicesLoading(state), undefined);
    assert.equal(isServicesRetrieveError(state), undefined);
    assert.deepEqual(getServices(state), undefined);

    await getServicesApi();

    state = redux.getState();
    assert.equal(isServicesLoading(state), false);
    assert.equal(isServicesRetrieveError(state), false);
    assert.ok(getServices(state) !== undefined);

    let exhausted;
    const done = assert.async();
    await new Promise(async(resolve) => {
      patchSocket((method, modelName, query) => {
        assert.equal(method, 'findAll');
        assert.equal(modelName, 'core-service');
        assert.deepEqual(query, {});
      });

      resolve();
    });

    await new Promise(async(resolve) => {
      getServicesApi();

      later(() => {
        exhausted = true;
      }, 2000);

      resolve();
    });

    return waitUntil(() => exhausted === true, { timeout }).then(() => {
      revertPatch();
      done();
    });
  });
});
