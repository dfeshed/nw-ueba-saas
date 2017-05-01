import Ember from 'ember';
const { Logger } = Ember;
/**
 * Error helper for logging fetch errors to the console.
 * @param response
 * @param type
 * @private
 */
export const handleContentRetrievalError = (response, type) => {
  if (response.code !== 2) {
    Logger.error(`Could not retrieve ${type} data`, response);
  }
};

/**
 * Error helper for logging data delete errors to the console.
 * @param response
 * @param type
 * @private
 */
export const handleContentDeletionError = (response, type) => {
  if (response.code !== 2) {
    Logger.error(`Could not delete ${type} data`, response);
  }
};

/**
 * Error helper for logging data update errors to the console.
 * @param response
 * @param type
 * @private
 */
export const handleContentUpdateError = (response, type) => {
  if (response.code !== 2) {
    Logger.error(`Could not update ${type}`, response);
  }
};

export const handleContentCreationError = (response, type) => {
  if (response.code !== 2) {
    Logger.error(`Could not create ${type}`, response);
  }
};
