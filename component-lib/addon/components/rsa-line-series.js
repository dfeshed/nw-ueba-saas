import Ember from 'ember';
import IsChartSeries from '../mixins/is-chart-series';
import d3 from 'd3';
import computed from 'ember-computed-decorators';

const {
  Component
} = Ember;
const createLine = (xAccessorFn, yAccessorFn, curve) => {
  return d3.line().x(xAccessorFn).y(yAccessorFn).curve(curve);
};

export default Component.extend(IsChartSeries, {
  classNameBindings: [':rsa-line-series', 'clazzName'],
  tagName: 'path',

  @computed('xAccessor', 'yAccessor', 'interpolator')
  pathFn: (...args) => createLine(...args)
});