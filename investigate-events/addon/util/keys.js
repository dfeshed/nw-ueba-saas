import _ from 'lodash';

/**
 * Map of `keyCode` and `key` values that would come from a `keyboardEvent`.
 * @private
 */
const keyMap = {
  arrowDown: { code: 40, key: 'ArrowDown' },
  arrowLeft: { code: 37, key: 'ArrowLeft' },
  arrowRight: { code: 39, key: 'ArrowRight' },
  arrowUp: { code: 38, key: 'ArrowUp' },
  backspace: { code: 8, key: 'Backspace' },
  enter: { code: 13, key: 'Enter' },
  escape: { code: 27, key: 'Escape' },
  space: { code: 32, key: ' ' },
  tab: { code: 9, key: 'Tab' },
  delete: { code: 46, key: 'Delete' },
  shift: { code: 16, key: 'Shift' }
};

/**
 * Determines if the supplied value matches a key
 * @param {number} $0.code A key code
 * @param {string} $0.key A key name
 * @param {*} value Value to test
 * @return Boolean
 * @private
 */
const matches = ({ code, key }, value) => code === value || key === value;

// The following are function partials. They take in a function, and a value to
// supply that function as a parameter. In this case we're passing in an object
// from `keyMap` that is destructured to `code` and `key`. The function that's
// returned from `_.partial` takes in one more param which fulfills the `value`
// param of `matches`.
const matchesArrowDown = _.partial(matches, keyMap.arrowDown);
const matchesArrowLeft = _.partial(matches, keyMap.arrowLeft);
const matchesArrowRight = _.partial(matches, keyMap.arrowRight);
const matchesArrowUp = _.partial(matches, keyMap.arrowUp);
const matchesBackspace = _.partial(matches, keyMap.backspace);
const matchesEnter = _.partial(matches, keyMap.enter);
const matchesEscape = _.partial(matches, keyMap.escape);
const matchesSpace = _.partial(matches, keyMap.space);
const matchesTab = _.partial(matches, keyMap.tab);
const matchesDelete = _.partial(matches, keyMap.delete);
const matchesShift = _.partial(matches, keyMap.shift);

/**
 * Is the event from a down arrow keyboard event?
 * @param {Object} event A KeyboardEvent.
 * @return A Boolean value.
 * @public
 */
export const isArrowDown = (event) => matchesArrowDown(event.keyCode);
/**
 * Is the event from a left arrow keyboard event?
 * @param {Object} event A KeyboardEvent.
 * @return A Boolean value.
 * @public
 */
export const isArrowLeft = (event) => matchesArrowLeft(event.keyCode);
/**
 * Is the event from a right arrow keyboard event?
 * @param {Object} event A KeyboardEvent.
 * @return A Boolean value.
 * @public
 */
export const isArrowRight = (event) => matchesArrowRight(event.keyCode);
/**
 * Is the event from an up arrow keyboard event?
 * @param {Object} event A KeyboardEvent.
 * @return A Boolean value.
 * @public
 */
export const isArrowUp = (event) => matchesArrowUp(event.keyCode);
/**
 * Is the event from the backspace keyboard event?
 * @param {Object} event A KeyboardEvent.
 * @return A Boolean value.
 * @public
 */
export const isBackspace = (event) => matchesBackspace(event.keyCode);
/**
 * Is the event from an enter keyboard event?
 * @param {Object} event A KeyboardEvent.
 * @return A Boolean value.
 * @public
 */
export const isEnter = (event) => matchesEnter(event.keyCode);
/**
 * Is the event from an delete keyboard event?
 * @param {Object} event A KeyboardEvent.
 * @return A Boolean value.
 * @public
 */
export const isDelete = (event) => matchesDelete(event.keyCode);
/**
 * Is the event from an escape keyboard event?
 * @param {Object} event A KeyboardEvent.
 * @return A Boolean value.
 * @public
 */
export const isEscape = (event) => matchesEscape(event.keyCode);
/**
 * Is the event from a shift keyboard event?
 * @param {Object} event A KeyboardEvent.
 * @return A Boolean value.
 * @public
 */
export const isShift = (event) => matchesShift(event.keyCode);
/**
 * Is the event from a space keyboard event?
 * @param {Object} event A KeyboardEvent.
 * @return A Boolean value.
 * @public
 */
export const isSpace = (event) => matchesSpace(event.keyCode);
/**
 * Is the event from a tab keyboard event?
 * @param {Object} event A KeyboardEvent.
 * @return A Boolean value.
 * @public
 */
export const isTab = (event) => matchesTab(event.keyCode);

export default keyMap;