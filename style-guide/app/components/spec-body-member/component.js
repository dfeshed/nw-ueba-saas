import Ember from 'ember';

const { Component } = Ember;

export default Component.extend({

  tagName: 'section',

  classNames: 'spec-body-member',

  classNameBindings: ['model.id'],

  model: null,

  spec: null
});
