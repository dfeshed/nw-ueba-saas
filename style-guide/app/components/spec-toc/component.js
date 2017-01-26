import Ember from 'ember';
const { Component } = Ember;

export default Component.extend({
  classNames: ['spec-toc'],

  /*
   * The offset to add to the scrollTo
   */
  offset: undefined,

  actions: {
    scrollTo(selector) {
      this.scrollTo(selector, this.get('offset'));
    }
  }
});
