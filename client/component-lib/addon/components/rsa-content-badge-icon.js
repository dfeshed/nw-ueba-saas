import Ember from 'ember';
import layout from '../templates/components/rsa-content-badge-icon';

export default Ember.Component.extend({

  layout,

  classNames: ['rsa-content-badge-icon'],

  classNameBindings: ['isLow',
    'isMedium',
    'isPassive',
    'isHigh',
    'isDanger'],

  icon: null,

  label: null,

  style: null, // ['low', 'medium', 'passive', 'high', 'danger']

  isLow: Ember.computed.equal('style', 'low'),
  isMedium: Ember.computed.equal('style', 'medium'),
  isPassive: Ember.computed.equal('style', 'passive'),
  isHigh: Ember.computed.equal('style', 'high'),
  isDanger: Ember.computed.equal('style', 'danger')
});