import { test, module } from 'qunit';
import Immutable from 'seamless-immutable';
import _ from 'lodash';

import * as ACTION_TYPES from 'investigate-events/actions/types';
import reducer from 'investigate-events/reducers/investigate/dictionaries/reducer';
import makePackAction from '../../../helpers/make-pack-action';
import { LIFECYCLE } from 'redux-pack';
import data from '../../../helpers/language-and-aliases-data';

module('Unit | Reducers | dictionaries | Investigate');

const prevState = Immutable.from({
  aliases: undefined,
  aliasesCache: {},
  language: undefined,
  languageCache: {},
  metaKeyCache: undefined,
  languageAndAliasesError: false
});

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

test('LANGUAGE_AND_ALIASES_RETRIEVE reducer, upon start, sets language and aliases and related properties correctly', function(assert) {
  // randomString to be used as serviceId
  const randomString = Date.now().toString();
  const request = _.cloneDeep(request1);
  request.filter[0].value = randomString;

  const startAction = makePackAction(LIFECYCLE.START, {
    type: ACTION_TYPES.LANGUAGE_AND_ALIASES_RETRIEVE,
    payload: {
      data,
      request
    }
  });
  const startResult = reducer(prevState, startAction);
  assert.notOk(startResult.languageAndAliasesError, 'languageAndAliasesError shall be false');
  assert.equal(startResult.aliases, undefined, 'aliases shall be undefined');
  assert.equal(startResult.language, undefined, 'language shall be undefined');
});

test('LANGUAGE_AND_ALIASES_RETRIEVE reducer, upon success, sets language and aliases and related properties correctly', function(assert) {
  // randomString to be used as serviceId
  const randomString = Date.now().toString();
  const request = _.cloneDeep(request1);
  request.filter[0].value = randomString;

  const successAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.LANGUAGE_AND_ALIASES_RETRIEVE,
    payload: {
      data,
      request
    }
  });
  const successResult = reducer(prevState, successAction);
  assert.equal(successResult.languageAndAliasesError, false, 'languageAndAliasesError shall be false');
  assert.ok(successResult.aliasesCache.hasOwnProperty(randomString), 'aliasesCache has correct serviceId');
  assert.ok(successResult.languageCache.hasOwnProperty(randomString), 'languageCache has correct serviceId');
  assert.equal(successResult.language.length, data.length, 'language array is of correct length');
});

test('LANGUAGE_AND_ALIASES_RETRIEVE reducer, upon failure, sets language and aliases and related properties correctly', function(assert) {
  // randomString to be used as serviceId
  const randomString = Date.now().toString();
  const request = _.cloneDeep(request1);
  request.filter[0].value = randomString;

  const failureAction = makePackAction(LIFECYCLE.FAILURE, {
    type: ACTION_TYPES.LANGUAGE_AND_ALIASES_RETRIEVE,
    payload: {
      data,
      request
    }
  });
  const failureResult = reducer(prevState, failureAction);
  assert.ok(failureResult.languageAndAliasesError, 'languageAndAliasesError shall be true');
  assert.equal(failureResult.aliases, undefined, 'aliases shall be undefined');
  assert.propEqual(failureResult.aliasesCache, {}, 'aliasesCache shall be empty object');
  assert.propEqual(failureResult.languageCache, {}, 'languageCache shall be empty object');
  assert.equal(failureResult.language, undefined, 'language shall be undefined');
  assert.equal(failureResult.metaKeyCache, undefined, 'metaKeyCache shall be undefined');
});
