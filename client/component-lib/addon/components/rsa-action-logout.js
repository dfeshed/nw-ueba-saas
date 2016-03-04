import Ember from 'ember';
import layout from '../templates/components/rsa-action-logout';

export default Ember.Component.extend({

  layout,

  classNames: ['rsa-action-logout'],

  actions: {
    /**
    * Clears user session when users logs out
    * @listens onclick of logout
    * @public
    */
    invalidateSession() {
      this.get('session').invalidate();
    }
  }

});
