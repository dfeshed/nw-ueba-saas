import _ from 'lodash';

/**
 * Map of `code` and `key` values that would come from a `keyboardEvent`.
 * @private
 */
const keyMap = {
  arrowDown: { code: 40, key: 'ArrowDown' },
  arrowLeft: { code: 37, key: 'ArrowLeft' },
  arrowRight: { code: 39, key: 'ArrowRight' },
  arrowUp: { code: 38, key: 'ArrowUp' },
  backspace: { code: 8, key: 'Backspace' },
  closeParen: { code: 48, key: ')' },
  delete: { code: 46, key: 'Delete' },
  enter: { code: 13, key: 'Enter' },
  escape: { code: 27, key: 'Escape' },
  openParen: { code: 57, key: '(' },
  shift: { code: 16, key: 'Shift' },
  space: { code: 32, key: ' ' },
  tab: { code: 9, key: 'Tab' },
  home: { code: 36, key: 'Home' },
  end: { code: 35, key: 'End' },
  Key_a: { key: 'a' },
  Key_A: { key: 'A' }
};

/**
 * Determines if the supplied value matches a key
 * @param {number} $0.code A key code
 * @param {string} $0.key A key name
 * @param {*} value Value to test
 * @return Boolean
 * @private
 */
const matches = ({ code, key }, value) => key === value || code === value;

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
const matchesCloseParen = _.partial(matches, keyMap.closeParen);
const matchesDelete = _.partial(matches, keyMap.delete);
const matchesEnter = _.partial(matches, keyMap.enter);
const matchesEscape = _.partial(matches, keyMap.escape);
const matchesOpenParen = _.partial(matches, keyMap.openParen);
const matchesShift = _.partial(matches, keyMap.shift);
const matchesSpace = _.partial(matches, keyMap.space);
const matchesTab = _.partial(matches, keyMap.tab);
const matchesHome = _.partial(matches, keyMap.home);
const matchesEnd = _.partial(matches, keyMap.end);

/**
 * Is the event from a down arrow keyboard event?
 * @param {Object} event A KeyboardEvent.
 * @return A Boolean value.
 * @public
 */
export const isArrowDown = (event) => matchesArrowDown(event.key);
/**
 * Is the event from a left arrow keyboard event?
 * @param {Object} event A KeyboardEvent.
 * @return A Boolean value.
 * @public
 */
export const isArrowLeft = (event) => matchesArrowLeft(event.key);
/**
 * Is the event from a right arrow keyboard event?
 * @param {Object} event A KeyboardEvent.
 * @return A Boolean value.
 * @public
 */
export const isArrowRight = (event) => matchesArrowRight(event.key);
/**
 * Is the event from an up arrow keyboard event?
 * @param {Object} event A KeyboardEvent.
 * @return A Boolean value.
 * @public
 */
export const isArrowUp = (event) => matchesArrowUp(event.key);
/**
 * Is the event from the backspace keyboard event?
 * @param {Object} event A KeyboardEvent.
 * @return A Boolean value.
 * @public
 */
export const isBackspace = (event) => matchesBackspace(event.key);
/**
 * Is the event from shit + 0?
 * @param {Object} event A KeyboardEvent.
 * @return A Boolean value.
 * @public
 */
export const isCloseParen = (event) => matchesCloseParen(event.key);
/**
 * Is the event from an delete keyboard event?
 * @param {Object} event A KeyboardEvent.
 * @return A Boolean value.
 * @public
 */
export const isDelete = (event) => matchesDelete(event.key);
/**
 * Is the event from an enter keyboard event?
 * @param {Object} event A KeyboardEvent.
 * @return A Boolean value.
 * @public
 */
export const isEnter = (event) => matchesEnter(event.key);
/**
 * Is the event from an escape keyboard event?
 * @param {Object} event A KeyboardEvent.
 * @return A Boolean value.
 * @public
 */
export const isEscape = (event) => matchesEscape(event.key);
/**
 * Is the event from shit + 9?
 * @param {Object} event A KeyboardEvent.
 * @return A Boolean value.
 * @public
 */
export const isOpenParen = (event) => matchesOpenParen(event.key);
/**
 * Is the event from a shift keyboard event?
 * @param {Object} event A KeyboardEvent.
 * @return A Boolean value.
 * @public
 */
export const isShift = (event) => matchesShift(event.key);
/**
 * Is the event from shit + tab?
 * @param {Object} event A KeyboardEvent.
 * @return A Boolean value.
 * @public
 */
export const isShiftTab = (event) => matchesTab(event.key) && event.shiftKey;
/**
 * Is the event from a space keyboard event?
 * @param {Object} event A KeyboardEvent.
 * @return A Boolean value.
 * @public
 */
export const isSpace = (event) => matchesSpace(event.key);
/**
 * Is the event from a tab keyboard event?
 * @param {Object} event A KeyboardEvent.
 * @return A Boolean value.
 * @public
 */
export const isTab = (event) => matchesTab(event.key);

/**
 * Is the event from a home keyboard event?
 * @param {Object} event A KeyboardEvent.
 * @return A Boolean value.
 * @public
 */
export const isHome = (event) => matchesHome(event.key) || (event.metaKey && matchesArrowLeft(event.key));

/**
 * Is the event from a end keyboard event?
 * @param {Object} event A KeyboardEvent.
 * @return A Boolean value.
 * @public
 */
export const isEnd = (event) => matchesEnd(event.key) || (event.metaKey && matchesArrowRight(event.metaKey));

export const isCtrlA = (event) => [keyMap.Key_A.key, keyMap.Key_a.key].includes(event.key) && event.ctrlKey;

export default keyMap;