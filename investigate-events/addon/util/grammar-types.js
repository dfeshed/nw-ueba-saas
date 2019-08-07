/**
 * Class definitions for the various types of query grammar components.
 */
import EmberObject from '@ember/object';
import {
  CLOSE_PAREN,
  OPEN_PAREN
} from 'investigate-events/constants/pill';

const { log } = console; // eslint-disable-line

/**
 * Base grammar component class.
 */
const Filter = EmberObject.extend({
  id: undefined,
  isFocused: false,
  isSelected: false,
  type: undefined
});

/**
 * Close parentheses class.
 */
const CloseParen = Filter.extend({
  componentName: 'query-container/close-paren',

  /**
   * Id of the matching open parenthesis within the query.
   * @type {string}
   */
  twinId: undefined,

  init() {
    this.set('type', CLOSE_PAREN);
  }
});

/**
 * Open parentheses class.
 */
const OpenParen = Filter.extend({
  componentName: 'query-container/open-paren',

  /**
   * Id of the matching close parenthesis within the query.
   * @type {string}
   */
  twinId: undefined,

  init() {
    this.set('type', OPEN_PAREN);
  }
});

export {
  CloseParen,
  OpenParen
};