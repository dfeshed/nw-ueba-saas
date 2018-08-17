import * as ACTION_TYPES from './types';
import { debug } from '@ember/debug';
import { fetchEndpointServers } from 'investigate-files/actions/fetch/server';

export const setEndpointServer = (server) => {
  return (dispatch, getState) => {
    const { serverId } = getState().investigate.queryNode;
    if (serverId !== server.id) {
      dispatch({
        type: ACTION_TYPES.ENDPOINT_SERVER_SELECTED,
        payload: server.id
      });
    }
  };
};

export const getEndpointServers = () => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.LIST_OF_ENDPOINT_SERVERS,
      promise: fetchEndpointServers(),
      meta: {
        onSuccess: (response) => {
          debug(`onSuccess: ${response}`);
        }
      }
    });
  };
};