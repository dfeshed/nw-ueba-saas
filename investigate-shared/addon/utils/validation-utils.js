import { isEmpty } from '@ember/utils';

const VALID_PASSWORD_PATTERN = /^[!-~]{3,}$/;

export const validateConfig = (formData) => {
  const { password } = formData;

  if (isEmpty(password)) {
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

  return null;
};
