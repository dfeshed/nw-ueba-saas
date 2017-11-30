import { createSelector } from 'reselect';

const listOfDevices = (state) => state.packager.devices || [];

export const listOfServices = createSelector(
  [listOfDevices],
  (listOfDevices) => {
    const services = [];
    for (let i = 0; i < listOfDevices.length; i++) {
      const service = {};
      service.id = listOfDevices[i].host;
      service.value = `${listOfDevices[i].displayName} ${listOfDevices[i].host} ${listOfDevices[i].name}`;
      services.push(service);
    }
    return services;
  }
);