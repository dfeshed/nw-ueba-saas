import Ember from 'ember';
const {
  Helper
} = Ember;

/**
 * Action wrapper helper for disabling the bubbling of event
 * @param action
 * @returns {Function}
 * @public
 */
export function disableBubbling([action]) {
  return function(event) {
    event.stopPropagation();
    return action(event);
  };
}
export default Helper.helper(disableBubbling);
