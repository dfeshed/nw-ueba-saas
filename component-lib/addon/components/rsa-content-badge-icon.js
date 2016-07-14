import Ember from 'ember';
import layout from '../templates/components/rsa-content-badge-icon';

const {
  Component,
  computed
} = Ember;

export default Component.extend({

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

  isLow: computed.equal('style', 'low'),
  isMedium: computed.equal('style', 'medium'),
  isPassive: computed.equal('style', 'passive'),
  isHigh: computed.equal('style', 'high'),
  isDanger: computed.equal('style', 'danger')
});