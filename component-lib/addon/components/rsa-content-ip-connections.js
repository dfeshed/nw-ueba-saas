import Ember from 'ember';
import layout from '../templates/components/rsa-content-ip-connections';

const {
  Component,
  computed,
  computed: {
    gt,
    equal
  },
  run
} = Ember;

export default Component.extend({

  layout,

  classNames: ['rsa-content-ip-connections'],

  classNameBindings: ['multipleToIPs:multiple-ips', 'multipleFromIPs:multiple-ips', 'flow'],

  multipleToIPs: gt('toIPs.length', 1),

  multipleFromIPs: gt('fromIPs.length', 1),

  isVertical: equal('flow', 'vertical'),

  isHorizontal: equal('flow', 'horizontal'),

  flow: 'vertical', // ['vertical', 'horizontal']

  noIPs: computed('toIPs.length', 'fromIPs.length', function() {
    return (this.get('toIPs.length') === 0) && (this.get('fromIPs.length') === 0);
  }),

  toIpsId: computed(function() {
    let id = Math.random().toString();
    let formattedId = id.slice(2, id.length);
    return `toIps${formattedId}`;
  }),

  fromIpsId: computed(function() {
    let id = Math.random().toString();
    let formattedId = id.slice(2, id.length);
    return `fromIps${formattedId}`;
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
