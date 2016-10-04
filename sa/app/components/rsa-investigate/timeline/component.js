import Ember from 'ember';
import d3 from 'd3';
import computed, { alias } from 'ember-computed-decorators';
import { computeExtent, multiDateFormat, multiDate24Format } from 'component-lib/utils/chart-utils';
import safeCallback from 'component-lib/utils/safe-callback';

const {
  Component,
  inject: {
    service
  },
  isArray,
  isEmpty,
  get
} = Ember;

export default Component.extend({
  classNames: 'rsa-investigate-timeline',
  classNameBindings: ['status', 'isExpanded'],

  /**
   * An array of data to be plotted. The array can either be 1-D or 2-D. If 1-D, then it is assumed to be a single
   * series of data. If 2-D, it is assumed to be a multi-series set of data.
   * @type {[]}
   * @public
   */
  data: undefined,
  dateFormat: service(),
  detailChartMargin: { top: 5, bottom: 30, left: 35, right: 10 },
  hoverIndex: null,
  isExpanded: false,
  masterChartMargin: { top: 4, bottom: 0, left: 0, right: 0 },
  /**
   * Configurable callback to be invoked when there is an error and user clicks Retry button.
   * @type {function}
   * @public
   */
  retryAction: undefined,
  /**
   * The status of the request to fetch the timeline `data`.
   * Either undefined (promise hasn't been executed yet), 'wait' (promise is in progress), 'resolved' or 'rejected'.
   * @see protected/investigate/state/event-timeline
   * @type {string}
   * @public
   */
  status: undefined,
  timeFormat: service(),
  timezone: service(),
  xProp: 'value',
  yProp: 'count',

  @alias('dateFormat.selected.key')
  dateFormatString: null,

  @computed('dateFormatString')
  dateTimeFormatString: (dateFormat) => `${dateFormat} HH:mm`,

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

  @computed('endTime')
  endDate: (date) => date * 1000,

  @computed('chartData', 'hoverIndex', 'xProp', 'yProp')
  hoverValue: (data, index, xProp, yProp) => {
    const countFormat = d3.format(',d');
    const points = data.map((datum) => datum.objectAt(index)).compact();
    const date = get(points, `firstObject.${xProp}`);
    const counts = points.map((point) => countFormat(point[yProp]));
    return date ? { date, counts } : null;
  },

  @computed('data', 'status')
  isDataEmpty(data, status) {
    return isEmpty(data) && status === 'resolved';
  },

  // Resolves to true if either: (a) status == 'wait', or (b) status == 'rejected' or (c) isDataEmpty is truthy.
  // Used to toggle status indicator.
  @computed('status', 'isDataEmpty')
  shouldShowStatus: (status = '', isEmpty) => !!status.match(/wait|rejected/) || isEmpty,

  @computed('startTime')
  startDate: (date) => date * 1000,

  @computed('timeFormat.selected.key')
  tickformat: (format) => (format === '24hr') ? multiDate24Format : multiDateFormat,

  @computed('isExpanded')
  toggleIcon: (isExpanded) => isExpanded ? 'shrink-horizontal-2' : 'expand-vertical-4',

  @computed('startDate', 'endDate', 'chartData', 'xProp')
  xDomain: (start, end, chartData, xProp) => {
    let domain = [];
    if (!!start && !!end) {
      domain = [start, end];
    } else if (!!start || !!end) {
      domain = computeExtent(chartData, (d) => d[xProp]);
    }
    return domain;
  },

  @computed('timezone.selected')
  xScaleFn: (zone) => (zone === 'UTC') ? d3.scaleUtc : d3.scaleTime,

  actions: {
    safeCallback,

    toggleContent() {
      this.toggleProperty('isExpanded');
    }
  }
});
