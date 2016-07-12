import Ember from 'ember';

export default Ember.Component.extend({

  tagName: 'nav',

  classNames: 'spec-nav',

  model: null,

  /**
   * Returns the current browser URL's path, without hostname prefix & hash suffix (e.g, "/comp/button").
   * Used for constructing links to anchors in the sub-sections of the spec DOM.
   * @public
   */
  baseUrl: Ember.computed('model', function() {
    return window.location.pathname;
  })
});
