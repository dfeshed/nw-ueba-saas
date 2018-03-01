import Component from '@ember/component';
import computed, { equal } from 'ember-computed';
import layout from '../templates/components/rsa-loader';

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
    const loaderSizeLabels = this.get('loaderSizeLabels');
    const size = this.get('size');

    return (!loaderSizeLabels.includes(size) || size === 'small');
  }),
  isMedium: equal('size', 'medium'),
  isLarge: equal('size', 'large'),
  isLarger: equal('size', 'larger'),
  isLargest: equal('size', 'largest')
});
