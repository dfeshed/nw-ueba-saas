import Ember from 'ember';
import layout from '../templates/components/rsa-chart-grids';
import { axisBottom, axisRight } from 'd3-axis';
import { select } from 'd3-selection';

const {
  Component,
  run
} = Ember;

export default Component.extend({
  classNames: ['grids'],
  layout,
  tagName: 'g',

  showXGrid: true,
  showYGrid: true,
  xTickCount: 10,
  yTickCount: 10,

  didInsertElement() {
    this._super(...arguments);
    const { width, height, xScale, yScale, xTickCount, yTickCount } = this.getProperties('width', 'height', 'xScale', 'yScale', 'xTickCount', 'yTickCount');
    if (xScale || yScale) {
      run.scheduleOnce('afterRender', this, this.draw, width, height, xScale, yScale, xTickCount, yTickCount);
    }
  },

  didUpdateAttrs() {
    this._super(...arguments);
    const { width, height, xScale, yScale, xTickCount, yTickCount } = this.getProperties('width', 'height', 'xScale', 'yScale', 'xTickCount', 'yTickCount');
    if (xScale || yScale) {
      this.draw(width, height, xScale, yScale, xTickCount, yTickCount);
    }
  },

  draw(width, height, xScale, yScale, xTickCount, yTickCount) {
    const el = select(this.element);
    if (this.get('showXGrid')) {
      el.select('.x').call(
        axisBottom(xScale).ticks(xTickCount).tickSize(height).tickFormat('')
      );
    }
    if (this.get('showYGrid')) {
      el.select('.y').call(
        axisRight(yScale).ticks(yTickCount).tickSize(width).tickFormat('')
      );
    }
  }
});