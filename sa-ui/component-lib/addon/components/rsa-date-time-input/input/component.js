import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import {
  parseDatePart
} from '../util/date-time-utility';
import { format } from '../util/date-format';
import layout from './template';

export default Component.extend({
  layout,
  classNames: ['date-time-input'],
  classNameBindings: ['type'],

  /**
   * The numeric value for the date part represented. Months are zero-based (Jan = 0, Feb = 1)
   * @property value
   * @public
   */
  value: null,

  /**
   * The type of date part represented by the component. Possible types are: "year", "month", "date", "hour", "minute",
   * and "second"
   * @property type
   * @public
   */
  type: null,

  /**
   * Stub for the onChange method, which is invoked any time the value changes
   * @method onChange
   * @public
   */
  onChange: () => {},

  /**
   * The formatted version of the current value, which includes left-padded zero for single digit values
   * @property formattedValue
   * @param value
   * @param type
   * @returns {*}
   * @public
   */
  @computed('value', 'type')
  formattedValue(value, type) {
    // convert the zero-based month value to the human readable month, e.g., 0 (jan) to 1 (jan)
    const val = value !== null && type === 'month' ? value + 1 : value;
    return format(val, type);
  },

  /**
   * The max length of the input
   * @property length
   * @param type
   * @returns {number}
   * @public
   */
  @computed('type')
  length(type) {
    return type === 'year' ? 4 : 2;
  },

  /**
   * Selects the full value in the text input
   * @method selectValueText
   * @public
   */
  selectValueText() {
    const value = `${this.get('formattedValue')}`;
    this.element.querySelector('input').setSelectionRange(0, value.length);
  },

  /**
   * Converts the value to the internal representation valu, which includes (a) adjusting the month to the zero-based
   * value (january = 0) expected by moment.js, and (b) auto-adjusting single and double digit year values to be from the
   * 21st century (i.e., 5 = 2005, 20 = 2020, etc)
   * @param value
   * @param type
   * @returns {*}
   * @private
   */
  _convert(value, type) {
    if (value && type === 'month') {
      return value - 1; // re-adjust month to zero based index
    }
    if (value && type === 'year' && (value >= 0 && value <= 99)) {
      return 2000 + value; // adjust values for years between 0 and 99 to be 2000 - 2099
    }
    return value;
  },

  actions: {
    handleFocusOut(event) {
      const { value: previousValue, type } = this.getProperties('value', 'type');
      // parse the string value into a numeric value (or null if NaN)
      const value = parseDatePart(event.target.value);
      // format the new value and set it on the text input
      this.element.querySelector('input').value = format(value, type);
      // convert the value into the internal representation
      const updatedValue = this._convert(value, type);
      // set the converted value on the component
      this.set('value', updatedValue);
      // check if the new value is different from the old value
      if (updatedValue !== previousValue) {
        // if different, invoke the onChange action
        this.get('onChange')(updatedValue);
      }
    },

    handleFocusIn() {
      this.selectValueText();
    },

    handleClick() {
      this.selectValueText();
    }
  }
});
