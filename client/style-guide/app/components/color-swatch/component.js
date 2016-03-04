import Ember from 'ember';

export default Ember.Component.extend({

  classNames: 'color-swatch',

  classNameBindings: ['model.id'],

  model: null

});
