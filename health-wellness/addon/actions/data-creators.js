import api from './api';
import * as ACTION_TYPES from './types';

/**
 * Action creator for fetching monitors.
 * @method getMonitorList
 * @public
 */
export const getMonitorList = () => ({
  type: ACTION_TYPES.GET_MONITORS,
  promise: api.getMonitors()
});
