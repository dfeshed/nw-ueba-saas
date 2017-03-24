import Ember from 'ember';
import { not, readOnly } from 'ember-computed-decorators';
import { SpanielObserver } from 'spaniel';

import layout from './template';

const { Component, run } = Ember;

export default Component.extend({
  layout,
  index: null,
  isPacketExpanded: true,
  packet: null,
  packetFields: null,
  selection: null,
  tooltipData: null,
  viewportEntered: false,
  @readOnly @not('viewportEntered') viewportExited: null,

  /**
   * The number of bytes to display closely packed together, without a blank space.
   * @type {number}
   * @public
   */
  byteGrouping: 1,

  /**
   * Configurable callback to be invoked whenever `selection` changes.
   * The callback will be passed `selection` as its single single argument.
   * @type function
   * @public
   */
  onselect() {},

  /**
   * Observe the component's this.element intersecting with the root element
   * @private
   */
  didInsertElement() {
    this._super(...arguments);
    const options = {
      rootMargin: '-1000px 0px -1000px 0px',
      threshold: [{
        ratio: 0.01,
        time: 0
      }]
    };

    const observer = new SpanielObserver(([entry]) => {
      run.join(() => {
        this.set('viewportEntered', entry.entering);
      });
    }, options);

    observer.observe(this.$('.rsa-packet')[0]);

    this.set('observer', observer);
  },

  willDestroyElement() {
    this._super(...arguments);
    this.get('observer').disconnect();
  },

  actions: {
    togglePacketExpansion(isPacketExpanded) {
      this.set('isPacketExpanded', isPacketExpanded);
    }
  }
});
