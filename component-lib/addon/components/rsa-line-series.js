import Ember from 'ember';
import d3 from 'd3';
import computed from 'ember-computed-decorators';

const { Component, run } = Ember;
const createLine = (xAccessorFn, yAccessorFn, curve) => {
  return d3.line().x(xAccessorFn).y(yAccessorFn).curve(curve);
};

export default Component.extend({
  tagName: 'path',
  classNameBindings: [':rsa-line-series', 'clazzName'],
  data: [[]],
  dataIndex: 0,
  xScale: d3.scaleTime,
  yScale: d3.scaleLinear,
  xProp: 'x',
  yProp: 'y',
  interpolator: d3.curveLinear,

  @computed('dataIndex')
  clazzName: (index) => `series-${index}`,

  @computed('xScale', 'xProp')
  xAccessor: (scale, prop) => (d) => scale(d[prop]),

  @computed('yScale', 'yProp')
  yAccessor: (scale, prop) => (d) => scale(d[prop]),

  @computed('xAccessor', 'yAccessor', 'interpolator')
  lineFn: (...args) => createLine(...args),

  didInsertElement() {
    this._super(...arguments);
    const index = this.get('dataIndex');
    const datum = this.get('data').objectAt(index);
    const lineFn = this.get('lineFn');
    run.scheduleOnce('afterRender', this, this.draw, datum, lineFn);
  },

  didUpdateAttrs() {
    this._super(...arguments);
    const index = this.get('dataIndex');
    const datum = this.get('data')[index];
    const lineFn = this.get('lineFn');
    this.draw(datum, lineFn);
  },

  draw(datum, lineFn) {
    d3.select(this.element)
      .datum(datum)
      .transition().duration(750)
      .attr('d', lineFn);
  }
});