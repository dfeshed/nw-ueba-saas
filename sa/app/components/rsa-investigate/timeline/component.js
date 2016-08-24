import Ember from 'ember';

const { Component } = Ember;

export default Component.extend({
  classNames: 'rsa-investigate-timeline',
  chartMargin: {
    top: 0,
    bottom: 30, /* big enough for some text */
    left: 9,
    right: 9
  },
  chartData: [[]],
  xProp: 'value',
  yProp: 'count'
});
