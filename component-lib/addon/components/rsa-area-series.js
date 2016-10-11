import Ember from 'ember';
import IsChartSeries from '../mixins/is-chart-series';
import computed from 'ember-computed-decorators';
import { area } from 'd3-shape';

const {
  Component
} = Ember;
const createArea = (xAccessorFn, yAccessorFn, height, curve) => {
  return area().x(xAccessorFn).y0(height).y1(yAccessorFn).curve(curve);
};

export default Component.extend(IsChartSeries, {
  classNameBindings: [':rsa-area-series', 'clazzName'],
  tagName: 'path',
  height: 0,

  @computed('xAccessor', 'yAccessor', 'height', 'interpolator')
  pathFn: (...args) => createArea(...args)
});