import Ember from 'ember';
import d3 from 'd3';
import computed from 'ember-computed-decorators';

const {
  Component,
  inject: {
    service
  }
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
   * The chart data structure, to be passed down to child `rsa-chart` component.
   * @see component-lib/components/rsa-chart
   * @type { [[]] }
   * @private
   */
  chartData: undefined,

  xProp: 'value',
  yProp: 'count',

  timezone: service('timezone'),

  @computed('timezone.selected')
  xScaleFn: (zone) => (zone === 'UTC') ? d3.scaleUtc : d3.scaleTime,

  @computed('startTime', 'endTime')
  xDomain: (start, end) => (start && end) ? [start * 1000, end * 1000] : []
});
