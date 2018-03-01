import EmberObject from '@ember/object';
import { isEmpty } from '@ember/utils';
import computed from 'ember-computed-decorators';
import $ from 'jquery';

/**
 * @class Size utility
 * Represents a CSS size value, such as 'auto', '25px', '100%' or 50.
 * Parses a given size value into a number and units (if any), and updates this object's attrs accordingly.
 * If the given value is a number without units, assumes units are 'px' by default.
 * If the given value is empty or is the string 'auto', sets `auto` to `true`.
 * @example Parses '24.5%' into `{ number: 24.5, units: '%', auto: false }`
 * @public
 */
const Size = EmberObject.extend({
  /**
   * The numeric value, without units.
   * @type {Number}
   * @public
   */
  number: null,

  /**
   * The units string (e.g., 'px', '%', 'rem', etc).
   * @type {String}
   * @public
   */
  units: null,

  /**
   * Is true if this value is either empty or 'auto'.
   * @type {Boolean}
   * @public
   */
  auto: true,

  /**
   * The value as a number or string.
   * Set this property as an alternative to setting the `number, `units` & `auto` attrs. This property's setter
   * will then parse the given value and update the other attrs accordingly.
   * @type {Number|String}
   * @public
   */
  @computed
  value: {
    set(v) {
      if (isEmpty(v) || (v === 'auto')) {
        this.setProperties({
          auto: true,
          units: '',
          number: null
        });
      } else if ($.isNumeric(v)) {
        this.setProperties({
          number: v,
          units: 'px',
          auto: false
        });
      } else {
        const match = String(v).match(/([\d\.]+)([^\d]*)/);
        const number = match && Number(match[1]);
        const units = (match && match[2]) || 'px';

        if ($.isNumeric(number)) {
          this.setProperties({
            number,
            units,
            auto: false
          });
        } else {
          this.setProperties({
            number: null,
            auto: true,
            units: ''
          });
        }
      }
      return v;
    }
  },

  /**
   * Serializes this value to a string.
   * If this value is undefined or 'auto', returns empty string.
   * @returns {string}
   * @public
   */
  @computed('auto', 'number', 'units')
  string(auto, number, units) {
    return auto ? '' : `${number}${units}`;
  },

  /**
   * Adds another Size instance to this Size instance, returning a new instance with the result.
   * @param {Object} obj The object whose value is to be added with this one.
   * @returns {Object}
   * @public
   */
  add(obj) {
    if (!obj) {
      return this;
    }

    const { auto, number, units } = obj.getProperties('auto', 'number', 'units');

    // If given value has no defined width, ignore it
    if (auto) {
      return this;
    }

    const myUnits = this.get('units');
    if (myUnits !== units) {

      // If units are mixed, can't compute sum
      return Size.create({
        value: 'auto'
      });

    } else {

      // Units match, just add numbers.
      return Size.create({
        number: number + this.get('number'),
        units,
        auto: false
      });
    }

  }
});

export default Size;
