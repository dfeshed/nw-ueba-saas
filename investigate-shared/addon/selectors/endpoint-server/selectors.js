import reselect from 'reselect';

const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
const _serviceData = (state) => state.endpointServer.serviceData;
const _isSummaryRetrieveError = (state) => state.endpointServer.isSummaryRetrieveError;
const _getSelectedServiceId = (state) => state.endpointQuery.serverId;

const _selectedService = createSelector(
  [_serviceData, _getSelectedServiceId],
  (services, selectedServiceId) => {
    let ret = {};
    if (services && Array.isArray(services)) {
      const selectedService = services.find((e) => e.id === selectedServiceId);
      ret = selectedService ? selectedService : services[0];
    }
    return ret;
  }
);

// SELECTOR FUNCTIONS

export const selectedServiceWithStatus = createSelector(
  [_selectedService, _isSummaryRetrieveError],
  (selectedService, isSummaryRetrieveError) => {
    let { name = '' } = selectedService;
    name = name.toLowerCase().includes('broker') ? 'Broker ' : '';
    return {
      name,
      isServiceOnline: !isSummaryRetrieveError
    };
  }
);