import { createSelector } from 'reselect';

const _servers = (state) => state.servers || [];
const _serverId = (state) => state.serverId;

export const isBrokerView = createSelector(
  [ _servers, _serverId ],
  (servers, serverId) => servers.some((s) => s.id === serverId && s.name === 'endpoint-broker-server')
);