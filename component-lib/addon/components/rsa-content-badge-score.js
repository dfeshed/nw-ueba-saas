import Ember from 'ember';
import layout from '../templates/components/rsa-content-badge-score';

const {
  Component,
  computed
} = Ember;

export default Component.extend({

  layout,

  classNames: ['rsa-content-badge-score'],

  classNameBindings: ['hideLabel',
                      'isLow',
                      'isMedium',
                      'isHigh',
                      'isDanger',
                      'isHigh',
                      'isSmall',
                      'isSmaller',
                      'isLarge',
                      'isLarger',
                      'isLargest',
                      'isInline',
                      'progressBarLength'],

  score: null,

  label: null,
  isInline: false,

  hideLabel: computed.not('label'),

  style: null, // ['low', 'medium', 'high', 'danger']

  size: 'default', // ['small', 'default', 'large', 'larger', 'largest']

  isLow: computed.equal('style', 'low'),
  isMedium: computed.equal('style', 'medium'),
  isHigh: computed.equal('style', 'high'),
  isDanger: computed.equal('style', 'danger'),

  isSmaller: computed.equal('size', 'smaller'),
  isSmall: computed.equal('size', 'small'),
  isLarge: computed.equal('size', 'large'),
  isLarger: computed.equal('size', 'larger'),
  isLargest: computed.equal('size', 'largest'),

  progressBarLength: computed('score', function() {
    return `progress-bar-length-${ Math.max(10, Math.floor(this.get('score'))) }`;
  })
});
