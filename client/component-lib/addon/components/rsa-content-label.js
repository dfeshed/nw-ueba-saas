import Ember from 'ember';
import layout from '../templates/components/rsa-content-label';

export default Ember.Component.extend({
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
  isSmallSize: Ember.computed.equal('size', 'small'),
  isMediumSize: Ember.computed.equal('size', 'medium'),
  isLargeSize: Ember.computed.equal('size', 'large'),

  style: 'standard', // ['standard', 'low', 'medium', 'high', 'danger']
  isStandard: Ember.computed.equal('style', 'standard'),
  isLow: Ember.computed.equal('style', 'low'),
  isMedium: Ember.computed.equal('style', 'medium'),
  isHigh: Ember.computed.equal('style', 'high'),
  isDanger: Ember.computed.equal('style', 'danger')

});
