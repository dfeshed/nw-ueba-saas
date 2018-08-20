import { debug } from '@ember/debug';

/**
 * Error helper for logging errors to the console.
 * @param response
 * @param type
 * @private
 */
export const handleError = (type, response) => {
  const debugResponse = JSON.stringify(response);
  debug(`handleError: ${type} ${debugResponse}`);
};
