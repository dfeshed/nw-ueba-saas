import EmberObject from '@ember/object';
import Size from 'respond/utils/css/size';
import computed from 'ember-computed-decorators';
import { htmlSafe } from 'ember-string';

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

  @computed('width')
  parsedWidth(width) {
    return Size.create({ value: width });
  },

  @computed('width')
  parsedMinWidth(minWidth) {
    return Size.create({ value: minWidth });
  },

  @computed('width')
  parsedMaxWidth(maxWidth) {
    return Size.create({ value: maxWidth });
  },

  // Generates a safe CSS string that applies this column's width to the DOM. Used for template bindings.
  // Note: In some browsers, setting `width` alone on a <td> isn't enough; you need `min-width` & `max-width` too.
  @computed('parsedWidth.string', 'parsedMinWidth.string', 'parsedMaxWidth.string')
  styleText(parsedWidth, parsedMinWidth, parsedMaxWidth) {
    const styleText = `width:${parsedWidth};
      min-width:${parsedMinWidth || parsedWidth};
      max-width:${parsedMaxWidth || parsedWidth}`;
    return htmlSafe(styleText);
  }
});
