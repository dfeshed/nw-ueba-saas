import EmberObject, { computed } from '@ember/object';
import Size from 'respond/utils/css/size';
import { htmlSafe } from '@ember/string';

/**
 * Column model for the GroupTable component.
 * Responsible for parsing a given configuration object.
 * @type {Object}
 * @public
 */
export default EmberObject.extend({
  /**
   * Name of the data property from which values should be read for this column.
   * @type {String}
   * @public
   */
  field: '',

  /**
   * Optional title for column header. If missing, `field` is assumed.
   * @type {String}
   * @public
   */
  title: '',

  /**
   * If false, indicates that the column should be hidden from end-user.
   * @type {Boolean}
   * @public
   */
  visible: true,

  /**
   * Width of the column, in CSS units. If unitless, then 'px' is assumed.
   * @type {String|Number}
   * @default 100 (px)
   * @public
   */
  width: 100,

  /**
   * Optional minimum of the column, in CSS units. If unitless, then 'px' is assumed.
   * @type {String|Number}
   * @public
   */
  minWidth: null,

  /**
   * Optional maximum of the column, in CSS units. If unitless, then 'px' is assumed.
   * @type {String|Number}
   * @public
   */
  maxWidth: null,

  /**
   * Configurable name of Ember Component to be used for rendering this column's data.
   * @type {String}
   * @public
   */
  componentClass: null,

  parsedWidth: computed('width', function() {
    return Size.create({ value: this.width });
  }),

  parsedMinWidth: computed('width', function() {
    return Size.create({ value: this.width });
  }),

  parsedMaxWidth: computed('width', function() {
    return Size.create({ value: this.width });
  }),

  // Generates a safe CSS string that applies this column's width to the DOM. Used for template bindings.
  // Note: In some browsers, setting `width` alone on a <td> isn't enough; you need `min-width` & `max-width` too.
  styleText: computed(
    'parsedWidth.string',
    'parsedMinWidth.string',
    'parsedMaxWidth.string',
    function() {
      const styleText = `width:${this.parsedWidth?.string};
        min-width:${this.parsedMinWidth?.string || this.parsedWidth?.string};
        max-width:${this.parsedMaxWidth?.string || this.parsedWidth?.string}`;
      return htmlSafe(styleText);
    }
  )
});
