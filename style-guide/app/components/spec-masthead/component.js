import Ember from 'ember';

const { Component, computed, String: {htmlSafe} } = Ember;

export default Component.extend({

  tagName: 'header',

  classNames: 'spec-masthead',

  model: null,

  description: computed('model.description', function() {
    return htmlSafe(this.get('model.description'));
  })

});
