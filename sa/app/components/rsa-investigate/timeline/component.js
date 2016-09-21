import Ember from 'ember';
import d3 from 'd3';
import computed from 'ember-computed-decorators';
import { computeExtent, multiDateFormat, multiDate24Format } from 'component-lib/utils/chart-utils';

const {
  Component,
  inject: {
    service
  },
  isArray
} = Ember;

export default Component.extend({
  classNames: 'rsa-investigate-timeline',
  chartMargin: {
    top: 10,    /* avoid zeroes to prevent shaving edges off peaks */
    bottom: 30, /* big enough for some text */
    left: 35,
    right: 20
  },

  /**
   * An array of data to be plotted. The array can either be 1-D or 2-D. If 1-D, then it is assumed to be a single
   * series of data. If 2-D, it is assumed to be a multi-series set of data.
   * @type {[]}
   * @public
   */
  data: undefined,

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

  // The computed property should just be 'timeformat.selected.key'.
  // There appears to be a bug where if you set the time format preference,
  // `selected` is no longer an object, but the value '24hr' or '12hr'.
  // So, I'm taking this into account until that's fixed.
  @computed('timeFormat.selected')
  tickformat: (format) => (format === '24hr' || format.key === '24hr') ? multiDate24Format : multiDateFormat,

  @computed('startTime', 'endTime', 'chartData', 'xProp')
  xDomain: (start, end, chartData, xProp) => {
    let domain = [];
    if (!!start && !!end) {
      domain = [start * 1000, end * 1000];
    } else if (!!start || !!end) {
      domain = computeExtent(chartData, (d) => d[xProp]);
    }
    return domain;
  }
});
