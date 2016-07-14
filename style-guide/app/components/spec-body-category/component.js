import Ember from 'ember';

const { Component } = Ember;

export default Component.extend({

  tagName: 'section',

  classNames: 'spec-body-category',

  classNameBindings: ['model.id'],

  model: null,

  spec: null
});
