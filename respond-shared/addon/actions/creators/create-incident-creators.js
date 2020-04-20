import Incidents from '../api/create-incident';
import * as ACTION_TYPES from '../types';
import {
  getEnabledUsers,
  getPriorityTypes,
  getCategoryTags
} from 'respond-shared/selectors/create-incident/selectors';
import { success, failure } from 'respond-shared/utils/flash-messages';
import { lookup } from 'ember-dependency-lookup';

const initializeAlerts = () => {
  return (dispatch, getState) => {
    const state = getState();

    if (!getEnabledUsers(state).length) {
      dispatch(getAllEnabledUsers());
    }
    if (!getPriorityTypes(state).length) {
      dispatch(getAllPriorityTypes());
    }
    if (!getCategoryTags(state).length) {
      dispatch(getAllCategories());
    }
  };
};

/**
 * Action creator for fetching all known "enabled" Users
 * @method getAllEnabledUsers
 * @public
 * @returns {Object}
 */
const getAllEnabledUsers = () => {
  return {
    type: ACTION_TYPES.FETCH_ALL_ENABLED_USERS,
    promise: Incidents.getAllEnabledUsers()
  };
};

/**
 * Action creator for fetching all known priority types
 * @method getAllPriorityTypes
 * @public
 * @returns {Object}
 */
const getAllPriorityTypes = () => ({
  type: ACTION_TYPES.FETCH_PRIORITY_TYPES,
  promise: Incidents.getAllPriorityTypes()
});

/**
 * Action creator for fetching the list of categories available for incidents
 * @public
 * @returns {Object}
 */
const getAllCategories = () => ({
  type: ACTION_TYPES.FETCH_CATEGORY_TAGS,
  promise: Incidents.getAllCategories()
});


const createIncidentFromAlerts = (incidentDetails, alertIds) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.CREATE_INCIDENT_FROM_ALERTS,
      promise: Incidents.createIncidentFromAlerts(incidentDetails, alertIds),
      meta: {
        onSuccess: (response) => {
          lookup('service:eventBus').trigger('rsa-application-modal-close-create-incident');
          success('respond.incidents.actions.actionMessages.incidentCreated', { incidentId: response.data.id });
          dispatch({ type: ACTION_TYPES.UPDATE_INCIDENT_ON_CREATE, payload: response });
        },
        onFailure: () => {
          failure('respond.incidents.actions.actionMessages.incidentCreationFailed');
        }
      }
    });
  };
};

/**
 * Adds a given list of indicators (alerts) to an incident with a given ID.
 * @param data all request parameters to be passed as argument to investigate server
 * @public
 */
const createIncidentFromEvents = (data) => ({
  type: ACTION_TYPES.CREATE_INCIDENT_FROM_EVENTS,
  promise: Incidents.createIncidentFromEvents(data)
});

export {
  initializeAlerts,
  getAllEnabledUsers,
  getAllPriorityTypes,
  getAllCategories,
  createIncidentFromEvents,
  createIncidentFromAlerts
};
