import Ember from 'ember';
import IsChartSeries from '../mixins/is-chart-series';
import computed from 'ember-computed-decorators';
import { line } from 'd3-shape';

const {
  Component
} = Ember;
const createLine = (xAccessorFn, yAccessorFn, curve) => {
  return line().x(xAccessorFn).y(yAccessorFn).curve(curve);
};

export default Component.extend(IsChartSeries, {
  classNameBindings: [':rsa-line-series', 'clazzName'],
  tagName: 'path',

  @computed('xAccessor', 'yAccessor', 'interpolator')
  pathFn: (...args) => createLine(...args)
});