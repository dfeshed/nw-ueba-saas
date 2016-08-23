import Ember from 'ember';
import d3 from 'd3';
import computed from 'ember-computed-decorators';
import layout from '../templates/components/rsa-chart';

const { Component, run } = Ember;
const min = (data, accessorFn) => d3.min(data.map((d) => d3.min(d, accessorFn)));
const max = (data, accessorFn) => d3.max(data.map((d) => d3.max(d, accessorFn)));
const computeExtent = (data, accessorFn) => [min(data, accessorFn), max(data, accessorFn)];
const createScale = (scaleFn, domain, range) => scaleFn().domain(domain).range(range).clamp(true);
const calcGraphWidth = (width, marginLeft, marginRight) => width - marginLeft - marginRight;
const calcGraphHeight = (height, marginTop, marginBottom) => height - marginTop - marginBottom;
const DEFAULT_WIDTH = 600;
const DEFAULT_HEIGHT = 150;

export default Component.extend({
  layout,
  classNames: ['rsa-chart'],
  width: 0,
  chartWidth: DEFAULT_WIDTH,
  height: 0,
  chartHeight: DEFAULT_HEIGHT,
  data: null,
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
    this.$(window).on(`resize.${this.elementId}`, run.bind(this, this.resize));
    run.scheduleOnce('afterRender', this, this.resize);
  },

  willDestroyElement() {
    this._super(...arguments);
    this.$(window).off(`resize.${this.elementId}`);
  },

  resize() {
    let w = DEFAULT_WIDTH;
    let h = DEFAULT_HEIGHT;
    const width = this.get('width');
    if (typeof(width) === 'string') {
      // assuming this is a percentage
      w = parseInt(width, 10) / 100 * this.element.clientWidth;
    } else if (width > 0) {
      // there was a fixed width set
      w = width;
    }
    const height = this.get('height');
    if (typeof(height) === 'string') {
      // assuming this is a percentage
      h = parseInt(height, 10) / 100 * this.element.clientHeight;
    } else if (height > 0) {
      // there was a fixed height set
      h = height;
    }
    this.set('chartWidth', w);
    this.set('chartHeight', h);
  }
});
