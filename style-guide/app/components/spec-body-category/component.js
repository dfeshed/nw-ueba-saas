import Ember from 'ember';

export default Ember.Component.extend({

  tagName: 'section',

  classNames: 'spec-body-category',

  classNameBindings: ['model.id'],

  model: null,

  spec: null
});
