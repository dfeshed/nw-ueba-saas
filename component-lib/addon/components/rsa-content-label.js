import Ember from 'ember';
import layout from '../templates/components/rsa-content-label';

const {
  Component,
  computed: {
    equal
  },
  inject: {
    service
  }
} = Ember;

export default Component.extend({
  layout,

  tagName: 'div',
  classNames: ['rsa-content-label'],
  classNameBindings: ['isDisabled',
    'isInverted',
    'isSmallSize',
    'isMediumSize',
    'isLargeSize',
    'isStandard',
    'isLow',
    'isMedium',
    'isHigh',
    'isDanger'
  ],

  isDisabled: false,
  isInverted: false,

  size: 'small', // ['small', 'medium', 'large']
  isSmallSize: equal('size', 'small'),
  isMediumSize: equal('size', 'medium'),
  isLargeSize: equal('size', 'large'),

  style: 'standard', // ['standard', 'low', 'medium', 'high', 'danger']
  isStandard: equal('style', 'standard'),
  isLow: equal('style', 'low'),
  isMedium: equal('style', 'medium'),
  isHigh: equal('style', 'high'),
  isDanger: equal('style', 'danger')

});
