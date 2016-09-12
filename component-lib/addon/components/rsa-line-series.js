import Ember from 'ember';
import d3 from 'd3';
import computed from 'ember-computed-decorators';

const { Component, run } = Ember;
const createLine = (xAccessorFn, yAccessorFn, curve) => {
  return d3.line().x(xAccessorFn).y(yAccessorFn).curve(curve);
};
const createSymbol = d3.symbol().type(d3.symbolDiamond);

export default Component.extend({
  classNameBindings: [':rsa-line-series', 'clazzName'],
  tagName: 'path',

  data: [[]],
  dataIndex: 0,
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

  @computed('xAccessor', 'yAccessor', 'interpolator')
  lineFn: (...args) => createLine(...args),

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
    const { xAccessor, yAccessor, clazzName } = this.getProperties('xAccessor', 'yAccessor', 'clazzName');
    const pathFn = (datum.length === 1) ? this.get('symbolFn') : this.get('lineFn');
    const points = d3.select(`.points.${clazzName}`).selectAll('circle')
      .data(datum);

    d3.select(this.element)
      .datum(datum)
      .transition().duration(750)
      .attr('d', pathFn);

    points.attr('opacity', (d) => this.showNode(hoverData, d))
      .transition().duration(750)
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