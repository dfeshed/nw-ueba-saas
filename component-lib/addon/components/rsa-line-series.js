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
  hoverData: null,

  @computed('dataIndex')
  clazzName: (index) => `series-${index}`,

  @computed('xScale', 'xProp')
  xAccessor: (scaleFn, prop) => (d) => d ? scaleFn(d[prop]) : 0,

  @computed('yScale', 'yProp')
  yAccessor: (scaleFn, prop) => (d) => d ? scaleFn(d[prop]) : 0,

  @computed('xAccessor', 'yAccessor', 'interpolator')
  lineFn: (...args) => createLine(...args),

  didInsertElement() {
    this._super(...arguments);
    const { dataIndex, lineFn, hoverData } = this.getProperties('dataIndex', 'lineFn', 'hoverData');
    const datum = this.get('data').objectAt(dataIndex);
    run.scheduleOnce('afterRender', this, this.onAfterRender, datum, lineFn, hoverData);
  },

  didUpdateAttrs() {
    this._super(...arguments);
    const { dataIndex, lineFn, hoverData } = this.getProperties('dataIndex', 'lineFn', 'hoverData');
    const datum = this.get('data').objectAt(dataIndex);
    this.draw(datum, lineFn, hoverData);
  },

  showNode(hoverData, d) {
    const xProp = this.get('xProp');
    return (hoverData && hoverData[xProp] === d[xProp]) ? 1 : 0;
  },

  onAfterRender(datum, lineFn, hoverData) {
    const clazzName = this.get('clazzName');
    let svgGroup = this.get('parentView.svgGroup');
    if (!svgGroup) {
      // Provide a fallback that works for automated testing
      svgGroup = d3.select(this.parent).select('g');
    }
    // Append a group to the parent SVG so we can store our hover points for this series together
    svgGroup.append('g').attr('class', `points ${clazzName}`);
    this.draw(datum, lineFn, hoverData);
  },

  draw(datum, lineFn, hoverData) {
    const { xAccessor, yAccessor, clazzName } = this.getProperties('xAccessor', 'yAccessor', 'clazzName');
    const points = d3.select(`.points.${clazzName}`).selectAll('circle')
      .data(datum);

    d3.select(this.element)
      .datum(datum)
      .transition().duration(750)
      .attr('d', lineFn);

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
  }
});