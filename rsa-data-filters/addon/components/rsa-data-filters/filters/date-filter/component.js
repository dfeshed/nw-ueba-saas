import Component from '@ember/component';
import layout from './template';
import { assign } from '@ember/polyfills';
import computed from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import moment from 'moment';
import { isPresent, typeOf } from '@ember/utils';
import config from 'ember-get-config';

export default Component.extend({
  layout,

  classNames: ['date-filter'],

  timezone: service(),

  dateFormat: service(),

  timeFormat: service(),

  valueType: 'DATE',

  hasCustomDate: false,

  defaults: {
    showCustomDate: true,

    filterValue: {
      value: [null, null],
      unit: null
    }
  },

  customDateErrorMessage: null,

  startTime: null,

  endTime: null,

  @computed('startTime', 'endTime')
  hasCustomDateError(start, end) {
    let errorMessage = null;
    let hasError = false;

    if (isPresent(start) && isPresent(end) && start >= end) {
      hasError = true;
      errorMessage = 'dataFilters.customDateErrorStartAfterEnd';
    }
    this.set('customDateErrorMessage', errorMessage);
    return hasError;
  },

  @computed('options')
  filterValue: {
    get() {
      const { filterValue: { value, unit }, timeframes = [] } = this.get('options');
      if (unit) {
        const timeFrame = timeframes.find((time) => time.unit === unit && time.value === value[0]);
        return { value: [ value ], unit: timeFrame };
      } else {
        this.set('startTime', value[0]);
        this.set('endTime', value[1]);
        return { value };
      }

    },

    set(key, value) {
      return value;
    }

  },

  init() {
    this._super(arguments);
    const options = assign({}, this.get('defaults'), this.get('filterOptions'));
    const { filterValue: { value } } = options;
    this.set('hasCustomDate', value.compact().length === 2);
    this.set('options', options);
  },

  @computed('filterValue.value', 'timezone.selected.zoneId', 'dateFormat.selected.format', 'timeFormat.selected.format')
  customDateRangeStart(timestamp = []) {
    return this._toLocalTime(timestamp[0]);
  },

  @computed('filterValue.value', 'timezone.selected.zoneId', 'dateFormat.selected.format', 'timeFormat.selected.format')
  customDateRangeEnd(timestamp) {
    return this._toLocalTime(timestamp[1]);
  },


  _toLocalTime(timestamp) {
    if (typeOf(timestamp) === 'number') {
      return moment.tz(timestamp, this._getTimezone()).format(`${this._getDateFormat()} ${this._getTimeFormat()}`);
    } else {
      return null;
    }
  },


  _getTimezone() {
    return this.get('timezone.selected.zoneId') || this.get('timezone.options').findBy('zoneId', config.timezoneDefault).zoneId;
  },

  _getDateFormat() {
    return this.get('dateFormat.selected.format') || this.get('dateFormat.options').findBy('key', config.dateFormatDefault).format;
  },

  _getTimeFormat() {
    const timeFormat = this.get('timeFormat.selected.format') || this.get('timeFormat.options').findBy('key', config.timeFormatDefault).format;
    return timeFormat.replace('.SSS', '');
  },


  _toUTCTimestamp(date) {
    const dateParts = [date.getFullYear(), date.getMonth(), date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds()];
    return moment.tz(dateParts, this._getTimezone()).valueOf();
  },


  _handleChange(value, unit) {
    const { name } = this.get('filterOptions');
    const onChange = this.get('onChange');
    if (onChange) {
      if (unit) {
        onChange({ name, operator: 'GREATER_THAN', value, unit });
      } else {
        onChange({ name, operator: 'BETWEEN', value });
      }
    }
  },


  actions: {

    toggleCustomDate() {
      this.toggleProperty('hasCustomDate');
      if (!this.get('hasCustomDate')) {
        this._handleChange([], null); // clearing the custom date
      }
    },

    customStartDateChanged([date]) {
      const start = date ? this._toUTCTimestamp(date) : null;
      this.set('startTime', start);
      const end = this.get('endTime');
      this._handleChange([start, end]);
    },

    customEndDateChanged([date]) {
      const end = date ? this._toUTCTimestamp(date) + 999 : null;
      this.set('endTime', end);
      const start = this.get('startTime');
      this._handleChange([start, end]);
    },

    onChangeTimeframe(option) {
      this.set('filterValue.unit', option);
      if (option) {
        const value = [option.value];
        this._handleChange(value, option.unit);
      } else {
        this._handleChange([], null);
      }
    }
  }
});
