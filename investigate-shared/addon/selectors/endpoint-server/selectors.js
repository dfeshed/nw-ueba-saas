import reselect from 'reselect';

const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
const _serviceData = (state) => state.endpointServer.serviceData;
const _isServicesRetrieveError = (state) => state.endpointServer.isServicesRetrieveError;
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
  [_selectedService, _isServicesRetrieveError],
  (selectedService, isServicesRetrieveError) => {
    let { name = '' } = selectedService;
    name = name.toLowerCase().includes('broker') ? 'Broker ' : '';
    return {
      name,
      isServiceOnline: !isServicesRetrieveError
    };
  }
);