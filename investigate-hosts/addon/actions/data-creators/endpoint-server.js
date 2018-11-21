import { lookup } from 'ember-dependency-lookup';
import * as ACTION_TYPES from '../types';
import { debug } from '@ember/debug';
import { fetchEndpointServers } from 'investigate-hosts/actions/api/server';
import { getAllSchemas, getPageOfMachines } from 'investigate-hosts/actions/data-creators/host';
import { getServiceId } from 'investigate-shared/actions/data-creators/investigate-creators';

export const initializeEndpoint = () => {
  return (dispatch, getState) => {
    const persistedServerId = localStorage.getItem('endpoint:persistedServerId') != 'null' ? localStorage.getItem('endpoint:persistedServerId') : null;
    let [server] = getState().endpointServer.serviceData || [];
    server = persistedServerId ? { id: persistedServerId } : server;
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
    if (server && serverId !== server.id) {
      dispatch({
        type: ACTION_TYPES.ENDPOINT_SERVER_SELECTED,
        payload: server.id
      });
      localStorage.setItem('endpoint:persistedServerId', server.id);
      request.registerPersistentStreamOptions({ socketUrlPostfix: server.id, requiredSocketUrl: 'endpoint/socket' });
      dispatch({ type: ACTION_TYPES.RESET_HOSTS });
      return request.ping('endpoint-server-ping')
        .then(() => {
          dispatch(isEndpointServerOffline(false));
          dispatch(getPageOfMachines());
          dispatch(getServiceId('MACHINE'));
        })
        .catch(function() {
          dispatch(isEndpointServerOffline(true));
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
          dispatch(getAllSchemas());
          debug(`onSuccess: ${response}`);
        },
        onfailure: () => {
          request.clearPersistentStreamOptions(['socketUrlPostfix', 'requiredSocketUrl']);
        }
      }
    });
  };
};

export const setSelectedEndpointServer = (id) => ({ type: ACTION_TYPES.ENDPOINT_SERVER_SELECTED, payload: id });
