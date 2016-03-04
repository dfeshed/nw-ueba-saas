import Ember from 'ember';
export default Ember.Component.extend({

  tagName: 'article',

  classNames: 'spec-doc',

  classNameBindings: ['model.dataType'],

  model: null

});
