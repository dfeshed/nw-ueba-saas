import Ember from 'ember';
import layout from '../templates/components/rsa-content-ip-connections';

const {
  Component,
  computed,
  computed: {
    gt
  },
  run
} = Ember;

export default Component.extend({

  layout,

  classNames: ['rsa-content-ip-connections'],

  multipleToIPs: gt('toIPs.length', 1),

  multipleFromIPs: gt('fromIPs.length', 1),

  noIPs: computed('toIPs.length', 'fromIPs.length', function() {
    return (this.get('toIPs.length') === 0) || (this.get('fromIPs.length') === 0);
  }),

  toIPs: [],

  fromIPs: [],

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
