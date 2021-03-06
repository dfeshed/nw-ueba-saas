import Component from '@ember/component';
import { inject as service } from '@ember/service';
import { isArray } from '@ember/array';
import { get } from '@ember/object';
import { connect } from 'ember-redux';
import computed, { alias } from 'ember-computed-decorators';
import { computeExtent, dateFormatter } from 'component-lib/utils/chart-utils';
import { format } from 'd3-format';
import { scaleTime, scaleUtc } from 'd3-scale';
import { isDataEmpty, shouldShowStatus } from 'investigate-events/reducers/investigate/data-selectors';

const stateToComputed = (state) => ({
  data: state.investigate.eventTimeline.data,
  status: state.investigate.eventTimeline.status,
  startTime: state.investigate.queryNode.startTime,
  endTime: state.investigate.queryNode.endTime,
  queryNode: state.investigate.queryNode,
  isDataEmpty: isDataEmpty(state),
  shouldShowStatus: shouldShowStatus(state)
});

const TimelineComponent = Component.extend({
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

  init() {
    this._super(arguments);
    this.detailChartMargin = this.detailChartMargin || { top: 5, bottom: 30, left: 35, right: 10 };
    this.masterChartMargin = this.masterChartMargin || { top: 8, bottom: 0, left: 5, right: 5 };
  },

  hoverIndex: null,
  isExpanded: false,

  /**
   * The status of the request to fetch the timeline `data`.
   * Either undefined (promise hasn't been executed yet), 'wait' (promise is in progress), 'resolved' or 'rejected'.
   * @see state/event-timeline
   * @type {string}
   * @public
   */
  timeFormat: service(),
  timezone: service(),
  xProp: 'value',
  yProp: 'count',

  @alias('dateFormat.selected.format')
  dateFormatString: null,

  @alias('timeFormat.selected.format')
  timeFormatString: null,

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
    const countFormat = format(',d');
    const points = data.map((datum) => datum.objectAt(index)).compact();
    const date = get(points, `firstObject.${xProp}`);
    const counts = points.map((point) => countFormat(point[yProp]));
    return date ? { date, counts } : null;
  },

  @computed('startTime')
  startDate: (date) => date * 1000,

  @computed('timeFormat.selected.key', 'timezone.selected.zoneId')
  tickformat: (format, zone) => dateFormatter((format === 'HR24'), zone),

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

  @computed('timezone.selected.zoneId')
  xScaleFn: (zone) => (zone === 'UTC') ? scaleUtc : scaleTime,

  actions: {
    retryDataFetch() {
      // TODO - not implemented yet
      // this.get('eventTimelineGet')(queryNode, true);
    },
    toggleContent() {
      this.toggleProperty('isExpanded');
    }
  }
});

export default connect(stateToComputed)(TimelineComponent);
