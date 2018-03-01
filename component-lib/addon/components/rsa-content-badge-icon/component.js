import Component from '@ember/component';
import computed from 'ember-computed';
import layout from './template';

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
