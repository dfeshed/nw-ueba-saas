import Ember from 'ember';
import d3 from 'd3';

const { Component } = Ember;

/**
 * The y axis component.
 * @example {{y-axis yScale=yScale}}
 * @private
 */
export default Component.extend({
  tagName: 'g',
  classNames: ['rsa-y-axis'],
  scale: null,
  rotation: 0,

  didInsertElement() {
    this._super(...arguments);
    const scale = this.get('scale');
    if (scale) {
      const axis = d3.select(this.element);
      const rotation = this.get('rotation');
      axis.call(d3.axisLeft(scale).ticks(6, 's'));
      if (rotation) {
        this.rotateAxis(axis, rotation);
      }
    }
  },

  didUpdateAttrs() {
    this._super(...arguments);
    const axis = d3.select(this.element);
    const scale = this.get('scale');
    const rotation = this.get('rotation');
    this.update(axis, scale, rotation);
  },

  update(axis, scale, rotation) {
    axis.transition().duration(750)
      .call(d3.axisLeft(scale).ticks(6, 's'));
    if (rotation) {
      this.rotateAxis(axis, rotation);
    }
  },

  rotateAxis(axis, degrees) {
    axis.selectAll('text').attr('transform', `rotate(${degrees})`);
  }
});
