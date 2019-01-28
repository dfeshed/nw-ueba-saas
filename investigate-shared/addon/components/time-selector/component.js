import Component from '@ember/component';
import moment from 'moment';
import computed from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import TIME_RANGES from 'investigate-shared/constants/time-ranges';
import layout from './template';

let endTimeMilli = 0;
let startTimeMilli = 0;

const TimeSelector = Component.extend({

  layout,

  timezone: service(),

  timeFormat: service(),

  dateFormat: service(),

  classNames: ['rsa-investigate-query-container__time-selector'],
  // using the 'timeRangeInvalid' to toggle the red border on the time ranges dropdown. This flag is from the state
  // and is passed down by the parent component
  classNameBindings: ['timeRangeInvalid'],

  // This temporarily disables the tooltip until we can figure out how to
  // calculate a time range like "Last 2 Days" from the startTime/endTime
  // specified in the URL.
  selectedTimeRangeName: '',

  /**
   * Array of available time ranges for user to pick from.
   * @type {object[]}
   * @private
   */
  timeRanges: TIME_RANGES.RANGES,

  onEntireTimeRangeSelection: null,

  startTime: null,

  endTime: null,

  /**
   * Used to save previous computed startTime.
   * @private
   */
  _previousStartTimeMilli: null,

  /**
   * Used to save previous computed endTime.
   * @private
   */
  _previousEndTimeMilli: null,

  @computed()
  panelId() {
    return `queryTimerangeTooltip-${this.get('elementId')}`;
  },

  // The setting of time values can happen external to this component such that this
  // component is handed the dates to display.(Ex: previous dates in local storage, or
  // "Last 15 minutes" dropdown) Additionally, if internal to this component an invalid
  // date is chosen, that date is not sent out, so any external state including that date
  // is not updated. Only the fact that the date is now invalid is sent out. This means
  // the date component can be invalid, but the previous valid dates are still being
  // stored.
  //
  // What's this mean? If internal to this component you create invalid dates, (like
  // setting an invalid year) and then external to this component you set the dates
  // back to the previous valid time (like via the dropdown), then external state never
  // changes. And because setting the dates back to the valid date was external to
  // this component, this component doesn't know it happened and the component
  // is stuck in its previous invalid state.
  //
  // So we need to force the date to update even though in state it really didn't.
  // If the date is the same, but the validity has changed, that means we are in this
  // odd case where we've gone from valid, to invalid, to exact same valid. So we
  // tweak the date very slightly to force child components to re-render the date
  // over the previous invalid date.
  //
  // For additional info,refer to the test titled 'Make the timerange invalid,
  // then select from custom dropdown - error should go away' in investigate-events
  // Also, check out JIRA - ASOC-64474

  @computed('startTime', 'timeRangeInvalid')
  _startTimeMilli(startTime, timeRangeInvalid) {

    const newStartTime = !startTime ? moment().subtract(1, 'day').add(1, 'minutes').startOf('minute').valueOf() : startTime * 1000;
    // default to last 23 hrs 59 mins 59 seconds if startTime is not set

    if (newStartTime === this.get('_previousStartTimeMilli')) {
      // Here we know that the actual value of the date hasn't changed in state because it is the same
      // So the validity had to have changed or otherwise this computed would not have computed

      if (!timeRangeInvalid) {
        // Here we know that the date is the same, but the invalidity went from
        // true to false
        // This is to add a digit at the end of the startTime, so that we forcefully
        // trigger its child component's CP. Because milli seconds will be trimmed off later,
        // we are not worried about actually changing the time.
        startTimeMilli === 0 ? startTimeMilli = 1 : startTimeMilli = 0;
      }

    }
    this.set('_previousStartTimeMilli', newStartTime);
    return newStartTime + (startTimeMilli);
  },

  @computed('endTime', 'timeRangeInvalid')
  _endTimeMilli(endTime, timeRangeInvalid) {

    const newEndTime = !endTime ? moment().endOf('minute').valueOf() : endTime * 1000;
    // default to last 23 hrs 59 mins 59 seconds if endTime is not set

    if (newEndTime === this.get('_previousEndTimeMilli')) {
      // Here we know that the actual value of the date hasn't changed in state because it is the same
      // So the validity had to have changed or otherwise this computed would not have computed

      if (!timeRangeInvalid) {
        // Here we know that the date is the same, but the invalidity went from
        // true to false
        // This is to add a digit at the end of the endTime, so that we forcefully
        // trigger its child component's CP. Because milli seconds will be trimmed off later,
        // we are not worried about actually changing the time.
        endTimeMilli === 0 ? endTimeMilli = 1 : endTimeMilli = 0;
      }

    }
    this.set('_previousEndTimeMilli', newEndTime);
    return newEndTime + (endTimeMilli);
  },

  @computed('timeFormat.selected.key')
  use12HourClock(timeFormat) {
    // timeFormat can be either HR12 or HR24
    return timeFormat === 'HR12';
  },

  actions: {
    // called when the start, end times is changed manually in the component.
    setCustomTimeRange(start, end) {
      this.get('onIndividualTimeUnitChange')(start, end);
    },
    // called when the start, end times is changed manually in the component.
    // Although the tooltip provides feedback on what the error is,
    // need to capture the invalid start/end time that need to persist for the error validation to be justified
    // else the invalid time selection reverts to previous valid time selection, leaving the user confused
    // why the red border persists with the correct time.
    timeRangeError(error, start, end) {
      this.get('onTimeRangeError')(error, start, end);
    },
    // called from the dropdown that has the options "Last 5 mins, Last 1 hr, All Data etc".
    onEntireTimeRangeSelection(range) {
      this.get('onEntireTimeRangeSelection')(range);
    }
  }
});

export default TimeSelector;