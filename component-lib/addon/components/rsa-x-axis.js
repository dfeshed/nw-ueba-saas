import Ember from 'ember';
import computed from 'ember-computed-decorators';
import { axisBottom } from 'd3-axis';
import { multiDateFormat } from '../utils/chart-utils';
import { select } from 'd3-selection';

const { Component } = Ember;

/**
 * The x axis component.
 * @example {{x-axis height=graphHeight xScale=xScale rotation=-25}}
 * @public
 */
export default Component.extend({
  attributeBindings: ['transform'],
  classNames: ['rsa-x-axis'],
  tagName: 'g',

  duration: 0,
  height: 100,
  rotation: 0,
  scale: null,
  tickCount: 10,
  tickFormat: multiDateFormat,

  @computed('height')
  transform: (height) => `translate(0,${height})`,

  didInsertElement() {
    this._super(...arguments);
    const scale = this.get('scale');
    if (scale) {
      const { rotation, tickCount } = this.getProperties('rotation', 'tickCount');
      const axis = select(this.element);
      const format = this.get('tickFormat');
      axis.call(axisBottom(scale).ticks(tickCount).tickFormat(format));
      if (rotation) {
        this.rotateAxis(axis, rotation);
      }
    }
  },

  didUpdateAttrs() {
    this._super(...arguments);
    const { rotation, tickCount, duration } = this.getProperties('rotation', 'tickCount', 'duration');
    const axis = select(this.element);
    const format = this.get('tickFormat');
    const scale = this.get('scale');
    this.update(axis, scale, rotation, tickCount, format, duration);
  },

  update(axis, scale, rotation, tickCount, format, duration) {
    axis.transition().duration(duration)
      .call(axisBottom(scale).ticks(tickCount).tickFormat(format));
    if (rotation) {
      this.rotateAxis(axis, rotation);
    }
  },

  rotateAxis(axis, degrees) {
    axis.selectAll('text').attr('transform', `rotate(${degrees})`);
  }
});
