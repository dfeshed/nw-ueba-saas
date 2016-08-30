import Ember from 'ember';

const { Component } = Ember;

export default Component.extend({
  classNames: 'rsa-investigate-timeline',
  chartMargin: {
    top: 9,     /* avoid zeroes to prevent shaving edges off peaks */
    bottom: 30, /* big enough for some text */
    left: 9,
    right: 9
  },

  /**
   * The chart data structure, to be passed down to child `rsa-chart` component.
   * @see component-lib/components/rsa-chart
   * @type { [[]] }
   * @private
   */
  chartData: undefined,

  xProp: 'value',
  yProp: 'count'
});
