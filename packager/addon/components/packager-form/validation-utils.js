import { isEmpty } from 'ember-utils';

const VALID_PORT_PATTERN = /^(0|[1-9]\d*)$/;
const VALID_IP_PATTERN = /^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/;
const VALID_NAME_PATTERN = /^[a-zA-Z0-9]+$/;
const INVALID_CONFIG_NAME_PATTERN = /[ !@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/g;
const VALID_EVENT_PATTERN = /^[0-9-]+$/;

export const validatePackageConfig = (formData) => {
  const { port, server, serviceName, displayName, certificatePassword } = formData;
  if (!VALID_IP_PATTERN.test(server)) {
    return {
      isServerError: true,
      invalidServerMessage: 'packager.errorMessages.invalidIP'
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
      passwordEmptyMessage: 'packager.errorMessages.passwordEmptyMessage'
    };
  }
  if (!VALID_NAME_PATTERN.test(serviceName)) {
    return {
      isServiceNameError: true,
      invalidServiceNameMessage: 'packager.errorMessages.invalidName'
    };
  }
  if (!VALID_NAME_PATTERN.test(displayName)) {
    return {
      isDisplayNameError: true,
      invalidServiceNameMessage: 'packager.errorMessages.invalidName'
    };
  }
  return null;
};

export const validateLogConfigFields = (formData) => {
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

  channels.every((obj) => {
    const { eventId, filter, channel } = obj;
    if (isEmpty(channel) || isEmpty(filter) || isEmpty(eventId)) {
      error = {
        invalidTableItem: '',
        isError: true
      };
      return false;
    }
    if (eventId.trim().toUpperCase() === 'ALL') {
      return true;
    }
    const arrayOfEvents = eventId.split(',');
    const hasInvalidEventId = arrayOfEvents.some((event) => {
      return !VALID_EVENT_PATTERN.test(event.trim());
    });
    if (hasInvalidEventId) {
      error = {
        invalidTableItem: eventId,
        isError: true
      };
      return false;
    }
  });
  return error;
};
