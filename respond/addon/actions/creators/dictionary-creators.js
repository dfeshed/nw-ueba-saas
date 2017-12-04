import { dictionaries, users } from '../api';
import * as ACTION_TYPES from '../types';

/**
 * Action creator for fetching all known "enabled" Users
 * @method getAllEnabledUsers
 * @public
 * @returns {Object}
 */
const getAllEnabledUsers = () => {
  return {
    type: ACTION_TYPES.FETCH_ALL_ENABLED_USERS,
    promise: users.getAllEnabledUsers()
  };
};

/**
 * Action creator for fetching all known Users (including disabled users)
 * @method getAllEnabledUsers
 * @public
 * @returns {Object}
 */
const getAllUsers = () => {
  return {
    type: ACTION_TYPES.FETCH_ALL_USERS,
    promise: users.getAllUsers()
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
    promise: dictionaries.getAllPriorityTypes()
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
    promise: dictionaries.getAllStatusTypes()
  };
};

/**
 * Action creator for fetching the list of categories available for incidents
 * @public
 * @returns {Object}
 */
const getAllCategories = () => {
  return {
    type: ACTION_TYPES.FETCH_CATEGORY_TAGS,
    promise: dictionaries.getAllCategories()
  };
};

/**
 * Action creator for fetching all known remediation status types
 * @method getAllStatusTypes
 * @public
 * @returns {Object}
 */
const getAllRemediationStatusTypes = () => {
  return {
    type: ACTION_TYPES.FETCH_REMEDIATION_STATUS_TYPES,
    promise: dictionaries.getAllRemediationStatusTypes()
  };
};

/**
 * Action creator for fetching all known remediation types
 * @method getAllRemediationTypes
 * @public
 * @returns {Object}
 */
const getAllRemediationTypes = () => {
  return {
    type: ACTION_TYPES.FETCH_REMEDIATION_TYPES,
    promise: dictionaries.getAllRemediationTypes()
  };
};

/**
 * Action creator for fetching all known alert types
 * @method getAllAlertTypes
 * @public
 * @returns {Object}
 */
const getAllAlertTypes = () => {
  return {
    type: ACTION_TYPES.FETCH_ALERT_TYPES,
    promise: dictionaries.getAllAlertTypes()
  };
};

/**
 * Action creator for fetching all known alert sources
 * @method getAllAlertSources
 * @public
 * @returns {Object}
 */
const getAllAlertSources = () => {
  return {
    type: ACTION_TYPES.FETCH_ALERT_SOURCES,
    promise: dictionaries.getAllAlertSources()
  };
};

/**
 * Action creator for fetching all unique alert rule names
 * @public
 * @method getAlertRuleNames
 * @returns {Object}
 */
const getAllAlertNames = () => ({
  type: ACTION_TYPES.FETCH_ALERT_NAMES,
  promise: dictionaries.getAllAlertNames()
});

/**
 * Action creator for fetching all known milestones
 * @method getAllMilestoneTypes
 * @public
 * @returns {Object}
 */
const getAllMilestoneTypes = () => {
  return {
    type: ACTION_TYPES.FETCH_MILESTONE_TYPES,
    promise: dictionaries.getAllMilestoneTypes()
  };
};

export {
  getAllUsers,
  getAllEnabledUsers,
  getAllPriorityTypes,
  getAllStatusTypes,
  getAllCategories,
  getAllRemediationStatusTypes,
  getAllRemediationTypes,
  getAllAlertTypes,
  getAllAlertSources,
  getAllAlertNames,
  getAllMilestoneTypes
};