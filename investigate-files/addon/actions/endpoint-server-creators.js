import { lookup } from 'ember-dependency-lookup';
import * as ACTION_TYPES from './types';
import { debug } from '@ember/debug';
import { fetchEndpointServers } from 'investigate-files/actions/fetch/server';
import { fetchSchemaInfo, getFirstPageOfFiles } from 'investigate-files/actions/data-creators';
import { getCertificates } from 'investigate-files/actions/certificate-data-creators';

export const initializeEndpoint = () => {
  return (dispatch, getState) => {
    const server = getState().endpointServer.serviceData ? getState().endpointServer.serviceData[0] : {};
    dispatch(setEndpointServer(server));
  };
};

export const isEndpointServerOffline = (status) => ({
  type: ACTION_TYPES.ENDPOINT_SERVER_STATUS,
  payload: status
});

export const setEndpointServer = (server) => {
  return (dispatch, getState) => {
    const { serverId } = getState().endpointQuery;
    const request = lookup('service:request');
    if (serverId !== server.id) {
      dispatch({
        type: ACTION_TYPES.ENDPOINT_SERVER_SELECTED,
        payload: server.id
      });
      request.registerPersistentStreamOptions({ socketUrlPostfix: server.id, requiredSocketUrl: 'endpoint/socket' });
      return request.ping('endpoint-server-ping')
      .then(function() {
        dispatch(isEndpointServerOffline(false));
        // reset files state every time a different endpoint server is selected.
        dispatch({ type: ACTION_TYPES.RESET_FILES });
        dispatch({ type: ACTION_TYPES.RESET_CERTIFICATES });
        dispatch(getCertificates());
        dispatch(getFirstPageOfFiles());
      })
      .catch(function() {
        dispatch(isEndpointServerOffline(true));
        dispatch({ type: ACTION_TYPES.RESET_FILES });
      });
    }
  };
};

export const getEndpointServers = () => {
  return (dispatch) => {
    const request = lookup('service:request');
    request.registerPersistentStreamOptions({ socketUrlPostfix: 'any', requiredSocketUrl: 'endpoint/socket' });
    dispatch({
      type: ACTION_TYPES.LIST_OF_ENDPOINT_SERVERS,
      promise: fetchEndpointServers(),
      meta: {
        onSuccess: (response) => {
          dispatch(fetchSchemaInfo());
          debug(`onSuccess: ${response}`);
        },
        onfailure: () => {
          request.clearPersistentStreamOptions(['socketUrlPostfix', 'requiredSocketUrl']);
        }
      }
    });
  };
};