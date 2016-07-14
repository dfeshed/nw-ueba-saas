import Ember from 'ember';
import layout from '../templates/components/rsa-content-section-header';

const { Component } = Ember;

export default Component.extend({

  layout,

  tagName: 'header',

  classNames: ['rsa-content-section-header']

});