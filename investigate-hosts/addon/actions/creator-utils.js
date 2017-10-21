import Ember from 'ember';
const { Logger } = Ember;

/**
 * Error helper for logging errors to the console.
 * @param response
 * @param type
 * @private
 */
export const handleError = (type, response) => {
  Logger.error(type, response);
};