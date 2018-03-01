/**
 * @file Rounded decimal display component
 * Formats the value of a number rounded to the nearest integer and formats a number using fixed-point notation
 * Ex: 100.53345 => 100.53
 * @public
 */
import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { assert } from '@ember/debug';
import { isPresent } from '@ember/utils';
import layout from './template';

export default Component.extend({

  layout,

  tagName: 'span',

  classNames: 'rsa-rounded-decimal',

  /**
   * Required. Value to be rounded off
   * @type {number}
   * @public
   */
  value: undefined,

  /**
   * Optional. The number of digits to appear after the decimal point; defaults 2
   * @type {number}
   * @public
   */
  digits: 2,

  @computed('value', 'digits')
  roundedValue(value, digits) {
    assert('value must be provided', isPresent(value));
    return parseFloat(value).toFixed(digits);
  }
});
