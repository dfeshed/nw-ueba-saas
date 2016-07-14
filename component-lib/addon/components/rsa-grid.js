import Ember from 'ember';
import layout from '../templates/components/rsa-grid';

const { Component } = Ember;

export default Component.extend({

  layout,

  classNames: ['rsa-grid'],

  classNameBindings: [ 'isPageView'],

  isPageView: false

});