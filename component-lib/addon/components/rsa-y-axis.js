import Ember from 'ember';
import { axisLeft } from 'd3-axis';
import { select } from 'd3-selection';
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

  duration: 0,
  rotation: 0,
  scale: null,
  tickCount: 10,
  tickFormat: siFormat,

  didInsertElement() {
    this._super(...arguments);
    const scale = this.get('scale');
    if (scale) {
      const { rotation, tickCount } = this.getProperties('rotation', 'tickCount');
      const axis = select(this.element);
      const count = this.get('tickCount');
      const format = this.get('tickFormat')(scale.domain(), count);
      axis.call(axisLeft(scale).ticks(tickCount).tickFormat(format));
      if (rotation) {
        this.rotateAxis(axis, rotation);
      }
    }
  },

  didUpdateAttrs() {
    this._super(...arguments);
    const { scale, rotation, tickCount, duration } = this.getProperties('scale', 'rotation', 'tickCount', 'duration');
    const axis = select(this.element);
    const count = this.get('tickCount');
    const format = this.get('tickFormat')(scale.domain(), count);
    this.update(axis, scale, rotation, tickCount, format, duration);
  },

  update(axis, scale, rotation, tickCount, format, duration) {
    axis.transition().duration(duration)
      .call(axisLeft(scale).ticks(tickCount).tickFormat(format));
    if (rotation) {
      this.rotateAxis(axis, rotation);
    }
  },

  rotateAxis(axis, degrees) {
    axis.selectAll('text').attr('transform', `rotate(${degrees})`);
  }
});
