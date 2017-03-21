import Ember from 'ember';
import computed, { not, readOnly } from 'ember-computed-decorators';
import { SpanielObserver } from 'spaniel';

import layout from './template';

const { Component, K, run } = Ember;

export default Component.extend({
  layout,
  index: null,
  packet: null,
  packetFields: null,
  packetIsExpanded: true,
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
  onselect: K,

  /**
   * Determine the direction, request or response, for the arrow
   * @param side Request or response
   * @returns {string} right or left
   * @public
   */
  @computed('packet.side')
  arrowDirection(side) {
    return side === 'request' ? 'right' : 'left';
  },

  /**
   * Determine the expand/collapse arrow direction for a single packet
   * @param packetIsExpanded If expanded or not
   * @returns {string} down or right
   * @public
   */
  @computed('packetIsExpanded')
  collapseArrowDirection(packetIsExpanded) {
    return packetIsExpanded ? 'down' : 'right';
  },

  /**
   * Observe the component's this.element intersecting with the root element
   * @private
   */
  didInsertElement() {
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
    this.get('observer').disconnect();
  },

  actions: {
    expandPacket() {
      this.toggleProperty('packetIsExpanded');
    }
  }
});
