import api from 'configure/actions/api/respond';
import * as ACTION_TYPES from 'configure/actions/types/respond';

/**
 * Action creator for fetching all known "enabled" Users
 * @method getAllEnabledUsers
 * @public
 * @returns {Object}
 */
const getAllEnabledUsers = () => {
  return {
    type: ACTION_TYPES.FETCH_ALL_ENABLED_USERS,
    promise: api.users.getAllEnabledUsers()
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
    promise: api.users.getAllUsers()
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
    promise: api.dictionaries.getAllCategories()
  };
};

export {
  getAllUsers,
  getAllEnabledUsers,
  getAllCategories
};