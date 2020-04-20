import { test, module } from 'qunit';
import Immutable from 'seamless-immutable';
import _ from 'lodash';

import * as ACTION_TYPES from 'recon/actions/types';
import reducer from 'recon/reducers/dictionaries/reducer';
import { getAliases, getLanguage } from 'recon/reducers/dictionaries/utils';
import makePackAction from '../../../helpers/make-pack-action';
import { LIFECYCLE } from 'redux-pack';
import data from '../../../helpers/language-and-aliases-data';

module('Unit | Reducers | dictionaries | Recon');

const prevState = Immutable.from({
  aliases: undefined,
  language: undefined,
  languageAndAliasesError: false
});

const request1 = {
  id: `id-${Date.now().toString()}`,
  filter: [
    {
      field: 'endpointId',
      value: undefined,
      operator: '==',
      isNull: false
    }
  ]
};

test('INITIALIZE reducer sets aliases and language from payload', function(assert) {
  const aliases = getAliases(data);
  const language = getLanguage(data);

  const action = {
    type: ACTION_TYPES.INITIALIZE,
    payload: {
      language,
      aliases
    }
  };
  const result = reducer(prevState, action);
  assert.deepEqual(result.language, language, 'language shall be set');
  assert.deepEqual(result.aliases, aliases, 'aliases shall be set');
});

test('LANGUAGE_AND_ALIASES_RETRIEVE reducer, upon success, sets language and aliases and related properties correctly',
  function(assert) {
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
    assert.equal(successResult.language.length, data.length, 'language array is of correct length');
  });
