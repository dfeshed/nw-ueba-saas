
import Component from 'ember-component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import TIME_RANGES from 'investigate-events/constants/time-ranges';
import { setQueryTimeRange } from 'investigate-events/actions/interaction-creators';

const dispatchToActions = { setQueryTimeRange };

const TimeCrumb = Component.extend({
  classNames: ['rsa-investigate-breadcrumb', 'js-test-investigate-events-time-breadcrumb'],

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

  @computed()
  panelId() {
    return `breadCrumbTimerangeTooltip-${this.get('elementId')}`;
  }

});

export default connect(undefined, dispatchToActions)(TimeCrumb);
