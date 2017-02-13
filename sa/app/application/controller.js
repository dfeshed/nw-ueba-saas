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

  accessControl: service(),

  isFullyAuthenticated: computed('session.isAuthenticated', 'currentPath', function() {
    const path = this.get('currentPath');

    if (!this.get('session.isAuthenticated') || path === 'not-found') {
      return false;
    }

    const query = window.location.search;
    const isRedirecting = localStorage.getItem('_redirecting');
    return !(typeof query !== 'undefined' && query.indexOf('?next=') == 0 && isRedirecting == null && path !== 'login');
  })
});
