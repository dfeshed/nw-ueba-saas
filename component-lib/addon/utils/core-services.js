// This pattern filters out numbers after the first decimal place
// A serviceId like 11.1.0.0 will be changed to 11.1
const serviceIdRegex = new RegExp(/\d*\.\d/);

export const coreServiceNotUpdated = (coreDeviceVersion, minServiceVersion) => {
  if (coreDeviceVersion && minServiceVersion) {
    const coreVersion = Number(coreDeviceVersion.match(serviceIdRegex)[0]);
    return coreVersion < Number(minServiceVersion);
  }
  return false;
};
