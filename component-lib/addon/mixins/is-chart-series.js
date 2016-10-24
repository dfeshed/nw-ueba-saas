/**
 * @file Is Chart Series mixin
 * @public
 */
import Ember from 'ember';
import HasChartParent from '../mixins/has-chart-parent';
import computed, { alias } from 'ember-computed-decorators';
import { select } from 'd3-selection';
import { curveLinear, symbol, symbolDiamond } from 'd3-shape';
import { scaleLinear, scaleTime } from 'd3-scale';
import 'd3-transition';

const {
  Mixin,
  observer,
  run,
  warn
} = Ember;

const createSymbol = symbol().type(symbolDiamond).size(32);

export default Mixin.create(HasChartParent, {
  attributeBindings: ['clipPath:clip-path'],
  duration: 0,
  data: [],
  dataIndex: 0,
  interpolator: curveLinear,
  xProp: 'x',
  xScale: scaleTime,
  yProp: 'y',
  yScale: scaleLinear,

  @alias('chart.hoverIndex')
  hoverIndex: null,

  @computed('datum', 'hoverIndex')
  hoverData: (datum, index) => datum.objectAt(index),

  @computed('data', 'dataIndex')
  datum: (data, index) => data.objectAt(index) || [],

  // If there's only one data element, we don't need a clipPath
  @computed('clipId', 'data')
  clipPath: (id, data) => data.length > 1 ? `url(#${id})` : '',

  @computed('dataIndex')
  clazzName: (index) => `series-${index}`,

  @computed('xScale', 'xProp')
  xAccessor: (scale, prop) => (d) => d ? scale(d[prop]) : 0,

  @computed('yScale', 'yProp')
  yAccessor: (scale, prop) => (d) => d ? scale(d[prop]) : 0,

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

    if (!svgGroup) {
      // Provide a fallback that works for automated testing
      svgGroup = select(this.parent).select('g');
    }

    if (svgGroup) {
      // Append a group to the parent SVG so we can store our hover point
      svgGroup.append('g').attr('class', `points ${clazzName}`);
      const point = svgGroup.select(`.points.${clazzName}`).append('circle')
        .attr('class', 'pt')
        .attr('r', 3)
        .attr('opacity', 0);
      this.set('_point', point);
    } else {
      warn('chart series missing chart.svgGroup', false, { id: 'chart.series.missing-svgGroup' });
    }

    this.draw(datum, xAccessor, duration);
  },

  draw(datum, xAccessor, duration) {
    const pathFn = (datum.length === 1) ? this.get('symbolFn') : this.get('pathFn');
    const path = select(this.element).datum(datum)
      .classed('symbol', datum.length === 1 ? true : false);

    path.transition().duration(duration)
      .attr('d', pathFn)
      .attr('transform', () => {
        let ret;
        if (datum.length === 1) {
          ret = `translate(${xAccessor(datum[0])},0)`;
        } else {
          ret = 'translate(0,0)';
        }
        return ret;
      });
  }
});