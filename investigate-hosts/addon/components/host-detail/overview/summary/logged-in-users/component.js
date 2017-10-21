import Component from 'ember-component';

export default Component.extend({

  tagName: 'hbox',

  classNames: 'col-xs-12 host-logged-in-users rsa-b-b-1 host-content__user-details',
  /**
   * Logged in user information
   * @public
   */
  user: null
});
