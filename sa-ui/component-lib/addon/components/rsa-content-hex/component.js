/**
 * @file Content Hex component
 * Formats the provided number into hex
 * @public
 */
import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { isPresent } from '@ember/utils';
import layout from './template';

export default Component.extend({
  layout,

  tagName: 'span',

  classNames: 'rsa-content-hex',

  /**
   * Required. Value in number
   * @type {number}
   * @public
   */
  value: undefined,

  @computed('value')
  formatedHexValue: (value) => {
    if (!isPresent(value) || value === 0) {
      return value;
    }
    const hexadecimal = Math.abs(value).toString(16).toUpperCase();

    return value < 0 ? `-0x${hexadecimal}` : `0x${hexadecimal}`;
  }
});
