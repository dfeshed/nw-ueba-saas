import Ember from 'ember';
import layout from '../templates/components/rsa-content-badge-icon';

export default Ember.Component.extend({

  layout,

  classNames: ['rsa-content-badge-icon'],

  classNameBindings: ['isDanger',
                      'isSuccess',
                      'isStandard',
                      'isPassive',
                      'isAlert',
                      'isWarning'],

  icon: null,

  label: null,

  style: 'standard', // ['standard', 'danger', 'warning', 'alert', 'success', 'passive']

  isStandard: Ember.computed.equal('style', 'standard'),

  isDanger: Ember.computed.equal('style', 'danger'),

  isSuccess: Ember.computed.equal('style', 'success'),

  isPassive: Ember.computed.equal('style', 'passive'),

  isAlert: Ember.computed.equal('style', 'alert'),

  isWarning: Ember.computed.equal('style', 'warning')

});