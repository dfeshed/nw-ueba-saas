/**
 * @file Memory Size display component
 * Formats a given number of bytes into a string with units (e.g, "20 bytes", "26.1 KB").
 * If the given size is not numeric, displays the given size with no change.
 * If the given size is numeric but < 1024 bytes, returns the given size + "bytes" (localized string).
 * Otherwise, returns X + "KB" (localized string) where X = the given size in bytes / 1024, rounded to first decimal.
 * Note that the tooltip for this component's DOM will also be set.
 * @public
 */

// This was taken from the main sa-ui repo and modified to not use the translation
// service. If the translation service is ever introduced to direct-access, rsa-content-memsize
// can be subbed back in for this component.

import $ from 'jquery';
import Component from '@ember/component';
import computed from 'ember-computed-decorators';

const byteSizing = {
  label: 'B',
  bytes: 0
};

const sizing = [{
  label: 'GB',
  bytes: Math.pow(1024, 3)
}, {
  label: 'MB',
  bytes: Math.pow(1024, 2)
}, {
  label: 'KB',
  bytes: 1024
}, byteSizing];

export default Component.extend({
  tagName: 'span',
  classNames: 'direct-access-memsize',
  attributeBindings: ['title'],

  /**
   * Size in bytes.
   * @type {number}
   * @public
   */
  size: undefined,

  @computed('size')
  parsedSize: (size) => {
    if ($.isNumeric(size)) {
      return sizing.find(({ bytes }) => size >= bytes);
    }
    return byteSizing;
  },

  @computed('size', 'parsedSize')
  displaySize: (size, { bytes }) => bytes ? (size / bytes).toFixed(1) : size,

  /**
   * Tooltip for DOM, showing the precise size value in bytes.
   * @type {string}
   * @public
   */
  @computed('size')
  title(size) {
    return $.isNumeric(size) ? `${size} B` : '';
  }
});
