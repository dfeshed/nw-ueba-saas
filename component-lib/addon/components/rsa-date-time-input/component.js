import Component from '@ember/component';
import moment from 'moment';
import computed, { notEmpty } from 'ember-computed-decorators';
import {
  getTimestamp,
  getDateParts,
  convertHourTo24HourClock
} from './util/date-time-utility';
import { validate } from './util/date-validation';
import layout from './template';

export default Component.extend({
  layout,
  classNames: ['rsa-date-time-input'],
  classNameBindings: ['hasError'],

  /**
   * A unix timestamp in milliseconds representing the date/time to display in the component
   * @property timestamp
   * @type number
   * @public
   */
  timestamp: null,

  /**
   * The timezone (e.g., America/Los_Angeles) to use in converting the timestamp to a human readable date relative
   * to the timezone
   * @property timezone
   * @type string
   * @public
   */
  timezone: 'UTC',

  /**
   * Whether the time should be displayed in the control. If false, only the Month/Day/Year will be displayed
   * @property includeTime
   * @public
   */
  includeTime: true,

  /**
   * Whether to show the seconds value in the control. Only relevant if includeTime is also true. If includeSeconds is
   * false, the control will show values only to the minute
   * @property includeSeconds
   * @public
   */
  includeSeconds: true,

  /**
   * Whether the displayed time should use the 12 hour clock. If true, the control will show AM/PM and the only valid
   * hour values will be in the range 1-12.
   * @property use12HourClock
   * @public
   */
  use12HourClock: false,

  /**
   * The date format used to determine the order of displaying months, days, and years. The supported options are:
   * MM/DD/YYYY (default), DD/MM/YYYY, YYYY/MM/DD
   * @property dateFormat
   * @public
   */
  dateFormat: 'MM/DD/YYYY', // supported options: MM/DD/YYYY, DD/MM/YYYY, YYYY/MM/DD

  /**
   * The character used to separate date part values (i.e., months, years, days)
   * @property dateSeparatorCharacter
   * @public
   */
  dateSeparatorCharacter: '/',
  /**
   * The character used to separate time part values (i.e., hours, minutes, seconds)
   * @property timeSeparatorCharacter
   * @public
   */
  timeSeparatorCharacter: ':',

  /**
   * The current numeric value of the year in the control. It may not may not be a valid value.
   * @property year
   * @type number
   * @public
   */
  year: null,

  /**
   * The current numeric value of the year in the control. It may not may not be a valid value.
   * @property month
   * @type number
   * @public
   */
  month: null,

  /**
   * The current numeric value of the year in the control. It may not may not be a valid value.
   * @property date
   * @type number
   * @public
   */
  date: null,

  /**
   * The current numeric value of the year in the control. It may not may not be a valid value.
   * @property hour
   * @type number
   * @public
   */
  hour: null,

  /**
   * The current numeric value of the year in the control. It may not may not be a valid value.
   * @property minute
   * @type number
   * @public
   */
  minute: null,

  /**
   * The current numeric value of the year in the control. It may not may not be a valid value.
   * @property second
   * @type number
   * @public
   */
  second: null,

  /**
   * Indicates whether the current date value in the control has an error (i.e., whether it is valid (false) or
   * invalid (true)
   * @property hasError
   * @type boolean
   * @public
   */
  @notEmpty('errors') hasError: false,

  /**
   * An object containing (as properties) all of the last valid values for the various parts of the date. The object
   * appears for example as { year: 1976, month: 1, date: 22, hour: 17, minute: 0, second: 9, amPm: 'pm' }
   * @property dateParts
   * @param timestamp
   * @param timezone
   * @param use12HourClock
   * @returns {*}
   * @public
   */
  @computed('timestamp', 'timezone', 'use12HourClock')
  dateParts(timestamp, timezone, use12HourClock) {
    return this.setProperties(getDateParts(this.get('timestamp'), timezone, use12HourClock));
  },

  /**
   * An array of error codes representing all errors in the current date time.
   * @property errors
   * @param use12HourClock
   * @returns {Array}
   * @public
   */
  @computed('use12HourClock', 'values.[]', 'dateParts')
  errors(use12HourClock) {
    return validate(this.get('values'), use12HourClock);
  },

  /**
   * Takes the date format (e.g., MM/DD/YYYY) and splits it in a lower-cased array (e.g., ['mm','dd','yyyy']). This
   * is used in the component template to order the date/time inputs according to the format's ordering.
   * @property dateFormatValues
   * @param dateFormat
   * @returns {string[]}
   * @public
   */
  @computed('dateFormat')
  dateFormatValues(dateFormat) {
    if (dateFormat) {
      return dateFormat.toLowerCase().split('/');
    }
  },

  @computed('year', 'month', 'date', 'hour', 'minute', 'second')
  values(year, month, date, hour, minute, second) {
    return [year, month, date, hour, minute, second];
  },

  /**
   * If no timestamp is declared on the component, set the timestamp to be the current timestamp on init
   * @private
   */
  onInit: function() {
    const timestamp = this.get('timestamp');
    if (timestamp === null) {
      this.set('timestamp', moment().valueOf()); // set the timestamp as the current unix timestamp in milliseconds
    }
  }.on('init'),

  /**
   * The function called when the date changes (and is valid). An invalid change to the date will not trigger the
   * onChange, but instead the onError function. The consumer of the component will pass in a closure action function
   * to replace this stubbed default.
   * @method onChange
   * @public
   */
  onChange: () => {},

  /**
   * The function called when the date changes (and is NOT valid). A valid change to the date will not trigger the
   * onError, but instead the onChange function. The consumer of the component will pass in a closure action function
   * to replace this stubbed default.
   * @method onChange
   * @public
   */
  onError: () => {},

  actions: {
    handleChange(type, value) {
      this.set(type, value);
      if (!this.get('hasError')) {
        const {
          year, month, date, hour, minute, second, amPm, use12HourClock, timezone
        } = this.getProperties('year', 'month', 'date', 'hour', 'minute', 'second', 'amPm', 'use12HourClock', 'timezone');

        const hr = use12HourClock ? convertHourTo24HourClock(hour, amPm) : hour;
        const updatedTimestamp = getTimestamp([year, month, date, hr, minute, second], timezone);
        this.set('timestamp', updatedTimestamp);
        this.get('onChange')(updatedTimestamp);
      } else {
        this.get('onError')(this.get('errors'));
      }
    }
  }
});
