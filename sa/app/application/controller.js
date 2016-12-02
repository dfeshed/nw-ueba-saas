import Ember from 'ember';

const {
  Controller,
  inject: {
    service
  },
  computed
} = Ember;

export default Controller.extend({
  fatalErrors: service(),

  session: service(),

  isFullyAuthenticated: computed('session.isAuthenticated', function() {
    if (!this.get('session.isAuthenticated')) {
      return false;
    }
    const query = window.location.search;
    const isRedirecting = localStorage.getItem('_redirecting');
    return !(typeof query !== 'undefined' && query.indexOf('?next=') == 0 && isRedirecting == null);
  })
});
