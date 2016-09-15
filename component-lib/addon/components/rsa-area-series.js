import Ember from 'ember';
import d3 from 'd3';
import computed from 'ember-computed-decorators';

const { Component, run } = Ember;
const createArea = (xAccessorFn, yAccessorFn, height, curve) => {
  return d3.area().x(xAccessorFn).y0(height).y1(yAccessorFn).curve(curve);
};
const createSymbol = d3.symbol().type(d3.symbolDiamond);

export default Component.extend({
  classNameBindings: [':rsa-area-series', 'clazzName'],
  tagName: 'path',

  duration: 300,
  data: [[]],
  dataIndex: 0,
  height: 0,
  hoverData: null,
  interpolator: d3.curveLinear,
  xProp: 'x',
  xScale: d3.scaleTime,
  yProp: 'y',
  yScale: d3.scaleLinear,

  @computed('dataIndex')
  clazzName: (index) => `series-${index}`,

  @computed('xScale', 'xProp')
  xAccessor: (scale, prop) => (d) => d ? scale(d[prop]) : 0,

  @computed('yScale', 'yProp')
  yAccessor: (scale, prop) => (d) => d ? scale(d[prop]) : 0,

  @computed('xAccessor', 'yAccessor', 'height', 'interpolator')
  pathFn: (...args) => createArea(...args),

  symbolFn: () => createSymbol(),

  didInsertElement() {
    this._super(...arguments);
    const { dataIndex, hoverData } = this.getProperties('dataIndex', 'hoverData');
    const datum = this.get('data').objectAt(dataIndex);
    run.scheduleOnce('afterRender', this, this.onAfterRender, datum, hoverData);
  },

  didUpdateAttrs() {
    this._super(...arguments);
    const { dataIndex, hoverData } = this.getProperties('dataIndex', 'hoverData');
    const datum = this.get('data').objectAt(dataIndex);
    this.draw(datum, hoverData);
  },

  showNode(hoverData, d) {
    const xProp = this.get('xProp');
    return (hoverData && hoverData[xProp] === d[xProp]) ? 1 : 0;
  },

  onAfterRender(datum, hoverData) {
    const clazzName = this.get('clazzName');
    let svgGroup = this.get('parentView.svgGroup');
    if (!svgGroup) {
      // Provide a fallback that works for automated testing
      svgGroup = d3.select(this.parent).select('g');
    }
    // Append a group to the parent SVG so we can store our hover points for this series together
    svgGroup.append('g').attr('class', `points ${clazzName}`);
    this.draw(datum, hoverData);
  },

  draw(datum, hoverData) {
    const { xAccessor, yAccessor, clazzName, duration } = this.getProperties('xAccessor', 'yAccessor', 'clazzName', 'duration');
    const pathFn = (datum.length === 1) ? this.get('symbolFn') : this.get('lineFn');
    const points = d3.select(`.points.${clazzName}`).selectAll('circle')
      .data(datum);

    d3.select(this.element)
      .datum(datum)
      .transition().duration(duration)
      .attr('d', pathFn);

    points.attr('opacity', (d) => this.showNode(hoverData, d))
      .transition().duration(duration)
      .attr('cx', xAccessor)
      .attr('cy', yAccessor);

    points.enter().append('circle')
      .attr('class', 'pt')
      .attr('cx', xAccessor)
      .attr('cy', yAccessor)
      .attr('r', 3)
      .attr('opacity', (d) => this.showNode(hoverData, d));

    points.exit().remove();

    if (datum.length === 1) {
      // move single point into position
      d3.select(this.element)
        .attr('transform', `translate(${xAccessor(datum[0])}, 0)`);
    }
  }
});