import Ember from 'ember';

export default Ember.Controller.extend({
  queryParams: ['mode'],
  mode: localStorage.getItem('rsa-respond-default-page') || 'card',
  isCardMode: Ember.computed.equal('mode', 'card')
});