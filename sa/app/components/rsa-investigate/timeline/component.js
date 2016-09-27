import Ember from 'ember';
import d3 from 'd3';
import computed from 'ember-computed-decorators';
import { computeExtent, multiDateFormat, multiDate24Format } from 'component-lib/utils/chart-utils';
import safeCallback from 'component-lib/utils/safe-callback';

const {
  Component,
  inject: {
    service
  },
  isArray
} = Ember;

export default Component.extend({
  classNames: 'rsa-investigate-timeline',
  classNameBindings: ['status'],
  chartMargin: {
    top: 10,    /* avoid zeroes to prevent shaving edges off peaks */
    bottom: 30, /* big enough for some text */
    left: 35,
    right: 20
  },

  /**
   * The status of the request to fetch the timeline `data`.
   * Either undefined (promise hasn't been executed yet), 'wait' (promise is in progress), 'resolved' or 'rejected'.
   * @see protected/investigate/state/event-timeline
   * @type {string}
   * @public
   */
  status: undefined,

  /**
   * An array of data to be plotted. The array can either be 1-D or 2-D. If 1-D, then it is assumed to be a single
   * series of data. If 2-D, it is assumed to be a multi-series set of data.
   * @type {[]}
   * @public
   */
  data: undefined,

  /**
   * Configurable callback to be invoked when there is an error and user clicks Retry button.
   * @type {function}
   * @public
   */
  retryAction: undefined,

  // Resolves to true if `status` is either 'wait' or 'rejected'. Used to toggle status indicator.
  @computed('status')
  isStatusWaitOrRejected(status = '') {
    return !!status.match(/wait|rejected/);
  },

  /**
   * The chart data structure, to be passed down to child `rsa-chart` component.
   * This property computes the 2-D structure from a given 1-D data array.
   * @see component-lib/components/rsa-chart
   * @type { [[]] }
   * @private
   */
  @computed('data')
  chartData(data = []) {
    if (isArray(data)) {
      return isArray(data[0]) ? data : [ data ];
    }
    return [[]];
  },

  xProp: 'value',
  yProp: 'count',

  timezone: service(),
  timeFormat: service(),

  @computed('timezone.selected')
  xScaleFn: (zone) => (zone === 'UTC') ? d3.scaleUtc : d3.scaleTime,

  @computed('timeFormat.selected.key')
  tickformat: (format) => (format === '24hr') ? multiDate24Format : multiDateFormat,

  @computed('startTime', 'endTime', 'chartData', 'xProp')
  xDomain: (start, end, chartData, xProp) => {
    let domain = [];
    if (!!start && !!end) {
      domain = [start * 1000, end * 1000];
    } else if (!!start || !!end) {
      domain = computeExtent(chartData, (d) => d[xProp]);
    }
    return domain;
  },

  actions: {
    safeCallback
  }
});
