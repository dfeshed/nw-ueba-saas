import Ember from 'ember';

const { Component } = Ember;

export default Component.extend({

  classNames: 'color-swatch',

  classNameBindings: ['backgroundClass'],

  title: null,

  color: null,

  hex: null,

  sass: null

});
