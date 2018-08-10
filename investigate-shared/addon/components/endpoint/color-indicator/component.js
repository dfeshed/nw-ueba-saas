import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';

const RADIUS = {
  small: 5,
  medium: 6
};

export default Component.extend({
  layout,
  tagName: 'svg',
  classNames: ['color-indicator'],
  attributeBindings: ['diameter:width', 'diameter:height'],
  classNameBindings: ['color', 'size'],
  color: 'red',
  size: 6,

  @computed('size')
  radius(size) {
    return RADIUS[size] || size;
  },

  @computed('radius')
  diameter(radius) {
    return 2 * radius;
  }
});
