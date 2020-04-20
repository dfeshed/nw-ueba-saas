import { dictionaries, users } from '../api';
import * as ACTION_TYPES from '../types';


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
 * @method getAllAlertNames
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
  getAllStatusTypes,
  getAllRemediationStatusTypes,
  getAllAlertTypes,
  getAllAlertSources,
  getAllAlertNames,
  getAllMilestoneTypes
};