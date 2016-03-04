import Ember from 'ember';
import layout from '../templates/components/rsa-content-badge-score';

export default Ember.Component.extend({

  layout,

  classNames: ['rsa-content-badge-score'],

  classNameBindings: ['hideLabel',
                      'isDanger:is-danger:is-standard'],

  score: null,

  label: null,

  style: 'standard', // ['standard', 'danger']

  isDanger: Ember.computed.equal('style', 'danger'),

  hideLabel: Ember.computed.not('label')

});