import Ember from 'ember';
import layout from '../templates/components/rsa-nav-tab';

const { Component } = Ember;

export default Component.extend({

  layout,

  classNames: ['rsa-nav-tab'],

  classNameBindings: ['isActive'],

  isActive: false

});
