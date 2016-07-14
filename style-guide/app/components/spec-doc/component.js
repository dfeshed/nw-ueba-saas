import Ember from 'ember';

const { Component } = Ember;

export default Component.extend({

  tagName: 'article',

  classNames: 'spec-doc',

  classNameBindings: ['model.dataType'],

  model: null

});
