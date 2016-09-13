import Ember from 'ember';
import d3 from 'd3';
import { siFormat } from '../utils/chart-utils';

const { Component } = Ember;

/**
 * The y axis component.
 * @example {{y-axis yScale=yScale}}
 * @private
 */
export default Component.extend({
  classNames: ['rsa-y-axis'],
  tagName: 'g',

  rotation: 0,
  scale: null,
  tickCount: 10,
  tickFormat: siFormat,

  didInsertElement() {
    this._super(...arguments);
    const scale = this.get('scale');
    if (scale) {
      const { rotation, tickCount } = this.getProperties('rotation', 'tickCount');
      const axis = d3.select(this.element);
      const count = this.get('tickCount');
      const format = this.get('tickFormat')(scale.domain(), count);
      axis.call(d3.axisLeft(scale).ticks(tickCount).tickFormat(format));
      if (rotation) {
        this.rotateAxis(axis, rotation);
      }
    }
  },

  didUpdateAttrs() {
    this._super(...arguments);
    const { scale, rotation, tickCount } = this.getProperties('scale', 'rotation', 'tickCount');
    const axis = d3.select(this.element);
    const count = this.get('tickCount');
    const format = this.get('tickFormat')(scale.domain(), count);
    this.update(axis, scale, rotation, tickCount, format);
  },

  update(axis, scale, rotation, tickCount, format) {
    axis.transition().duration(750)
      .call(d3.axisLeft(scale).ticks(tickCount).tickFormat(format));
    if (rotation) {
      this.rotateAxis(axis, rotation);
    }
  },

  rotateAxis(axis, degrees) {
    axis.selectAll('text').attr('transform', `rotate(${degrees})`);
  }
});
