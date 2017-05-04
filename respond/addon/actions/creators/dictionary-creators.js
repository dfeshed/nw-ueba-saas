import Ember from 'ember';
import { dictionaries } from '../api';
import * as ACTION_TYPES from '../types';
import * as ErrorHandlers from '../util/error-handlers';

const {
  Logger
} = Ember;

/**
 * Action creator for fetching all known remediation status types
 * @method getAllStatusTypes
 * @public
 * @returns {Promise}
 */
const getAllRemediationStatusTypes = () => {
  return {
    type: ACTION_TYPES.FETCH_REMEDIATION_STATUS_TYPES,
    promise: dictionaries.getAllRemediationStatusTypes(),
    meta: {
      onSuccess: (response) => Logger.debug(ACTION_TYPES.FETCH_REMEDIATION_STATUS_TYPES, response),
      onFailure: (response) => ErrorHandlers.handleContentRetrievalError(response, 'remediation status types')
    }
  };
};

/**
 * Action creator for fetching all known remediation types
 * @method getAllRemediationTypes
 * @public
 * @returns {Promise}
 */
const getAllRemediationTypes = () => {
  return {
    type: ACTION_TYPES.FETCH_REMEDIATION_TYPES,
    promise: dictionaries.getAllRemediationTypes(),
    meta: {
      onSuccess: (response) => Logger.debug(ACTION_TYPES.FETCH_REMEDIATION_TYPES, response),
      onFailure: (response) => ErrorHandlers.handleContentRetrievalError(response, 'remediation types')
    }
  };
};

export {
  getAllRemediationStatusTypes,
  getAllRemediationTypes
};