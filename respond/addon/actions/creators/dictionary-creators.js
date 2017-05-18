import Ember from 'ember';
import { dictionaries, users } from '../api';
import * as ACTION_TYPES from '../types';
import * as ErrorHandlers from '../util/error-handlers';

const {
  Logger
} = Ember;

/**
 * Action creator for fetching all known Users
 * @method getAllUsers
 * @public
 * @returns {Object}
 */
const getAllUsers = () => {
  return {
    type: ACTION_TYPES.FETCH_ALL_USERS,
    promise: users.getAllUsers(),
    meta: {
      onSuccess: (response) => Logger.debug(ACTION_TYPES.FETCH_ALL_USERS, response),
      onFailure: (response) => ErrorHandlers.handleContentRetrievalError(response, 'users')
    }
  };
};

/**
 * Action creator for fetching all known priority types
 * @method getAllPriorityTypes
 * @public
 * @returns {Object}
 */
const getAllPriorityTypes = () => {
  return {
    type: ACTION_TYPES.FETCH_PRIORITY_TYPES,
    promise: dictionaries.getAllPriorityTypes(),
    meta: {
      onSuccess: (response) => Logger.debug(ACTION_TYPES.FETCH_PRIORITY_TYPES, response),
      onFailure: (response) => ErrorHandlers.handleContentRetrievalError(response, 'priority types')
    }
  };
};

/**
 * Action creator for fetching all known status types
 * @method getAllStatusTypes
 * @public
 * @returns {Object}
 */
const getAllStatusTypes = () => {
  return {
    type: ACTION_TYPES.FETCH_STATUS_TYPES,
    promise: dictionaries.getAllStatusTypes(),
    meta: {
      onSuccess: (response) => Logger.debug(ACTION_TYPES.FETCH_STATUS_TYPES, response),
      onFailure: (response) => ErrorHandlers.handleContentRetrievalError(response, 'status types')
    }
  };
};

/**
 * Action creator for fetching the list of categories available for incidents
 * @public
 * @returns {Promise}
 */
const getAllCategories = () => {
  return {
    type: ACTION_TYPES.FETCH_CATEGORY_TAGS,
    promise: dictionaries.getAllCategories(),
    meta: {
      onSuccess: (response) => Logger.debug(ACTION_TYPES.FETCH_CATEGORY_TAGS, response),
      onFailure: (response) => ErrorHandlers.handleContentRetrievalError(response, 'category tags')
    }
  };
};

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
  getAllUsers,
  getAllPriorityTypes,
  getAllStatusTypes,
  getAllCategories,
  getAllRemediationStatusTypes,
  getAllRemediationTypes
};