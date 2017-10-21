import Component from 'ember-component';

export default Component.extend({

  tagName: 'hbox',

  classNames: 'host-ip-addresses host-content__ip-details col-xs-12',
  /**
   * ipadress for display
   * @public
   */
  ipAddress: null
});
