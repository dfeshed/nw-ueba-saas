import { test, module } from 'qunit';
import { setupTest } from 'ember-qunit';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../helpers/make-pack-action';
import reducer from 'respond/reducers/respond/recon/index';
import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'respond/actions/types';

module('Unit | Reducers | respond | recon', function(hooks) {
  setupTest(hooks);

  test('SERVICES_RETRIEVE will not explode when null item exists', function(assert) {
    let action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.SERVICES_RETRIEVE,
      payload: {
        data: [
          {
            id: '555d9a6fe4b0d37c827d402d',
            name: 'CONCENTRATOR'
          }
        ]
      }
    });

    let result = reducer(Immutable.from({}), action);

    assert.deepEqual(result, {
      isServicesLoading: false,
      isServicesRetrieveError: false,
      serviceData: {
        '555d9a6fe4b0d37c827d402d': {
          id: '555d9a6fe4b0d37c827d402d',
          name: 'CONCENTRATOR'
        }
      }
    });

    action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.SERVICES_RETRIEVE,
      payload: {
        data: [
          null,
          {
            id: '555d9a6fe4b0d37c827d402d',
            name: 'CONCENTRATOR'
          }
        ]
      }
    });

    result = reducer(Immutable.from({}), action);

    assert.deepEqual(result, {
      isServicesLoading: false,
      isServicesRetrieveError: false,
      serviceData: {
        '555d9a6fe4b0d37c827d402d': {
          id: '555d9a6fe4b0d37c827d402d',
          name: 'CONCENTRATOR'
        }
      }
    });

    action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.SERVICES_RETRIEVE,
      payload: {
      }
    });

    result = reducer(Immutable.from({}), action);

    assert.deepEqual(result, {
      isServicesLoading: false,
      isServicesRetrieveError: false,
      serviceData: {}
    });
  });
});
