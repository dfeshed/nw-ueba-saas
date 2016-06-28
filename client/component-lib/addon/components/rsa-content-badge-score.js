import Ember from 'ember';
import layout from '../templates/components/rsa-content-badge-score';

export default Ember.Component.extend({

  layout,

  classNames: ['rsa-content-badge-score'],

  classNameBindings: ['hideLabel',
                      'isLow',
                      'isMedium',
                      'isHigh',
                      'isDanger',
                      'isHigh',
                      'isSmall',
                      'isLarge',
                      'isLarger',
                      'isLargest'],

  score: null,

  label: null,

  hideLabel: Ember.computed.not('label'),

  style: null, // ['low', 'medium', 'high', 'danger']

  size: 'default', // ['small', 'default', 'large', 'larger', 'largest']

  isLow: Ember.computed.equal('style', 'low'),
  isMedium: Ember.computed.equal('style', 'medium'),
  isHigh: Ember.computed.equal('style', 'high'),
  isDanger: Ember.computed.equal('style', 'danger'),

  isSmall: Ember.computed.equal('size', 'small'),
  isLarge: Ember.computed.equal('size', 'large'),
  isLarger: Ember.computed.equal('size', 'larger'),
  isLargest: Ember.computed.equal('size', 'largest')
});