import Ember from 'ember';

export default Ember.Component.extend({

  tagName: 'section',

  classNames: 'spec-body-member',

  classNameBindings: ['model.id'],

  model: null,

  spec: null
});
