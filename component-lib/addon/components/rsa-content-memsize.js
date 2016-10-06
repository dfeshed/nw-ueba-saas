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
import computed from 'ember-computed-decorators';

import layout from '../templates/components/rsa-content-memsize';

const {
  $,
  Component,
  inject: {
    service
  }
} = Ember;

const byteSizing = {
  label: 'memsize.B',
  bytes: 0
};

const sizing = [{
  label: 'memsize.GB',
  bytes: Math.pow(1024, 3)
}, {
  label: 'memsize.MB',
  bytes: Math.pow(1024, 2)
}, {
  label: 'memsize.KB',
  bytes: 1024
}, byteSizing];

export default Component.extend({
  layout,
  tagName: 'span',
  classNames: 'rsa-content-memsize',
  attributeBindings: ['title'],
  i18n: service(),

  /**
   * Size in bytes.
   * @type {number}
   * @public
   */
  size: undefined,

  @computed('size')
  translatedSize: (size) => {
    if ($.isNumeric(size)) {
      return sizing.find(({ bytes }) => size >= bytes);
    }
    return byteSizing;
  },

  @computed('size', 'translatedSize')
  displaySize: (size, { bytes }) => bytes ? (size / bytes).toFixed(1) : size,

  /**
   * Tooltip for DOM, showing the precise size value in bytes.
   * @type {string}
   * @public
   */
  @computed('size')
  title(size) {
    const i18n = this.get('i18n');
    return $.isNumeric(size) ? `${size} ${i18n.t('memsize.B')}` : '';
  }
});
