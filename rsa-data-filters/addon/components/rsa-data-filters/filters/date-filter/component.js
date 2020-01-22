import classic from 'ember-classic-decorator';
import { classNames, layout as templateLayout } from '@ember-decorators/component';
import { action, computed } from '@ember/object';
import { inject as service } from '@ember/service';
import Component from '@ember/component';
import layout from './template';
import { assign } from '@ember/polyfills';
import moment from 'moment';
import { isPresent, typeOf } from '@ember/utils';
import config from 'ember-get-config';

@classic
@templateLayout(layout)
@classNames('date-filter')
export default class DateFilter extends Component {
  @service
  timezone;

  @service
  dateFormat;

  @service
  timeFormat;

  valueType = 'DATE';
  hasCustomDate = false;
  customDateErrorMessage = null;
  startTime = null;
  endTime = null;

  @computed('startTime', 'endTime')
  get hasCustomDateError() {
    let errorMessage = null;
    let hasError = false;

    if (isPresent(this.startTime) && isPresent(this.endTime) && this.startTime >= this.endTime) {
      hasError = true;
      errorMessage = 'dataFilters.customDateErrorStartAfterEnd';
    }
    this.set('customDateErrorMessage', errorMessage);
    return hasError;
  }

  @computed('options')
  get filterValue() {
    const { filterValue: { value, unit }, timeframes = [] } = this.get('options');
    if (unit) {
      const timeFrame = timeframes.find((time) => time.unit === unit && time.value === value[0]);
      return { value: [ value ], unit: timeFrame };
    } else {
      this.set('startTime', value[0]);
      this.set('endTime', value[1]);
      return { value };
    }

  }

  set filterValue(value) {
    return value;
  }

  init() {
    super.init(arguments);
    this.defaults = this.defaults || {
      showCustomDate: true,
      includeTimezone: true, // By default, local timezone is applied to the datetime value
      filterValue: {
        value: [null, null],
        unit: null
      }
    };
    const options = assign({}, this.get('defaults'), this.get('filterOptions'));
    const { filterValue: { value } } = options;
    this.set('hasCustomDate', value.compact().length === 2);
    this.set('options', options);
  }

  @computed(
    'filterValue.value',
    'timezone.selected.zoneId',
    'dateFormat.selected.format',
    'timeFormat.selected.format'
  )
  get customDateRangeStart() {
    const timestamp = this.filterValue?.value || [];
    const includeTimezone = this.get('options.includeTimezone');
    return includeTimezone ? this._toLocalTime(timestamp[0]) : this._withoutTimeZone(timestamp[0]);
  }

  @computed(
    'filterValue.value',
    'timezone.selected.zoneId',
    'dateFormat.selected.format',
    'timeFormat.selected.format'
  )
  get customDateRangeEnd() {
    const includeTimezone = this.get('options.includeTimezone');
    return includeTimezone ? this._toLocalTime(this.filterValue?.value[1]) : this._withoutTimeZone(this.filterValue?.value[1]);
  }

  _withoutTimeZone(timestamp) {
    if (typeOf(timestamp) === 'number') {
      return moment(timestamp).format(`${this._getDateFormat()} ${this._getTimeFormat()}`);
    } else {
      return null;
    }
  }

  _toLocalTime(timestamp) {
    if (typeOf(timestamp) === 'number') {
      return moment.tz(timestamp, this._getTimezone()).format(`${this._getDateFormat()} ${this._getTimeFormat()}`);
    } else {
      return null;
    }
  }

  _getTimezone() {
    return this.get('timezone.selected.zoneId') || this.get('timezone.options').findBy('zoneId', config.timezoneDefault).zoneId;
  }

  _getDateFormat() {
    return this.get('dateFormat.selected.format') || this.get('dateFormat.options').findBy('key', config.dateFormatDefault).format;
  }

  _getTimeFormat() {
    const timeFormat = this.get('timeFormat.selected.format') || this.get('timeFormat.options').findBy('key', config.timeFormatDefault).format;
    return timeFormat.replace('.SSS', '');
  }

  _toUTCTimestamp(date) {
    const includeTimezone = this.get('options.includeTimezone');
    const dateParts = [date.getFullYear(), date.getMonth(), date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds()];
    return includeTimezone ? moment.tz(dateParts, this._getTimezone()).valueOf() : moment(dateParts).valueOf();
  }

  _handleChange(value, unit) {
    const { name, operator = 'LESS_THAN' } = this.get('filterOptions');
    const onChange = this.get('onChange');
    if (onChange) {
      if (unit) {
        onChange({ name, operator, value, unit });
      } else {
        onChange({ name, operator: 'BETWEEN', value });
      }
    }
  }

  @action
  toggleCustomDate() {
    this.toggleProperty('hasCustomDate');
    if (!this.get('hasCustomDate')) {
      this._handleChange([], null); // clearing the custom date
    }
  }

  @action
  customStartDateChanged([date]) {
    const start = date ? this._toUTCTimestamp(date) : null;
    this.set('startTime', start);
    const end = this.get('endTime');
    this._handleChange([start, end]);
  }

  @action
  customEndDateChanged([date]) {
    const end = date ? this._toUTCTimestamp(date) + 999 : null;
    this.set('endTime', end);
    const start = this.get('startTime');
    this._handleChange([start, end]);
  }

  @action
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