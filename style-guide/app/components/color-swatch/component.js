import Ember from 'ember';

const { Component } = Ember;

export default Component.extend({

  classNames: 'color-swatch',

  classNameBindings: ['model.id'],

  model: null

});
