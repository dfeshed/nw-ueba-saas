import { isEmpty } from '@ember/utils';

const VALID_PASSWORD_PATTERN = /^[!-~]{3,}$/;
const VALID_IP_PATTERN = /^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/;
const VALID_HOST_NAME_PATTERN = /^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9-]*[a-zA-Z0-9])\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9-]*[A-Za-z0-9])$/;
const VALID_PORT_PATTERN = /^(0|[1-9]\d*)$/;

export const validateConfig = (formData) => {
  const { password, address: server, httpsPort, esh: hostName, httpsBeaconIntervalInSeconds: beconInterval } = formData;

  if ((password !== undefined) && isEmpty(password)) {
    return {
      isPasswordError: true,
      passwordInvalidMessage: 'endpointRAR.errorMessages.passwordEmptyMessage'
    };
  }

  if (password && !VALID_PASSWORD_PATTERN.test(password)) {
    return {
      isPasswordError: true,
      passwordInvalidMessage: 'endpointRAR.errorMessages.invalidPasswordString'
    };
  }

  if ((server !== undefined) && isEmpty(server)) {
    return {
      isServerError: true,
      invalidServerMessage: 'endpointRAR.errorMessages.serverEmptyMessage'
    };
  }

  if (server && !(VALID_IP_PATTERN.test(server) || VALID_HOST_NAME_PATTERN.test(server))) {
    return {
      isServerError: true,
      invalidServerMessage: 'endpointRAR.errorMessages.invalidServer'
    };
  }

  if (hostName && !VALID_HOST_NAME_PATTERN.test(hostName)) {
    return {
      isHostError: true,
      invalidHostNameMessage: 'endpointRAR.errorMessages.invalidHostName'
    };
  }

  if ((httpsPort !== undefined && isEmpty(httpsPort)) || (httpsPort && !VALID_PORT_PATTERN.test(httpsPort))) {
    return {
      isPortError: true,
      invalidPortMessage: 'endpointRAR.errorMessages.invalidPort'
    };
  }

  if ((beconInterval !== undefined && isEmpty(beconInterval)) ||
      (beconInterval && (!VALID_PORT_PATTERN.test(beconInterval) ||
      _beconIntervalValidation(beconInterval)))) {
    return {
      isBeaconError: true,
      invalidBeaconIntervalMessage: 'endpointRAR.errorMessages.invalidBeaconInterval'
    };
  }

  return null;
};

const _beconIntervalValidation = (beconInterval) => {
  // converting mins into seconds
  const seconds = beconInterval * 60;
  if (seconds < 60 || seconds > 86400) {
    return true;
  }
};