import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import TIME_RANGES from 'investigate-shared/constants/time-ranges';
import layout from './template';

const TimeSelector = Component.extend({

  layout,

  classNames: ['rsa-investigate-query-container__time-selector'],

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

  onTimeSelection: null,

  startTime: null,

  endTime: null,

  @computed()
  panelId() {
    return `queryTimerangeTooltip-${this.get('elementId')}`;
  }

});

export default TimeSelector;
