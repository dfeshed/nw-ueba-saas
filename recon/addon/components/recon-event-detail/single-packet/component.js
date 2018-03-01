import Component from '@ember/component';
import { join } from '@ember/runloop';
import { htmlSafe } from 'ember-string';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';
import computed, { not, readOnly } from 'ember-computed-decorators';

import layout from './template';

const stateToComputed = ({ recon: { packets } }) => ({
  hasSignaturesHighlighted: packets.hasSignaturesHighlighted,
  hasStyledBytes: packets.hasStyledBytes
});

const SinglePacketComponent = Component.extend({
  classNames: ['rsa-packet'],
  classNameBindings: [
    'packet.side',
    'packet.isContinuation',
    'hasStyledBytes',
    'hasSignaturesHighlighted'
  ],
  layout,
  tagName: 'section',

  viewport: service(),

  index: null,
  isPacketExpanded: true,
  packet: null,
  packetFields: null,
  selection: null,
  tooltipData: null,
  viewportEntered: false,

  @readOnly @not('viewportEntered')
  viewportExited: null,

  /**
   * Returns the calculated height of a packet body (offset and byte-tables).
   * This assumes a table row height of 17px.
   *
   * The reason we calculate this is because we originally relied upon the
   * height of the rendered offset tables to tell Spaniel how big all of our
   * packets are. Having all that extra DOM rendered for the offsets was causing
   * performance problems, so we removed the `<table>` elements from offset.
   * That then caused problems with browser zooming, so we have to add the
   * `<table>` elements back. So we don't regress on performance, we no longer
   * render out-of-view offset `<table>`s. Instead, we calculate the size that
   * it _would_ take and apply that to the packet body so that Spaniel knows
   * how large each packet is and can render an appropriate scroll bar.
   * @param {Object} rows The number of packet rows.
   * @return CSS style string to be used for `style` attribute.
   * @public
   */
  @computed('packet.byteRows')
  calculatedHeight: (rows = []) => htmlSafe(`min-height: ${rows.length * 17}px`),

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

    const el = this.get('element');
    const viewport = this.get('viewport');
    const watcher = viewport.getWatcher();
    watcher.watch(el, (e) => {
      if (e === 'exposed' && !this.get('viewportEntered')) {
        if (!this.get('isDestroying') && !this.get('isDestroyed')) {
          join(() => {
            this.set('viewportEntered', true);
          });
        }
      }
    });
  },

  willDestroyElement() {
    this._super(...arguments);
    const el = this.get('element');
    const watcher = this.get('watcher');
    watcher && watcher.unwatch(el);
  },

  actions: {
    togglePacketExpansion(isPacketExpanded) {
      this.set('isPacketExpanded', isPacketExpanded);
    }
  }
});

export default connect(stateToComputed)(SinglePacketComponent);
