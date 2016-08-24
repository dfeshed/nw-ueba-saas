import Ember from 'ember';
import d3 from 'd3';
import computed from 'ember-computed-decorators';
import layout from '../templates/components/rsa-chart';
/* global addResizeListener */
/* global removeResizeListener */

const { Component, run } = Ember;
const min = (data, accessorFn) => d3.min(data.map((d) => d3.min(d, accessorFn)));
const max = (data, accessorFn) => d3.max(data.map((d) => d3.max(d, accessorFn)));
const computeExtent = (data, accessorFn) => [min(data, accessorFn), max(data, accessorFn)];
const createScale = (scaleFn, domain, range) => scaleFn().domain(domain).range(range).clamp(true);
const calcGraphWidth = (width, marginLeft, marginRight) => width - marginLeft - marginRight;
const calcGraphHeight = (height, marginTop, marginBottom) => height - marginTop - marginBottom;

// The size of the component is actually completely dictated by CSS. However,
// if we specify default zeroes, the initial DOM rendering will (briefly) look
// crunched. Specifying non-zero default sizes yields a better initial UX.
const DEFAULT_WIDTH = 600;
const DEFAULT_HEIGHT = 150;

export default Component.extend({
  layout,
  classNames: ['rsa-chart'],
  data: null,
  chartWidth: DEFAULT_WIDTH,
  chartHeight: DEFAULT_HEIGHT,
  xProp: 'x',
  yProp: 'y',
  xScaleFn: d3.scaleTime,
  yScaleFn: d3.scaleLinear,

  // Adjust margins so that the axes fit
  margin: { top: 5, bottom: 30, left: 30, right: 0 },

  @computed('chartWidth', 'margin')
  graphWidth(width, { left, right }) {
    const calculatedWidth = calcGraphWidth(width, left, right);
    return Math.max(calculatedWidth, 0);
  },

  @computed('chartHeight', 'margin')
  graphHeight(height, { top, bottom }) {
    const calculatedHeight = calcGraphHeight(height, top, bottom);
    return Math.max(calculatedHeight, 0);
  },

  @computed('data', 'xProp')
  xDomain: (data, xProp) => computeExtent(data, (d) => d[xProp]),

  @computed('data', 'yProp')
  yDomain: (data, yProp) => computeExtent(data, (d) => d[yProp]),

  @computed('graphWidth')
  xRange: (graphWidth) => [0, graphWidth],

  @computed('graphHeight')
  yRange: (graphHeight) => [graphHeight, 0],

  @computed('xScaleFn', 'xDomain', 'xRange')
  xScale: (...args) => createScale(...args),

  @computed('yScaleFn', 'yDomain', 'yRange')
  yScale: (...args) => createScale(...args),

  didInsertElement() {
    this._super(...arguments);
    this._resizeListener = this.elementDidResize.bind(this);
    addResizeListener(this.element, this._resizeListener);
    run.scheduleOnce('afterRender', this._resizeListener);
  },

  willDestroyElement() {
    this._super(...arguments);
    if (this._resizeListener) {
      removeResizeListener(this.element, this._resizeListener);
      this._resizeListener = null;
    }
  },

  elementDidResize() {
    run.throttle(() => {
      const w = this.get('element.clientWidth') || 0;
      const h = this.get('element.clientHeight') || 0;
      this.setProperties({
        chartWidth: w,
        chartHeight: h
      });
    }, 250);
  }
});
