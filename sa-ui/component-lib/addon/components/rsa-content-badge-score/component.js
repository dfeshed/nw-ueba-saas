import Component from '@ember/component';
import computed from 'ember-computed';
import layout from './template';

export default Component.extend({

  layout,

  classNames: ['rsa-content-badge-score'],

  classNameBindings: [
    'hideLabel',
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
    'progressBarLength',
    'invertColor'],

  score: null,

  label: null,
  isInline: false,
  invertColor: false,

  hideLabel: computed.not('label'),

  style: null, // ['low', 'medium', 'high', 'danger']

  size: 'default', // ['small', 'default', 'large', 'larger', 'largest']

  isLow: computed.equal('style', 'low'),
  isMedium: computed.equal('style', 'medium'),
  isHigh: computed.equal('style', 'high'),
  isDanger: computed.equal('style', 'danger'),

  isSmaller: computed.equal('size', 'smaller'),
  isNotSmaller: computed.not('isSmaller'),
  isSmall: computed.equal('size', 'small'),
  isLarge: computed.equal('size', 'large'),
  isLarger: computed.equal('size', 'larger'),
  isLargest: computed.equal('size', 'largest'),

  progressBarLength: computed('score', function() {
    return `progress-bar-length-${ Math.max(10, Math.floor(this.get('score'))) }`;
  })
});
