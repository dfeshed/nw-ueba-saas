import Ember from 'ember';
import layout from '../templates/components/rsa-grid-row';

const { Component } = Ember;

export default Component.extend({

  layout,

  classNames: ['rsa-grid-row'],

  classNameBindings: ['collapseHeight',
                      'expandHeight'],

  fullWidth: false,

  collapseHeight: false,

  expandHeight: false

});
