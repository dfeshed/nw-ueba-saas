import { lookup } from 'ember-dependency-lookup';
import * as ACTION_TYPES from 'configure/actions/types/endpoint';
import { debug } from '@ember/debug';
import { fetchEndpointServers } from 'configure/actions/api/endpoint/server';
import { getCertificates } from 'configure/actions/creators/endpoint/certificates-creator';

const _initializeEndpoint = () => {
  return (dispatch, getState) => {
    const server = getState().configure.endpoint.server.serviceData ? getState().configure.endpoint.server.serviceData[0] : {};
    dispatch(setEndpointServer(server));
  };
};

export const isEndpointServerOffline = (status) => ({
  type: ACTION_TYPES.ENDPOINT_SERVER_STATUS,
  payload: status
});

export const setEndpointServer = (server) => {
  return (dispatch, getState) => {
    const { serverId } = getState().configure.endpoint.query;
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
        dispatch(getCertificates());
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
          request.clearPersistentStreamOptions(['socketUrlPostfix', 'requiredSocketUrl']);
          dispatch(_initializeEndpoint());
          debug(`onSuccess: ${response}`);
        },
        onfailure: () => {
          request.clearPersistentStreamOptions(['socketUrlPostfix', 'requiredSocketUrl']);
        }
      }
    });
  };
};