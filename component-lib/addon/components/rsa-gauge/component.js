import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { select } from 'd3-selection';
import { arc, radialLine } from 'd3-shape';
// Linter complains about non-use because of how the d3 imports work
// eslint-disable-next-line no-unused-vars
import { transition } from 'd3-transition';

export default Component.extend({
  value: 0,
  label: null,
  animate: true,
  showValue: true,
  display: null,
  numTicks: 6,
  tickLength: 15,

  tagName: 'svg',
  attributeBindings: ['viewBox'],

  prevValue: 0,
  width: 200,
  height: 200,
  needleCenterRadius: 5,
  gaugeMargin: 20,
  arcThickness: 4,
  // https://github.com/d3/d3-shape/blob/master/README.md#arc_startAngle
  // "The angle is specified in radians, with 0 at -y (12 o’clock) and
  // positive angles proceeding clockwise. If |endAngle - startAngle| ≥ τ,
  // a complete circle or annulus is generated rather than a sector."
  arcStartAngle: (-3 / 4) * Math.PI,
  arcEndAngle: (3 / 4) * Math.PI,
  colorPrimary: '#03A9F4', // Blue | text-color(secondary)
  colorSecondary: '#FFFFFF', // White | text-color(neutral)

  @computed('width', 'height')
  viewBox: (width, height) => {
    return `0, 0, ${width}, ${height}`;
  },

  @computed('width', 'height')
  minDimension: (width, height) => Math.min(width, height),

  @computed('minDimension', 'gaugeMargin')
  outerRadius: (minDimension, gaugeMargin) => minDimension * 0.5 - gaugeMargin,

  @computed('outerRadius', 'arcThickness')
  innerRadius: (outerRadius, arcThickness) => outerRadius - arcThickness,

  @computed('arcStartAngle', 'arcEndAngle')
  totalDegrees: (arcStartAngle, arcEndAngle) => (arcEndAngle - arcStartAngle) * 57.29577951308232, // 57.29577951308232 = 360/2π

  @computed('width')
  halfWidth: (width) => width * 0.5,

  @computed('height')
  halfHeight: (height) => height * 0.5,

  @computed('numTicks', 'tickLength', 'arcStartAngle', 'arcEndAngle', 'outerRadius')
  tickLineData: (numTicks, tickLength, arcStartAngle, arcEndAngle, outerRadius) => {
    const result = [];
    // Generates an array of arrays of two element arrays, where the two elements
    // are the angle and radius points of the tick lines
    if (numTicks > 0) {
      for (let i = 0; i < numTicks; i++) {
        const angle = (i / (numTicks - 1)) * (arcEndAngle - arcStartAngle) + arcStartAngle;
        result.push([[angle, outerRadius], [angle, outerRadius - tickLength]]);
      }
    }
    return result.map((line) => {
      return radialLine()(line);
    });
  },

  // Draws the initial state of the gauge
  didInsertElement() {
    this.firstDraw();
  },

  firstDraw() {
    const { halfWidth, halfHeight, value, colorPrimary, colorSecondary, outerRadius,
      innerRadius, totalDegrees, label, showValue, display, tickLineData, arcThickness } = this.getProperties(
      'halfWidth', 'halfHeight', 'value', 'colorPrimary', 'colorSecondary', 'outerRadius',
      'innerRadius', 'totalDegrees', 'label', 'showValue', 'display', 'tickLineData', 'arcThickness'
    );
    const parsedValue = this.parseValue(value);

    const gauge = this.set('gauge', select(`#${this.get('elementId')}`))
      .attr('width', '100%')
      .attr('height', '100%');

    // Create arc path
    const outsideArc = arc()
      .innerRadius(innerRadius)
      .outerRadius(outerRadius)
      .startAngle(this.get('arcStartAngle'))
      .endAngle(this.get('arcEndAngle'));

    // Draw needle base
    gauge.append('circle')
      .attr('class', 'gauge-needle-base')
      .attr('cx', halfWidth)
      .attr('cy', halfHeight)
      .attr('r', this.get('needleCenterRadius'))
      .attr('fill', colorPrimary);

    // Draw ticks
    for (let i = 0; i < tickLineData.length; i++) {
      gauge.append('path')
      .attr('class', 'gauge-ticks')
      .attr('d', tickLineData[i])
      .attr('stroke', colorSecondary)
      .attr('stroke-width', arcThickness)
      .attr('transform', `translate(${halfWidth},${halfHeight})`);
    }

    // Draw arc
    gauge.append('path')
      .attr('class', 'gauge-arc')
      .attr('d', outsideArc)
      .attr('fill', colorSecondary)
      .attr('transform', `translate(${halfWidth},${halfHeight})`);

    // Draw needle body
    gauge.append('path')
      .attr('class', 'gauge-needle-body')
      .attr('d', this.createNeedleData(0))
      .attr('fill', colorPrimary)
      .attr('transform', `translate(${halfWidth},${halfHeight}) rotate(${parsedValue * totalDegrees})`);

    // Draw label text
    gauge.append('text')
      .attr('class', 'gauge-label-text')
      .attr('fill', colorSecondary)
      .attr('x', '50%')
      .attr('y', '85%')
      .attr('text-anchor', 'middle')
      .text(label);

    if (showValue) {
      // Draw value text
      gauge.append('text')
        .attr('class', 'gauge-value-text')
        .attr('fill', colorSecondary)
        .attr('x', '50%')
        .attr('y', '75%')
        .attr('text-anchor', 'middle')
        .text(display || this.decorateValue(parsedValue));
    }

    // Store handles to the things we need to update
    this.setProperties({
      value: parsedValue,
      prevValue: parsedValue,
      needle: select(`#${this.elementId} .gauge-needle-body`),
      valueText: select(`#${this.elementId} .gauge-value-text`)
    });
  },

  didUpdateAttrs() {
    this.updateValue();
  },

  updateValue() {
    const prevValue = this.get('prevValue');
    const value = this.parseValue(this.get('value'));
    // If the value is the same, don't animate. This could cancel any in-progress
    // animation and make it look like the gauge isn't animating.
    if (prevValue === value) {
      return;
    }
    this.set('prevValue', value);
    const { needle, valueText, display, halfWidth, halfHeight, totalDegrees, animate } = this.getProperties(
      'needle', 'valueText', 'display', 'halfWidth', 'halfHeight', 'totalDegrees', 'animate'
    );
    if (animate) {
      needle
        .transition()
        .duration(500)
        .tween('progress', () => {
          return (t) => {
            const percentage = (prevValue + (value - prevValue) * t);
            const transitionDegrees = percentage * totalDegrees;
            needle.attr('transform', `translate(${halfWidth},${halfHeight}) rotate(${transitionDegrees})`);
          };
        });
    } else {
      needle.attr('transform', `translate(${halfWidth},${halfHeight}) rotate(${value * totalDegrees})`);
    }
    valueText.text(display || this.decorateValue(value));
  },

  createNeedleData(percentage) {
    const { arcStartAngle, arcEndAngle, needleCenterRadius, innerRadius } = this.getProperties(
      'arcStartAngle', 'arcEndAngle', 'needleCenterRadius', 'innerRadius'
    );
    const theta = (arcEndAngle - arcStartAngle) * percentage;

    const topAngle = theta - (Math.PI * 0.5) + arcStartAngle;
    const topX = Math.cos(topAngle) * innerRadius;
    const topY = Math.sin(topAngle) * innerRadius;

    const leftAngle = topAngle - (Math.PI * 0.5);
    const leftX = Math.cos(leftAngle) * needleCenterRadius;
    const leftY = Math.sin(leftAngle) * needleCenterRadius;

    const rightX = -1 * leftX;
    const rightY = -1 * leftY;

    return `M ${topX} ${topY} L ${leftX} ${leftY} L ${rightX} ${rightY} Z`;
  },

  decorateValue(value) {
    return `${(value * 100).toFixed(1)}%`;
  },

  parseValue(value) {
    let result = value;
    if (typeof value === 'string') {
      const last = value.length - 1;
      if (value.charAt(last) === '%') {
        result = parseFloat(value.substring(0, last)) / 100;
      } else {
        result = parseFloat(value);
      }
    }
    return result;
  }
});