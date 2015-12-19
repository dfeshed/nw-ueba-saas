import Ember from 'ember';

export default Ember.Component.extend({
  tagName: 'div',
  classNames: 'color-swatch',
  classNameBindings: ['model.id'],
  model: null
});
