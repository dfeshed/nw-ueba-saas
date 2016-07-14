import Ember from 'ember';

const {
  Component,
  computed
} = Ember;

export default Component.extend({

  tagName: 'nav',

  classNames: 'spec-nav',

  model: null,

  /**
   * Returns the current browser URL's path, without hostname prefix & hash suffix (e.g, "/comp/button").
   * Used for constructing links to anchors in the sub-sections of the spec DOM.
   * @public
   */
  baseUrl: computed('model', function() {
    return window.location.pathname;
  })
});
