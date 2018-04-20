import { createSelector } from 'reselect';

const listOfDevices = (state) => state.packager.devices || [];

const defaultPackagerConfig = (state) => state.packager.defaultPackagerConfig;

export const defaultDriverServiceName = createSelector(
  [defaultPackagerConfig],
  (defaultPackagerConfig) => defaultPackagerConfig.packageConfig ? defaultPackagerConfig.packageConfig.driverServiceName : null
);

export const defaultDriverDisplayName = createSelector(
  [defaultPackagerConfig],
  (defaultPackagerConfig) => defaultPackagerConfig.packageConfig ? defaultPackagerConfig.packageConfig.driverDisplayName : null
);

export const defaultDriverDescription = createSelector(
  [defaultPackagerConfig],
  (defaultPackagerConfig) => defaultPackagerConfig.packageConfig ? defaultPackagerConfig.packageConfig.driverDescription : null
);

export const listOfServices = createSelector(
[listOfDevices],
(listOfDevices) => {
  const services = [];
  for (let i = 0; i < listOfDevices.length; i++) {
    const service = {};
    service.id = listOfDevices[i].host;
    service.value = [listOfDevices[i].displayName, listOfDevices[i].host, listOfDevices[i].name];
    services.push(service);
  }
  return services;
}
);