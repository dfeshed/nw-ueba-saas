/**
 * @file Memory Size display component
 * Formats a given number of bytes into a string with units (e.g, "20 bytes", "26.1 KB").
 * If the given size is not numeric, displays the given size with no change.
 * If the given size is numeric but < 1024 bytes, returns the given size + "bytes" (localized string).
 * Otherwise, returns X + "KB" (localized string) where X = the given size in bytes / 1024, rounded to first decimal.
 * Note that the tooltip for this component's DOM will also be set.
 * @public
 */
import Ember from 'ember';

const { computed, $, Component } = Ember;

export default Component.extend({
  tagName: 'span',
  classNames: 'rsa-content-memsize',
  attributeBindings: ['title'],

  /**
   * Size in bytes.
   * @type {number}
   * @public
   */
  size: undefined,

  /**
   * Computes whether or not to display the size in KB rather than bytes.
   * Any size >= 1024 bytes will be displayed as KB.
   * @type {boolean}
   * @private
   */
  displayAsKB: computed('size', function() {
    const size = this.get('size');
    return $.isNumeric(size) && (size >= 1024);
  }),

  /**
   * The size amount in kilobytes, as a decimal string (e.g., "2.1").
   * @type {string}
   * @private
   */
  sizeAsKB: computed('size', function() {
    return (this.get('size') / 1024).toFixed(1);
  }),

  /**
   * Tooltip for DOM, showing the precise size value in bytes.
   * @type {string}
   * @public
   */
  title: computed('size', function() {
    const size = this.get('size');
    const i18n = this.get('i18n');
    return $.isNumeric(size) ? `${size} ${i18n.t('investigate.bytes')}` : '';
  })
});
