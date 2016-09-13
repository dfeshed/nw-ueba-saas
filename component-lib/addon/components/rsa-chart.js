import Ember from 'ember';
import d3 from 'd3';
import computed from 'ember-computed-decorators';
import layout from '../templates/components/rsa-chart';
import { calcGraphWidth, calcGraphHeight, computeExtent, createScale } from '../utils/chart-utils';
/* global addResizeListener */
/* global removeResizeListener */

const { Component, run } = Ember;

// The size of the component is actually completely dictated by CSS. However,
// if we specify default zeroes, the initial DOM rendering will (briefly) look
// crunched. Specifying non-zero default sizes yields a better initial UX.
const DEFAULT_WIDTH = 600;
const DEFAULT_HEIGHT = 150;

export default Component.extend({
  classNames: ['rsa-chart'],
  layout,

  chartWidth: DEFAULT_WIDTH,
  chartHeight: DEFAULT_HEIGHT,
  data: null,
  hoverData: null,
  interactive: true,
  // Adjust margins so that the axes fit
  margin: { top: 5, bottom: 30, left: 30, right: 0 },
  xAxisStartsAtZero: false,
  xProp: 'x',
  xScaleFn: d3.scaleTime,
  yProp: 'y',
  yAxisStartsAtZero: true,
  yScaleFn: d3.scaleLinear,

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

  @computed('data', 'xProp', 'xAxisStartsAtZero')
  xDomain: (data, xProp, zeroed) => computeExtent(data, (d) => d[xProp], zeroed),

  @computed('data', 'yProp', 'yAxisStartsAtZero')
  yDomain: (data, yProp, zeroed) => computeExtent(data, (d) => d[yProp], zeroed),

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
    this.set('svgGroup', d3.select('svg g'));
    run.scheduleOnce('afterRender', () => {
      this._resizeListener();
      if (this.get('interactive')) {
        this.setupInteractivity();
      }
    });
  },

  willDestroyElement() {
    this._super(...arguments);
    if (this._resizeListener) {
      removeResizeListener(this.element, this._resizeListener);
      this._resizeListener = null;
    }
  },

  setupInteractivity() {
    const self = this;
    // We use traditional event handlers instead of fat arrow functions so that
    // `this` is not overwritted. We want this so that when we get the x
    // coordinate of the mouse, we can just use `d3.mouse(this)`. Otherwise, we'd
    // have to save the `.rsa-chart-background` selection off and refer to it
    // each time the `mousemove` handler is invoked.
    d3.select('.rsa-chart-background')
      .on('mouseout', function() {
        self.set('hoverData', null);
      })
      .on('mousemove', function() {
        const { xProp, xScale } = self.getProperties('xProp', 'xScale');
        const data = self.get('data').objectAt(0);
        if (data && data.length > 0) {
          const x0 = xScale.invert(d3.mouse(this)[0]);
          const bisectLeft = d3.bisector((d) => d[xProp]).left;
          const i = bisectLeft(data, x0, 1);
          const d0 = data[i - 1];
          const d1 = data[i];
          if (d0 && d1) {
            const d = x0 - d0[xProp] > d1[xProp] - x0 ? d1 : d0;
            self.set('hoverData', d);
          }
        }
      });
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
