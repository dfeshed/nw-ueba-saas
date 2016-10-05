/* eslint-disable */

'use strict';

module.exports = {
  name: 'streaming-data',

  /**
   * Allows live-reloading when this addon changes even when being served by another projects `ember serve`.
   * @see https://github.com/ember-cli/ember-cli/blob/master/ADDON_HOOKS.md#isdevelopingaddon
   * @public
   */
  isDevelopingAddon: function() {
    return true;
  },
  included: function(app) {
    this._super.included.apply(this, arguments);

    // Websocket libraries: SockJS & STOMP
    app.import(app.bowerDirectory + "/sockjs-client/dist/sockjs.js");
    app.import(app.bowerDirectory + "/stomp-websocket/lib/stomp.js");

    this.eachAddonInvoke('included', arguments);
  },
  init: function() {
    this._super.init && this._super.init.apply(this, arguments);
    this.options = this.options || {};
    this.options.babel = this.options.babel || {};
    this.options.babel.stage = 0;
  }
};