import Ember from 'ember';
import d3 from 'd3';
import computed from 'ember-computed-decorators';

const { Component, run } = Ember;
const createArea = (xAccessorFn, yAccessorFn, height, curve) => {
  return d3.area().x(xAccessorFn).y0(height).y1(yAccessorFn).curve(curve);
};

export default Component.extend({
  tagName: 'path',
  classNameBindings: [':rsa-area-series', 'clazzName'],
  data: null,
  dataIndex: 0,
  xScale: null,
  yScale: null,
  xProp: 'x',
  yProp: 'y',
  height: 0,
  interpolator: d3.curveLinear,

  @computed('dataIndex')
  clazzName: (index) => `series-${index}`,

  @computed('xScale', 'xProp')
  xAccessor: (scaleFn, prop) => (d) => scaleFn(d[prop]),

  @computed('yScale', 'yProp')
  yAccessor: (scaleFn, prop) => (d) => scaleFn(d[prop]),

  @computed('xAccessor', 'yAccessor', 'height', 'interpolator')
  pathFn: (...args) => createArea(...args),

  didInsertElement() {
    this._super(...arguments);
    const index = this.get('dataIndex');
    const datum = this.get('data')[index];
    const pathFn = this.get('pathFn');
    run.scheduleOnce('afterRender', this, this.draw, datum, pathFn);
  },

  didUpdateAttrs() {
    this._super(...arguments);
    const index = this.get('dataIndex');
    const datum = this.get('data')[index];
    const pathFn = this.get('pathFn');
    this.draw(datum, pathFn);
  },

  draw(datum, pathFn) {
    d3.select(this.element)
      .datum(datum)
      .transition().duration(750)
      .attr('d', pathFn);
  }
});