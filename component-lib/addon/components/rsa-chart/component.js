import Component from '@ember/component';
import { run } from '@ember/runloop';
import computed from 'ember-computed-decorators';
import layout from './template';
import { bisector } from 'd3-array';
import {
  calcGraphWidth,
  calcGraphHeight,
  computeExtent,
  createScale
} from '../../utils/chart-utils';
import { scaleTime, scaleLinear } from 'd3-scale';
import { select, mouse } from 'd3-selection';
/* global addResizeListener */
/* global removeResizeListener */

// The size of the component is actually completely dictated by CSS. However,
// if we specify default zeroes, the initial DOM rendering will (briefly) look
// crunched. Specifying non-zero default sizes yields a better initial UX.
const DEFAULT_WIDTH = 600;
const DEFAULT_HEIGHT = 150;

export default Component.extend({
  classNames: ['rsa-chart'],
  layout,

  duration: 0,
  chartWidth: DEFAULT_WIDTH,
  chartHeight: DEFAULT_HEIGHT,
  data: null,
  hoverIndex: null,
  interactive: true,
  isChartParent: true,
  // Adjust margins so that the axes fit
  margin: { top: 5, bottom: 30, left: 30, right: 0 },
  xProp: 'x',
  xScaleFn: scaleTime,
  yProp: 'y',
  yScaleFn: scaleLinear,

  // object describing x & y min & max domain extents, all entries optional
  // default to lower bound of y-axis fixed at 0, all others dynamic
  domainExtents: { y: { fixed: [0] } },

  @computed('elementId')
  clipId: (id) => `clip-${id}`,

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

  @computed('data', 'xProp', 'domainExtents')
  xDomain: (data, xProp, domainExtents) => computeExtent(data, (d) => d[xProp], domainExtents.x),

  @computed('data', 'yProp', 'domainExtents')
  yDomain: (data, yProp, domainExtents) => computeExtent(data, (d) => d[yProp], domainExtents.y),

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
    this.set('svgGroup', select(this.element).select('svg g'));
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
    select(this.element).select('.rsa-chart-background')
      .on('mouseout', function() {
        self.set('hoverIndex', null);
      })
      .on('mousemove', function() {
        const { xProp, xScale } = self.getProperties('xProp', 'xScale');
        const data = self.get('data').objectAt(0);
        if (data && data.length > 0) {
          const x0 = xScale.invert(mouse(this)[0]);
          const bisectLeft = bisector((d) => d[xProp]).left;
          const i = bisectLeft(data, x0, 1);
          const d0 = data[i - 1];
          const d1 = data[i];
          if (d0 && d1) {
            const index = x0 - d0[xProp] > d1[xProp] - x0 ? i : i - 1;
            self.set('hoverIndex', index);
          } else {
            self.set('hoverIndex', 0);
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
