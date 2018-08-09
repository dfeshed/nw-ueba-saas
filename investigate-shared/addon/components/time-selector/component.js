import Component from '@ember/component';
import moment from 'moment';
import computed from 'ember-computed-decorators';
import TIME_RANGES from 'investigate-shared/constants/time-ranges';
import layout from './template';

const TimeSelector = Component.extend({

  layout,

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

  @computed()
  panelId() {
    return `queryTimerangeTooltip-${this.get('elementId')}`;
  },

  // default to last 23 hrs 59 mins 59 seconds if startTime and endTime are not set
  @computed('startTime')
  _startTimeMilli(startTime) {
    return !startTime ? moment().subtract(1, 'day').add(1, 'minutes').startOf('minute').valueOf() : startTime * 1000;
  },

  @computed('endTime')
  _endTimeMilli(endTime) {
    return !endTime ? moment().endOf('minute').valueOf() : endTime * 1000;
  },

  actions: {
    // called when the start, end times is changed manually in the component.
    setCustomTimeRange(start, end) {
      this.get('onIndividualTimeUnitChange')(start, end);
    },
    // called when the start, end times is changed manually in the component.
    // no need to capture the actual error object, as the tooltip
    // provides feedback on what the error is.
    timeRangeError() {
      this.get('onTimeRangeError')();
    },
    // called from the dropdown that has the options "Last 5 mins, Last 1 hr, All Data etc".
    onEntireTimeRangeSelection(range) {
      this.get('onEntireTimeRangeSelection')(range);
    }
  }
});

export default TimeSelector;