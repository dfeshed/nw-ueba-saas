import Component from '@ember/component';
import { equal, gt } from 'ember-computed';
import { htmlSafe } from '@ember/string';
import { isPresent } from '@ember/utils';
import { run } from '@ember/runloop';
import layout from '../templates/components/rsa-content-ip-connections';
import computed from 'ember-computed-decorators';

export default Component.extend({
  layout,

  classNames: ['rsa-content-ip-connections'],

  classNameBindings: ['multipleToIPs:multiple-ips', 'multipleFromIPs:multiple-ips', 'flow'],

  multipleToIPs: gt('toIPs.length', 1),

  multipleFromIPs: gt('fromIPs.length', 1),

  isVertical: equal('flow', 'vertical'),

  isHorizontal: equal('flow', 'horizontal'),

  flow: 'vertical', // ['vertical', 'horizontal']

  showPlaceHolder: false,

  placeHolder: htmlSafe('&ndash;'),

  toIPs: [],

  fromIPs: [],

  @computed('toIPs', 'showPlaceHolder')
  showToIP: (toIPs, showPlaceHolder) => showPlaceHolder || isPresent(toIPs),

  @computed('fromIPs', 'showPlaceHolder')
  showFromIP: (fromIPs, showPlaceHolder) => showPlaceHolder || isPresent(fromIPs),

  @computed('showToIP', 'showFromIP')
  showArrow: (showToIP, showFromIP) => showToIP && showFromIP,

  @computed()
  toIpsId() {
    return this._createId('toIps');
  },

  @computed()
  fromIpsId() {
    return this._createId('fromIps');
  },

  /**
   * Generate unique numeric ID and concatenate with a given prefix.
   * @param prefix
   * @returns {string}
   * @private
   */
  _createId(prefix) {
    const id = Math.random().toString();
    const formattedId = id.slice(2, id.length);
    return `${prefix}${formattedId}`;
  },

  didInsertElement() {
    run.schedule('afterRender', this, function() {
      if (window.Clipboard) {
        this.clipboard = new window.Clipboard('.js-clipboard-trigger');
      }
    });
  },

  /**
   * Teardown for the onclick listener in Clipboard JS.
   * @public
   */
  willDestroyElement() {
    if (this.clipboard) {
      this.clipboard.destroy();
      this.clipboard = null;
    }
  }

});
