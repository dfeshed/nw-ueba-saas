import Component from '@ember/component';
import { equal } from 'ember-computed';
import layout from './template';

export default Component.extend({
  layout,

  tagName: 'div',
  classNames: ['rsa-content-label'],
  classNameBindings: [
    'isDanger',
    'isDisabled',
    'isInverted',
    'isHigh',
    'isLargeSize',
    'isLow',
    'isMedium',
    'isMediumSize',
    'isNeutral',
    'isSmallSize',
    'isStandard'
  ],

  isDisabled: false,
  isInverted: false,

  size: 'small', // ['small', 'medium', 'large']
  isSmallSize: equal('size', 'small'),
  isMediumSize: equal('size', 'medium'),
  isLargeSize: equal('size', 'large'),

  style: 'standard', // ['standard', 'neutral', 'low', 'medium', 'high', 'danger']
  isStandard: equal('style', 'standard'),
  isNeutral: equal('style', 'neutral'),
  isLow: equal('style', 'low'),
  isMedium: equal('style', 'medium'),
  isHigh: equal('style', 'high'),
  isDanger: equal('style', 'danger')

});
