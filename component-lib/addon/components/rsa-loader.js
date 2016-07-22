import Ember from 'ember';
import layout from '../templates/components/rsa-loader';

const {
  Component,
  computed,
  computed: {
    equal
  }
} = Ember;

export default Component.extend({
  layout,

  classNames: ['rsa-loader'],
  classNameBindings: ['isSmall',
    'isMedium',
    'isLarge',
    'isLarger',
    'isLargest'
  ],

  loaderSizeLabels: [
    'small',
    'medium',
    'large',
    'larger',
    'largest'
  ],

  size: 'small',
  isSmall: computed('loaderSizeLabels', 'size', function() {
    let loaderSizeLabels = this.get('loaderSizeLabels');
    let size = this.get('size');

    return (!loaderSizeLabels.contains(size) || size === 'small');
  }),
  isMedium: equal('size', 'medium'),
  isLarge: equal('size', 'large'),
  isLarger: equal('size', 'larger'),
  isLargest: equal('size', 'largest')
});
