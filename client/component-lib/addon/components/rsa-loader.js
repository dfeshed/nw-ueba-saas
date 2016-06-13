import Ember from 'ember';
import layout from '../templates/components/rsa-loader';

export default Ember.Component.extend({
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
  isSmall: Ember.computed('loaderSizeLabels', 'size', function() {
    let loaderSizeLabels = this.get('loaderSizeLabels'),
        size = this.get('size');
    return (!loaderSizeLabels.contains(size) || size === 'small');
  }),
  isMedium: Ember.computed.equal('size', 'medium'),
  isLarge: Ember.computed.equal('size', 'large'),
  isLarger: Ember.computed.equal('size', 'larger'),
  isLargest: Ember.computed.equal('size', 'largest')
});
