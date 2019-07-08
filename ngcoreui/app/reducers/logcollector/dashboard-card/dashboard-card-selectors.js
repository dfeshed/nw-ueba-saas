import reselect from 'reselect';

const _protocolsState = (state) => state.logcollector.dashboardCard;
const { createSelector } = reselect;

export const areProtocolsLoading = createSelector(
  _protocolsState,
  (_protocolsState) => _protocolsState.itemsStatus === 'wait'
);

export const protocolArray = createSelector(
  _protocolsState,
  (_protocolsState) => _protocolsState.items
);