/**
 * Class definitions for the various types of query grammar components.
 */
import EmberObject from '@ember/object';
import {
  CLOSE_PAREN,
  OPEN_PAREN,
  OPERATOR_AND,
  OPERATOR_OR
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
 * Logical operator class for AND.
 */
const OperatorAnd = Filter.extend({
  componentName: 'query-container/logical-operator',

  init() {
    this.set('type', OPERATOR_AND);
  }
});

/**
 * Logical operator class for AND.
 */
const OperatorOr = Filter.extend({
  componentName: 'query-container/logical-operator',

  init() {
    this.set('type', OPERATOR_OR);
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
  OpenParen,
  OperatorAnd,
  OperatorOr
};