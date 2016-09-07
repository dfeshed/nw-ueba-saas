import Ember from 'ember';
import d3 from 'd3';
import computed from 'ember-computed-decorators';

const { Component } = Ember;

/**
 * The x axis component.
 * @example {{x-axis height=graphHeight xScale=xScale rotation=-25}}
 * @public
 */
export default Component.extend({
  tagName: 'g',
  attributeBindings: ['transform'],
  classNames: ['rsa-x-axis'],
  scale: null,
  height: 115,
  rotation: 0,
  tickCount: 10,

  @computed('height')
  transform: (height) => `translate(0,${height})`,

  didInsertElement() {
    this._super(...arguments);
    const scale = this.get('scale');
    if (scale) {
      const { rotation, tickCount } = this.getProperties('rotation', 'tickCount');
      const axis = d3.select(this.element);
      if (scale.range) {
        const range = scale.range();
        scale.range([range[0], range[1] - 1]);
      }
      axis.call(d3.axisBottom(scale).ticks(tickCount));
      if (rotation) {
        this.rotateAxis(axis, rotation);
      }
    }
  },

  didUpdateAttrs() {
    this._super(...arguments);
    const { rotation, tickCount } = this.getProperties('rotation', 'tickCount');
    const axis = d3.select(this.element);
    const scale = this.get('scale');
    // So I noticed that the right side of the domain
    // path is clipped from view. This is because the
    // path that's generated is moved right 0.5 pixels
    // on the left and right sided. So, we need to
    // subtract 1px to account for this.
    if (scale.range) {
      const range = scale.range();
      scale.range([range[0], range[1] - 1]);
    }
    this.update(axis, scale, rotation, tickCount);
  },

  update(axis, scale, rotation, tickCount) {
    axis.transition().duration(750)
      .call(d3.axisBottom(scale).ticks(tickCount));
    if (rotation) {
      this.rotateAxis(axis, rotation);
    }
  },

  rotateAxis(axis, degrees) {
    axis.selectAll('text').attr('transform', `rotate(${degrees})`);
  }
});
