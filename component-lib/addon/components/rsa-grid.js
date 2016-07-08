import Ember from 'ember';
import layout from '../templates/components/rsa-grid';

export default Ember.Component.extend({

  layout,

  classNames: ['rsa-grid'],

  classNameBindings: [ 'isPageView'],

  isPageView: false

});