import Ember from 'ember';
import HasChartParent from '../mixins/has-chart-parent';
import d3 from 'd3';
import computed, { alias } from 'ember-computed-decorators';

const {
  Component,
  observer,
  run,
  warn
} = Ember;
const createArea = (xAccessorFn, yAccessorFn, height, curve) => {
  return d3.area().x(xAccessorFn).y0(height).y1(yAccessorFn).curve(curve);
};
const createSymbol = d3.symbol().type(d3.symbolDiamond);

export default Component.extend(HasChartParent, {
  attributeBindings: ['clipPath:clip-path'],
  classNameBindings: [':rsa-area-series', 'clazzName'],
  tagName: 'path',

  duration: 0,
  data: [[]],
  dataIndex: 0,
  height: 0,
  interpolator: d3.curveLinear,
  xProp: 'x',
  xScale: d3.scaleTime,
  yProp: 'y',
  yScale: d3.scaleLinear,

  @alias('chart.hoverIndex')
  hoverIndex: null,

  @computed('datum', 'hoverIndex')
  hoverData: (datum, index) => datum.objectAt(index),

  @computed('data', 'dataIndex')
  datum: (data, index) => data.objectAt(index),

  // If there's only one data element, we don't need a clipPath
  @computed('clipId', 'datum')
  clipPath: (id, datum) => datum.length > 1 ? `url(#${id})` : '',

  @computed('dataIndex')
  clazzName: (index) => `series-${index}`,

  @computed('xScale', 'xProp')
  xAccessor: (scale, prop) => (d) => d ? scale(d[prop]) : 0,

  @computed('yScale', 'yProp')
  yAccessor: (scale, prop) => (d) => d ? scale(d[prop]) : 0,

  @computed('xAccessor', 'yAccessor', 'height', 'interpolator')
  pathFn: (...args) => createArea(...args),

  symbolFn: () => createSymbol(),

  hoverIndexWatcher: observer('hoverIndex', function() {
    const { hoverData, xAccessor, yAccessor } = this.getProperties('hoverData', 'xAccessor', 'yAccessor');
    const point = this.get('_point');
    this.updateHover(point, hoverData, xAccessor, yAccessor);
  }),

  didInsertElement() {
    this._super(...arguments);
    const { datum, xAccessor, duration } = this.getProperties('datum', 'xAccessor', 'duration');
    run.scheduleOnce('afterRender', this, this.onAfterRender, datum, xAccessor, duration);
  },

  didUpdateAttrs() {
    this._super(...arguments);
    const { datum, xAccessor, duration } = this.getProperties('datum', 'xAccessor', 'duration');
    this.draw(datum, xAccessor, duration);
  },

  updateHover(point, hoverData, xAccessor, yAccessor) {
    if (hoverData) {
      const x = xAccessor(hoverData);
      const y = yAccessor(hoverData);
      point
        .attr('transform', `translate(${x},${y})`)
        .attr('opacity', 1);
    } else {
      point.attr('opacity', 0);
    }
  },

  onAfterRender(datum, xAccessor, duration) {
    const clazzName = this.get('clazzName');
    let svgGroup = this.get('chart.svgGroup');

    if (svgGroup) {
      // Append a group to the parent SVG so we can store our hover point
      svgGroup.append('g').attr('class', `points ${clazzName}`);
      const point = d3.select(`.points.${clazzName}`).append('circle')
        .attr('class', 'pt')
        .attr('r', 3)
        .attr('opacity', 0);
      this.set('_point', point);
    } else {
      warn('rsa-area-series missing chart.svgGroup', false, { id: 'rsa-area-series.missing-svgGroup' });
    }

    this.draw(datum, xAccessor, duration);
  },

  draw(datum, xAccessor, duration) {
    const pathFn = (datum.length === 1) ? this.get('symbolFn') : this.get('pathFn');

    d3.select(this.element)
      .datum(datum)
      .transition().duration(duration)
      .attr('d', pathFn);

    if (datum.length === 1) {
      // move single point into position
      d3.select(this.element)
        .attr('transform', `translate(${xAccessor(datum[0])}, 0)`);
    }
  }
});