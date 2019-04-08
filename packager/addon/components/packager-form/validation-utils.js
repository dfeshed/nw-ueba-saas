import { isEmpty } from '@ember/utils';

const VALID_PORT_PATTERN = /^(0|[1-9]\d*)$/;
const VALID_IP_PATTERN = /^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/;
const VALID_HOST_NAME_PATTERN = /^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9-]*[a-zA-Z0-9])\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9-]*[A-Za-z0-9])$/;
const VALID_NAME_PATTERN = /^[a-zA-Z0-9]+$/;
const VALID_DISPLAY_NAME_PATTERN = /^[a-zA-Z0-9 ]+$/;
const VALID_PASSWORD_PATTERN = /^[!-~]{3,}$/;

export const validatePackageConfig = (formData) => {
  const { port, server, serviceName, displayName, certificatePassword, driverServiceName, driverDisplayName } = formData;
  if (server && (!(VALID_IP_PATTERN.test(server) || VALID_HOST_NAME_PATTERN.test(server)))) {
    return {
      isServerError: true,
      invalidServerMessage: 'packager.errorMessages.invalidServer'
    };
  }

  if (!VALID_PORT_PATTERN.test(port) || ((port < 1) || (port > 65535))) {
    return {
      isPortError: true,
      invalidPortMessage: 'packager.errorMessages.invalidPort'
    };
  }

  if (isEmpty(certificatePassword)) {
    return {
      isPasswordError: true,
      passwordInvalidMessage: 'packager.errorMessages.passwordEmptyMessage'
    };
  }

  if (!VALID_PASSWORD_PATTERN.test(certificatePassword)) {
    return {
      isPasswordError: true,
      passwordInvalidMessage: 'packager.errorMessages.invalidPasswordString'
    };
  }

  if (!VALID_NAME_PATTERN.test(serviceName)) {
    return {
      isServiceNameError: true,
      invalidServiceNameMessage: 'packager.errorMessages.invalidName',
      isAccordion: true
    };
  }
  if (!VALID_DISPLAY_NAME_PATTERN.test(displayName)) {
    return {
      isDisplayNameError: true,
      invalidDisplayNameMessage: 'packager.errorMessages.invalidName',
      isAccordion: true
    };
  }
  if (!VALID_DISPLAY_NAME_PATTERN.test(driverServiceName)) {
    return {
      isDriverServiceNameError: true,
      invalidServiceNameMessage: 'packager.errorMessages.invalidName',
      isAccordion: true
    };
  }
  if (!VALID_DISPLAY_NAME_PATTERN.test(driverDisplayName)) {
    return {
      isDriverDisplayNameError: true,
      invalidDisplayNameMessage: 'packager.errorMessages.invalidName',
      isAccordion: true
    };
  }
  return null;
};
