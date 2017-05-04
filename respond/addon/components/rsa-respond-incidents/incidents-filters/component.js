import Component from 'ember-component';
import service from 'ember-service/inject';
import { isPresent, typeOf } from 'ember-utils';
import connect from 'ember-redux/components/connect';
import * as DataActions from 'respond/actions/data-creators';
import { priorityOptions, statusOptions } from 'respond/selectors/dictionaries';
import computed from 'ember-computed-decorators';
import { SINCE_WHEN_TYPES } from 'respond/utils/since-when-types';
import moment from 'moment';
import config from 'ember-get-config';

const stateToComputed = (state) => {
  const {
    respond: {
      dictionaries: { categoryTags },
      incidents: { incidentsFilters }
    }
  } = state;

  return {
    priorityFilters: incidentsFilters.priority,
    statusFilters: incidentsFilters.status,
    assigneeFilters: incidentsFilters['assignee.id'],
    priorityTypes: priorityOptions(state),
    statusTypes: statusOptions(state),
    categoryFilters: incidentsFilters['categories.parent'],
    categoryTags,
    users: state.respond.users.users,
    timeframeFilter: incidentsFilters.created,
    hasCustomDate: state.respond.incidents.hasCustomDateRestriction
  };
};

const dispatchToActions = (dispatch) => {
  return {
    updateFilter(change) {
      dispatch(DataActions.updateIncidentFilters(change));
    },
    reset() {
      dispatch(DataActions.resetIncidentFilters());
    },
    toggleCustomDate() {
      dispatch(DataActions.toggleCustomDateRestriction());
    }
  };
};

/**
 * Toolbar that provides search filtering and sorting functionality for searching / exploring incidents.
 * @class IncidentsToolbar
 * @public
 */
const IncidentsFilters = Component.extend({
  classNames: 'incidents-filters',

  i18n: service(),

  timezone: service(),

  dateFormat: service(),

  timeFormat: service(),

  timeframes: SINCE_WHEN_TYPES,

  /**
   * The start date (as unix timestamp) on a custom date range.
   * Note: Dates/Times are assumed to be in UTC, and are therefore converted to local date/time
   * via the timezone service.
   * @property customDateRangeStart
   * @public
   * @param timestamp (UTC timestamp)
   * @returns {Number}
   */
  @computed('timeframeFilter.start', 'timezone.selected.zoneId', 'dateFormat.selected.format', 'timeFormat.selected.format')
  customDateRangeStart(timestamp) {
    return this._toLocalTime(timestamp);
  },

  /**
   * The end date on a custom date range.
   * Note: Dates/Times are assumed to be in UTC, and are therefore converted to local date/time
   * via the timezone service.
   * @property customDateRangeEnd
   * @public
   * @param timestamp (UTC timestamp)
   * @returns {Number}
   */
  @computed('timeframeFilter.end', 'timezone.selected.zoneId', 'dateFormat.selected.format', 'timeFormat.selected.format')
  customDateRangeEnd(timestamp) {
    return this._toLocalTime(timestamp);
  },

  /**
   * An error message that is displayed on an invalid date field. Cf hasCustomDateError property
   * @property customDateErrorMessage
   * @public
   */
  customDateErrorMessage: null,

  /**
   * Returns true when the custom date range has an error
   * @property hasCustomDateError
   * @public
   * @param start
   * @param end
   * @returns {boolean}
   */
  @computed('timeframeFilter.start', 'timeframeFilter.end')
  hasCustomDateError(start, end) {
    const i18n = this.get('i18n');
    let errorMessage = null;
    let hasError = false;

    if (isPresent(start) && isPresent(end) && start >= end) {
      hasError = true;
      errorMessage = i18n.t('respond.incidents.filters.customDateErrorStartAfterEnd');
    }
    this.set('customDateErrorMessage', errorMessage);
    return hasError;
  },

  /**
   * The time frame option from the common-ranges dropdown that is to be selected
   * @property selectedTimeframe
   * @public
   * @param timeframe
   * @param timeframes
   * @returns {*}
   */
  @computed('timeframeFilter', 'timeframes')
  selectedTimeframe(timeframe, timeframes) {
    return timeframes.findBy('name', timeframe.name);
  },

  /**
   * The user objects that have been selected via the assignee picker
   * @property selectedAssignees
   * @public
   * @param users
   * @param assigneeFilters
   * @returns {Array}
   */
  @computed('users', 'assigneeFilters')
  selectedAssignees(users, assigneeFilters = []) {
    return users.map((user) => {
      return assigneeFilters.includes(user.id) ? user : null;
    }).compact();
  },

  @computed('categoryTags', 'categoryFilters')
  selectedCategories(categories, categoryFilters = []) {
    return categories.map((item) => (categoryFilters.includes(item) ? item : null)).compact();
  },

  /**
   * Converts a unix timestamp to a formatted date string using the user's selected timezone and date/time format
   * @param timestamp
   * @returns {*}
   * @private
   */
  _toLocalTime(timestamp) {
    if (typeOf(timestamp) === 'number') {
      return moment.tz(timestamp, this._getTimezone()).format(`${this._getDateFormat()} ${this._getTimeFormat()}`);
    } else {
      return null;
    }
  },

  /**
   * Returns the timezone id for the user (e.g., America/Los_Angeles)
   * @private
   */
  _getTimezone() {
    return this.get('timezone.selected.zoneId') || this.get('timezone.options').findBy('zoneId', config.timezoneDefault).zoneId;
  },

  /**
   * Returns the user's date format (e.g., MM/DD/YYYY)
   * @private
   */
  _getDateFormat() {
    return this.get('dateFormat.selected.format') || this.get('dateFormat.options').findBy('key', config.dateFormatDefault).format;
  },

  /**
   * Returns the user's time format (e.g., HH:mm)
   * @private
   */
  _getTimeFormat() {
    return this.get('timeFormat.selected.format') || this.get('timeFormat.options').findBy('key', config.timeFormatDefault).format;
  },

  /**
   * Converts a JS Date to a specific timezone representation and returns that as a unix timestamp. The incoming date
   * from a date-picker selection is initially represented in the browser's/computer's local time, but the user may have
   * selected a different timezone (e.g., UTC or other) to work in beyond his/her local time. This ensures that the
   * computer generated date/time is pre-converted into the desired timezone before being returned as a unix timestamp.
   * @param date
   * @returns {boolean|*|Number|XMLList|Array|Namespace}
   * @private
   */
  _toUTCTimestamp(date) {
    const dateParts = [date.getFullYear(), date.getMonth(), date.getDate(), date.getHours(), date.getMinutes()];
    return moment.tz(dateParts, this._getTimezone()).valueOf();
  },

  actions: {
    toggleStatusFilter(status) {
      const statusFilters = this.get('statusFilters');
      this.send('updateFilter', {
        status: statusFilters.includes(status) ? statusFilters.without(status) : [...statusFilters, status]
      });
    },

    togglePriorityFilter(priority) {
      const priorityFilters = this.get('priorityFilters');
      this.send('updateFilter', {
        priority: priorityFilters.includes(priority) ? priorityFilters.without(priority) : [...priorityFilters, priority]
      });
    },

    assigneeChanged(selections) {
      this.send('updateFilter', {
        'assignee.id': selections.map((selection) => {
          return selection.id;
        })
      });
    },

    onChangeTimeframe(created) {
      this.send('updateFilter', { created });
    },

    categoryChanged(selections) {
      this.send('updateFilter', {
        'categories.parent': selections
      });
    },

    customStartDateChanged(date) {
      const start = date ? this._toUTCTimestamp(date) : null;
      const end = this.get('timeframeFilter.end');
      this.send('updateFilter', {
        created: { start, end }
      });
    },

    customEndDateChanged(date) {
      const end = date ? this._toUTCTimestamp(date) : null;
      const start = this.get('timeframeFilter.start');
      this.send('updateFilter', {
        created: { start, end }
      });
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(IncidentsFilters);