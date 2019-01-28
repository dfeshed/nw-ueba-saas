import Component from '@ember/component';
import moment from 'moment';
import computed, { notEmpty } from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import layout from './template';

export default Component.extend({
  layout,
  i18n: service(),

  classNames: ['rsa-date-time-range'],
  classNameBindings: ['hasErrors'],
  attributeBindings: ['title', 'startTimestamp', 'endTimestamp'],

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
   * The character used to separate the two date/time controls
   * @property rangeSeparatorCharacter
   * @public
   */
  rangeSeparatorCharacter: '-',

  /**
   * The unix timestamp (in ms) for the start date/time portion of the time range
   * @property start
   * @public
   */
  start: null,

  /**
   * The unix timestamp (in ms) for the end date/time portion of the time range
   * @property end
   * @public
   */
  end: null,

  didUpdateAttrs() {
    this._super(...arguments);
    // When the attributes of the component is changed (like start, end) clear out any previously set error state.
    this.setProperties({
      startErrors: [],
      endErrors: []
    });
  },

  /**
   * The starting date/time unix timestamp (in ms) that accounts for adjustment when includeSeconds is false. If
   * includeSeconds is false, we convert the initial start timestamp's seconds value down to 0.
   * @property startTimestamp
   * @param start
   * @param includeSeconds
   * @returns {*}
   * @public
   */
  @computed('start', 'includeSeconds')
  startTimestamp(start, includeSeconds) {
    let startTimestamp = start;
    if (!includeSeconds) {
      startTimestamp = moment(startTimestamp).seconds(0).valueOf();
    }
    return startTimestamp;
  },

  /**
   * The ending date/time unix timestamp (in ms) that accounts for adjustment when includeSeconds is false. If
   * includeSeconds is false, we convert the initial end timestamp's seconds value up to 59.
   * @property endTimestamp
   * @param end
   * @param includeSeconds
   * @returns {*}
   * @public
   */
  @computed('end', 'includeSeconds')
  endTimestamp(end, includeSeconds) {
    let endTimestamp = end;
    if (!includeSeconds) {
      endTimestamp = moment(endTimestamp).seconds(59).valueOf();
    }
    return endTimestamp;
  },

  /**
   * Returns the textual desciption of the duration represented in the date/time range. For example, if the date range
   * is 02/22/1976 to 02/22/2018, the duration will be returned (if locale is 'en') as "42 years"
   * @property duration
   * @param start
   * @param end
   * @param hasErrors
   * @returns {string}
   * @public
   */
  @computed('startTimestamp', 'endTimestamp', 'hasErrors')
  duration(start, end, hasErrors) {
    if (!hasErrors) {
      const i18n = this.get('i18n');
      const duration = moment.duration(moment(end).diff(moment(start)));
      const years = duration.years() > 0 ? `${duration.years()} ${i18n.t('dateTime.years')}` : '';
      const months = duration.months() > 0 ? `${duration.months()} ${i18n.t('dateTime.months')}` : '';
      const days = duration.days() > 0 ? `${duration.days()} ${i18n.t('dateTime.days')}` : '';
      const hours = duration.hours() > 0 ? `${duration.hours()} ${i18n.t('dateTime.hours')}` : '';
      const minutes = duration.minutes() > 0 ? `${duration.minutes()} ${i18n.t('dateTime.minutes')}` : '';
      const seconds = `${duration.seconds()} ${i18n.t('dateTime.seconds')}`;
      return `${years} ${months} ${days} ${hours} ${minutes} ${seconds}`.replace(/\s\s+/g, ' ');
    }
  },

  /**
   * Determines the title attribute for the component's "tooltip". If there are errors messages, it returns them,
   * otherwise it will return the calculated duration.
   * @propety title
   * @param errorMessages
   * @param duration
   * @returns {*|string}
   * @public
   */
  @computed('errorMessages', 'duration')
  title(errorMessages, duration) {
    const i18n = this.get('i18n');
    return errorMessages || `${i18n.t('dateTime.duration')}: ${duration}`;
  },

  _createErrorMessage(errors, labelKey) {
    const i18n = this.get('i18n');
    const errorMessages = errors.map((error) => i18n.t(`dateTime.${error}`));
    if (errorMessages.length) {
      return `${i18n.t(labelKey)}: ${errorMessages.join(', ')}`;
    } else {
      return '';
    }
  },

  /**
   * Returns true if any part of the time range has an error
   * @property hasErrors
   * @public
   */
  @notEmpty('errors') hasErrors: false,

  /**
   * Returns an array of error codes for general range-related errors, such as the end time being temporally before
   * the start time.
   * @property rangeErrors
   * @param start
   * @param end
   * @returns {*}
   * @public
   */
  @computed('startTimestamp', 'endTimestamp')
  rangeErrors(start, end) {
    return end < start ? ['endBeforeStart'] : [];
  },

  /**
   * The translation of the start date/time error codes into a human readable description of the error(s)
   * @property startErrorMessage
   * @param startErrors
   * @returns {*|string}
   * @public
   */
  @computed('startErrors')
  startErrorMessage(startErrors) {
    return this._createErrorMessage(startErrors, 'dateTime.start');
  },

  /**
   * The translation of the end date/time error codes into a human readable description of the error(s)
   * @property endErrorMessage
   * @param endErrors
   * @returns {*|string}
   * @public
   */
  @computed('endErrors')
  endErrorMessage(endErrors) {
    return this._createErrorMessage(endErrors, 'dateTime.end');
  },

  /**
   * The translation of the range error codes into a human readable description of the error(s)
   * @property rangeErrorMessage
   * @param rangeErrors
   * @returns {*|string}
   * @public
   */
  @computed('rangeErrors')
  rangeErrorMessage(rangeErrors) {
    return this._createErrorMessage(rangeErrors, 'dateTime.range');
  },

  /**
   * An array of all of the error codes found in either the start date/time, end date/time, or for the entire range.
   * @property errors
   * @param rangeErrors
   * @param startErrors
   * @param endErrors
   * @returns {*[]}
   * @public
   */
  @computed('rangeErrors', 'startErrors', 'endErrors')
  errors(rangeErrors, startErrors, endErrors) {
    return [].concat(rangeErrors, startErrors, endErrors);
  },

  @computed('hasErrors')
  errorMessages(hasError) {
    if (hasError) {
      return `${this.get('rangeErrorMessage')} ${this.get('startErrorMessage')} ${this.get('endErrorMessage')}`;
    }
  },

  /**
   * Initialization handler to ensure that empty start/end values are initialized with the currrent date/time
   * @method onInit
   * @private
   */
  onInit: function() {
    this.setProperties({
      start: this.get('start') || moment().valueOf(),
      end: this.get('end') || moment().valueOf(),
      startErrors: [],
      endErrors: []
    });
  }.on('init'),

  /**
   * Invoked whenever the start or end date/time changes and is valid
   * @method onChange
   * @public
   */
  onChange: () => {},

  /**
   * Invoked whenever the start or end date/time changes and is NOT valid
   * @method onError
   * @public
   */
  onError: () => {},

  actions: {
    // capture invalid start and end times to persist in state, so that error notification is justified
    onChangeStart(start) {
      this.setProperties({
        startErrors: [], // reset the start errors array since the start of the range has no errors
        start
      });
      if (!this.get('hasErrors')) {
        const { onChange, endTimestamp } = this.getProperties('onChange', 'endTimestamp');
        onChange(start, endTimestamp);
      } else {
        // invoke onError since this may be a date range error (end before start)
        const { onError, errors, endTimestamp } = this.getProperties('onError', 'errors', 'endTimestamp');
        onError(errors, start, endTimestamp);
      }
    },
    onChangeEnd(end) {
      this.setProperties({
        endErrors: [], // reset the end errors array since the start of the range has no errors
        end
      });
      if (!this.get('hasErrors')) {
        const { onChange, startTimestamp } = this.getProperties('onChange', 'startTimestamp');
        onChange(startTimestamp, end);
      } else {
        // invoke onError since this may be a date range error (end before start)
        const { onError, errors, startTimestamp } = this.getProperties('onError', 'errors', 'startTimestamp');
        onError(errors, startTimestamp, end);
      }
    },
    onStartError(errors) {
      const onError = this.get('onError');
      this.set('startErrors', errors);
      const { startTimestamp, endTimestamp } = this.getProperties('startTimestamp', 'endTimestamp');
      onError(this.get('errors'), startTimestamp, endTimestamp);
    },
    onEndError(errors) {
      const onError = this.get('onError');
      this.set('endErrors', errors);
      const { startTimestamp, endTimestamp } = this.getProperties('startTimestamp', 'endTimestamp');
      onError(this.get('errors'), startTimestamp, endTimestamp);
    }
  }
});
