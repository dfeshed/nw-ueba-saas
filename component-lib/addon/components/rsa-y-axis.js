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
  tickCount: 10,

  didInsertElement() {
    this._super(...arguments);
    const scale = this.get('scale');
    if (scale) {
      const { rotation, tickCount } = this.getProperties('rotation', 'tickCount');
      const axis = d3.select(this.element);
      axis.call(d3.axisLeft(scale).ticks(tickCount, 's'));
      if (rotation) {
        this.rotateAxis(axis, rotation);
      }
    }
  },

  didUpdateAttrs() {
    this._super(...arguments);
    const { scale, rotation, tickCount } = this.getProperties('scale', 'rotation', 'tickCount');
    const axis = d3.select(this.element);
    this.update(axis, scale, rotation, tickCount);
  },

  update(axis, scale, rotation, tickCount) {
    axis.transition().duration(750)
      .call(d3.axisLeft(scale).ticks(tickCount, 's'));
    if (rotation) {
      this.rotateAxis(axis, rotation);
    }
  },

  rotateAxis(axis, degrees) {
    axis.selectAll('text').attr('transform', `rotate(${degrees})`);
  }
});
