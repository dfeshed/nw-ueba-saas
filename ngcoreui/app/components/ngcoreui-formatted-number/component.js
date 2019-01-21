/**
 * @file Formatted Number display component
 * Formats a given integer into a string with scale (e.g, "2.1 Million", "26.1 Billion").
 * If the given size is numeric but is less than 1 Million, returns the given size
 * formatted with commas.
 * Note that the tooltip for this component's DOM will also be set.
 * @public
 */

// This is based on `ngcoreui-memsize`, see that component for note
// regarding translations

import $ from 'jquery';
import Component from '@ember/component';
import computed from 'ember-computed-decorators';

const zero = {
  label: '',
  values: 0
};

const sizing = [{
  label: 'Quintillion',
  values: Math.pow(1000, 6)
}, {
  label: 'Trillion',
  values: Math.pow(1000, 4)
}, {
  label: 'Billion',
  values: Math.pow(1000, 3)
}, {
  label: 'Million',
  values: Math.pow(1000, 2)
}, zero];

export default Component.extend({
  tagName: 'span',
  classNames: 'ngcoreui-formatted-number',
  attributeBindings: ['title'],

  /**
   * Integer value
   * @type {number}
   * @public
   */
  size: undefined,

  @computed('size')
  parsedSize: (size) => {
    if ($.isNumeric(size)) {
      return sizing.find(({ values }) => Math.abs(size) >= values);
    }
    return zero;
  },

  @computed('size', 'parsedSize')
  displaySize: (size, { values }) => values > 1000 ? (size / values).toFixed(1) : parseInt(size, 10).toLocaleString(),

  /**
   * Tooltip for DOM, showing the precise size value fomatted with commas.
   * @type {string}
   * @public
   */
  @computed('size')
  title(size) {
    return parseInt(size, 10).toLocaleString();
  }
});
