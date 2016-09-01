import Ember from 'ember';
import d3 from 'd3';
import layout from '../templates/components/rsa-chart-grids';

const {
  Component,
  run
} = Ember;

export default Component.extend({
  layout,
  tagName: 'g',
  classNames: ['grids'],
  showXGrid: true,
  showYGrid: true,
  // tickCount is more of a suggestion,
  // so it's possible that we could get more
  tickCount: 6,

  didInsertElement() {
    this._super(...arguments);
    const width = this.get('width');
    const height = this.get('height');
    const xScale = this.get('xScale');
    const yScale = this.get('yScale');
    const count = this.get('tickCount');
    if (xScale || yScale) {
      run.scheduleOnce('afterRender', this, this.draw, width, height, xScale, yScale, count);
    }
  },

  didUpdateAttrs() {
    this._super(...arguments);
    const width = this.get('width');
    const height = this.get('height');
    const xScale = this.get('xScale');
    const yScale = this.get('yScale');
    const count = this.get('tickCount');
    if (xScale || yScale) {
      this.draw(width, height, xScale, yScale, count);
    }
  },

  draw(width, height, xScale, yScale, count) {
    const el = d3.select(this.element);
    if (this.get('showXGrid')) {
      el.select('.x').call(
        d3.axisBottom(xScale).tickSize(height).tickFormat('')
      );
    }
    if (this.get('showYGrid')) {
      el.select('.y').call(
        d3.axisRight(yScale).ticks(count).tickSize(width).tickFormat('')
      );
    }
  }
});