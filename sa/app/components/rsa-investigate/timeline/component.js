import Ember from 'ember';
import computed from 'ember-computed-decorators';

const { Component } = Ember;

export default Component.extend({
  classNames: 'rsa-investigate-timeline',
  chartMargin: {
    top: 0,
    bottom: 30, /* big enough for some text */
    left: 9,
    right: 9
  },

  /**
   * The timeline data in the structure fetched from server.
   * @type {object[]}
   * @public
   */
  data: undefined,

  /**
   * Wraps `data` into a structure understood by the `rsa-chart` component.
   * Whereas `data` is expected to be a 1 dimensional array that represents a single series of data, `rsa-chart`
   * supports displaying multiple series and therefore expects a 2-dimensional array.
   * @see component-lib/components/rsa-chart
   * @type { [[]] }
   * @private
   */
  @computed('data')
  chartData: (data) => data ? [ data ] : [[]],

  xProp: 'value',
  yProp: 'count'
});
