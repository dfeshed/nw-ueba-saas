import { test, module } from 'qunit';
import { setupTest } from 'ember-qunit';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../helpers/make-pack-action';
import reducer from 'respond/reducers/respond/recon/index';
import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'respond/actions/types';
import data from '../../../helpers/language-and-aliases-data-helper';

module('Unit | Reducers | respond | recon', function(hooks) {
  setupTest(hooks);

  const prevState = {
    serviceData: undefined,
    isServicesLoading: undefined,
    isServicesRetrieveError: undefined,
    aliases: {},
    language: {},
    aliasesCache: {},
    languageCache: {},
    loadingRecon: false
  };

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

  test('ALIASES_AND_LANGUAGE_RETRIEVE sets loadingRecon', function(assert) {
    const action1 = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.ALIASES_AND_LANGUAGE_RETRIEVE,
      payload: {
        loading: true
      }
    });
    const result = reducer(Immutable.from(prevState), action1);
    assert.ok(result.loadingRecon, 'loadingRecon set correctly');
    const action2 = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.ALIASES_AND_LANGUAGE_RETRIEVE,
      payload: {
        loading: false
      }
    });
    const result2 = reducer(Immutable.from(prevState), action2);
    assert.notOk(result2.loadingRecon, 'loadingRecon set correctly');
  });

  test('GET_FROM_LANGUAGE_AND_ALIASES_CACHE sets aliases and language from cache properties', function(assert) {
    const state1 = { ...prevState };
    state1.aliasesCache = { '1111': { 1: 'some', 2: 'values', 3: 'for', 4: 'aliases' } };
    state1.languageCache = {
      '1111': [
        {
          format: 'UInt32',
          metaName: 'rpayload',
          flags: 2147483665,
          displayName: 'Session Retransmit Payload'
        },
        {
          format: 'MAC',
          metaName: 'alias.mac',
          flags: 2147483649,
          displayName: 'MAC Alias Record'
        },
        {
          format: 'UInt16',
          metaName: 'session.split',
          flags: 2147483649,
          displayName: 'Session Split Count'
        }
      ]
    };

    const action1 = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.GET_FROM_LANGUAGE_AND_ALIASES_CACHE,
      payload: { endpointId: '1111' }
    });
    const result = reducer(Immutable.from(state1), action1);
    assert.ok(result.aliases && Object.keys(result.aliases).length === 4, 'aliases set from cache');
    assert.ok(result.language && Object.keys(result.language).length === 3, 'language set from cache');
  });

  test('ALIASES_AND_LANGUAGE_COMPLETE sets language and aliases and cache properties correctly', function(assert) {
    const request1 = {
      id: `id-${Date.now().toString()}`,
      filter: [
        {
          field: 'endpointId',
          isNull: false,
          operator: '==',
          value: undefined
        }
      ],
      filterOperator: 'and'
    };

    // randomString to be used as serviceId
    const randomString = Date.now().toString();
    const request = { ...request1 };
    request.filter[0].value = randomString;

    const successAction = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.ALIASES_AND_LANGUAGE_COMPLETE,
      payload: {
        response: {
          code: 0,
          meta: {
            complete: true
          },
          data,
          request
        },
        endpointId: randomString
      }
    });
    const result = reducer(Immutable.from(prevState), successAction);
    assert.ok(result.aliasesCache.hasOwnProperty(randomString), 'aliasesCache has correct serviceId');
    assert.ok(result.languageCache.hasOwnProperty(randomString), 'languageCache has correct serviceId');
    assert.equal(Object.keys(result.aliases).length, 8, 'aliases object has correct number of metaName');
    assert.equal(result.language.length, data.length, 'language array is of correct length');
  });
});
