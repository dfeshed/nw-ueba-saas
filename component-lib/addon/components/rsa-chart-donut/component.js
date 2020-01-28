import Component from '@ember/component';
import { layout as templateLayout } from '@ember-decorators/component';
import { select } from 'd3-selection';
import * as CHROMATIC from 'd3-scale-chromatic';
import { arc, pie } from 'd3-shape';
import layout from './template';
import { interpolateColors } from 'component-lib/utils/chart-utils';
import { computed } from '@ember/object';

/**
 * Supported colors, If you want to introduce new color then add the mapping
 */
const COLOR_SCALE_MAPPING = {
  'BLUE': 'interpolateBlues',
  'GREEN': 'interpolateGreens',
  'ORANGE': 'interpolateOranges',
  'RED': 'interpolateReds',
  'MULTI': 'interpolateRainbow'
};

@templateLayout(layout)
export default class LayoutColumn extends Component {

  columnClass = null;

  legendSpacing = 32;

  legendGap = 16;

  legendHeight = 35;

  legendWidth = 5;

  columnName = null;

  /**
   * One can pass the chart configuration options to change the look and feel of the donut chart
   * Supported configuration options are
   *  - radius - @type {String}  External Radius for the donut chart
   *  - innerRadius - @type {String} Inner radius to make arc for donut
   *  - showLegend - @type {boolean} Controlling the hide and show of the legend. Default to true
   *  - valueProp - @type {String} Aggregate value to be look in the data object. Defaults to 'count'
   *  - colorScale - @type {String} Color options for donut-chart. Supported values are BLUE, GREEN, ORANGE, RED, MULTI
   *  - showNoResultMessage - @type {boolean} - To control no result message display. Defaults to 'true'
   */
  options = null;

  fontSize = '1.5em';

  @computed('hasData', 'options.showNoResultMessage')
  get showMessage() {
    return !this.hasData && this.options.showNoResultMessage;
  }

  get hasData() {
    return !!this.data.length;
  }

  /**
   * Creates the arc for the pie chart
    * @param options
   * @returns {*}
   * @private
   */
  _createArc(options) {
    const { radius, innerRadius } = options;
    return arc()
      .outerRadius(radius)
      .innerRadius(innerRadius);
  }

  /**
   * Creates the slice for the pie chart
   * @returns {*}
   * @private
   */
  _cretePie({ valueProp }) {
    return pie()
      .sort(null)
      .value((d) => d[valueProp]);
  }

  /**
   * Create the SVG element to render the chart
   * @param element
   * @param options
   * @returns {*}
   * @private
   */
  _createSVG(element, options) {
    const { width, height, radius } = options;
    return select(element).append('svg')
      .attr('class', 'donut-chart')
      .attr('width', width)
      .attr('height', height)
      .append('g')
      .attr('transform', `translate(${radius},${ height / 2 })`);
  }

  /**
   * Adds legend to the chart
   * @param svg
   * @param color
   * @param options
   * @private
   */
  _addLegend(svg, color, options) {
    const { legendSpacing, legendHeight, legendWidth, legendGap } = this;
    const { radius } = options;
    const legendX = radius + legendSpacing;
    svg.selectAll('.legend')
      .data(this.data)
      .enter()
      .append('rect')
      .attr('class', 'legend')
      .attr('x', legendX)
      .attr('y', (d, i) => -radius + i * (legendHeight + legendGap))
      .attr('width', legendWidth)
      .attr('height', legendHeight)
      .style('fill', (d, i) => color[i]);

    svg.selectAll('.label')
      .data(this.data)
      .enter()
      .append('text')
      .style('alignment-baseline', 'middle')
      .attr('class', 'label')
      .attr('text-anchor', 'left')
      .attr('x', legendX + 16 * 1.2)
      .attr('y', (d, i) => -radius + i * (legendHeight + legendGap) + legendHeight / 2)
      .style('fill', (d, i) => color[i])
      .text((d) => d.name);
  }

  _renderChart() {
    const { options, hasData, data } = this;
    const { colorScale } = options;
    const scale = COLOR_SCALE_MAPPING[colorScale];

    if (hasData) {
      const svg = this._createSVG(this.element, options);
      const pie = this._cretePie(options);
      const arc = this._createArc(options);
      const color = interpolateColors(data.length, CHROMATIC[scale]);

      // Create Donut chart and append text
      const arcGroup = svg.selectAll('.arc')
        .data(pie(data))
        .enter()
        .append('g');

      arcGroup.append('path')
        .attr('d', arc)
        .style('fill', (d, i) => color[i]);

      if (this.columnName) {
        svg.append('text')
          .attr('text-anchor', 'middle')
          .attr('class', 'column-name')
          .attr('font-size', this.fontSize)
          .attr('y', 10)
          .text(this.columnName);
      }

      if (options.showLegend) {
        // Add legend
        this._addLegend(svg, color, options);
      }
    }
  }

  init() {
    super.init(...arguments);
    // Merging default options with user defined
    this.options = { ...{
      colorScale: 'BLUE',
      height: 350,
      width: 430,
      radius: 120,
      innerRadius: 100,
      showLegend: true,
      valueProp: 'count',
      showNoResultMessage: true
    }, ...this.options };

    this.data = this.data ? this.data : [];
  }

  didUpdateAttrs() {
    super.didUpdateAttrs(...arguments);
    this._renderChart();
  }

  didInsertElement() {
    super.didInsertElement(...arguments);
    this._renderChart();
  }
}
