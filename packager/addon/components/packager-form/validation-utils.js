import { isEmpty } from '@ember/utils';

const VALID_PORT_PATTERN = /^(0|[1-9]\d*)$/;
const VALID_IP_PATTERN = /^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/;
const VALID_HOST_NAME_PATTERN = /^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\-]*[a-zA-Z0-9])\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\-]*[A-Za-z0-9])$/;
const VALID_NAME_PATTERN = /^[a-zA-Z0-9]+$/;
const VALID_DISPLAY_NAME_PATTERN = /^[a-zA-Z0-9 ]+$/;
const INVALID_CONFIG_NAME_PATTERN = /[!@#$%^&*()+\=\[\]{};':"\\|,.<>\/?]/g;
const VALID_EVENT_PATTERN = /^[0-9-]+$/;
const VALID_PASSWORD_PATTERN = /^[!-~]{3,}$/;

export const validatePackageConfig = (formData) => {
  const { port, server, serviceName, displayName, certificatePassword, driverServiceName, driverDisplayName } = formData;
  if (!VALID_IP_PATTERN.test(server) && !VALID_HOST_NAME_PATTERN.test(server)) {
    return {
      isServerError: true,
      invalidServerMessage: 'packager.errorMessages.invalidServer'
    };
  }

  if (!VALID_PORT_PATTERN.test(port)) {
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
      invalidServiceNameMessage: 'packager.errorMessages.invalidName'
    };
  }
  if (!VALID_DISPLAY_NAME_PATTERN.test(displayName)) {
    return {
      isDisplayNameError: true,
      invalidDisplayNameMessage: 'packager.errorMessages.invalidName'
    };
  }
  if (!VALID_DISPLAY_NAME_PATTERN.test(driverServiceName)) {
    return {
      isDriverServiceNameError: true,
      invalidServiceNameMessage: 'packager.errorMessages.invalidName'
    };
  }
  if (!VALID_DISPLAY_NAME_PATTERN.test(driverDisplayName)) {
    return {
      isDriverDisplayNameError: true,
      invalidDisplayNameMessage: 'packager.errorMessages.invalidName'
    };
  }
  return null;
};

function _checkForServerErrors(index, eventId, errorObj) {
  let error = null;
  if (errorObj) {
    if (errorObj.identifier === index + 1 && errorObj.reason.toUpperCase() === 'EVENT_ID_INVALID') {
      error = {
        invalidTableItem: eventId,
        isError: true
      };
    }
  }
  if (errorObj && errorObj.identifier == index + 1) {
    if (errorObj.reason.toUpperCase() === 'FILTER_INVALID' || errorObj.reason.toUpperCase() === 'CHANNEL_EMPTY') {
      error = {
        invalidTableItem: '',
        isError: true
      };
    }
  }
  return error;
}

function _validateChannels(channels, errorObj) {
  let error = null;
  channels.every((obj, index) => {
    const { eventId, filter, channel } = obj;
    const isEventIdString = typeof eventId === 'string';
    let hasInvalidEventId = false;
    if (isEmpty(channel) || isEmpty(filter) || isEmpty(eventId)) {
      error = {
        invalidTableItem: '',
        isError: true
      };
      return false;
    }
    if (eventId && isEventIdString && (eventId.trim().toUpperCase() === 'ALL' && filter.toUpperCase() !== 'EXCLUDE')) {
      return true;
    }
    if (isEventIdString) {
      const arrayOfEvents = eventId.split(',');
      hasInvalidEventId = arrayOfEvents.some((event) => {
        return !VALID_EVENT_PATTERN.test(event.trim());
      });
    }
    if (hasInvalidEventId) {
      error = {
        invalidTableItem: eventId,
        isError: true
      };
      return false;
    }
    error = _checkForServerErrors(index, eventId, errorObj);
    if (error) {
      return false;
    }
    return true;
  });
  return error;
}

export const validateLogConfigFields = (formData, errorObj) => {
  const { configName, primaryDestination, channels } = formData;
  let error = null;

  if (isEmpty(configName)) {
    return {
      isConfigError: true,
      errorMessage: 'packager.emptyName'
    };
  }
  if (INVALID_CONFIG_NAME_PATTERN.test(configName)) {
    return {
      isConfigError: true,
      errorMessage: 'packager.specialCharacter'
    };
  }
  if (isEmpty(primaryDestination)) {
    return {
      errorClass: 'is-error',
      className: 'rsa-form-label is-error power-select'
    };
  }

  error = _validateChannels(channels, errorObj);
  return error;
};
