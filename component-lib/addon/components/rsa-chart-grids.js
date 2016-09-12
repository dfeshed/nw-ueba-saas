import Ember from 'ember';
import d3 from 'd3';
import layout from '../templates/components/rsa-chart-grids';

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
    const el = d3.select(this.element);
    if (this.get('showXGrid')) {
      el.select('.x').call(
        d3.axisBottom(xScale).ticks(xTickCount).tickSize(height).tickFormat('')
      );
    }
    if (this.get('showYGrid')) {
      el.select('.y').call(
        d3.axisRight(yScale).ticks(yTickCount).tickSize(width).tickFormat('')
      );
    }
  }
});