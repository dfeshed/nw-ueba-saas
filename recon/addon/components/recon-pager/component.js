import Component from 'ember-component';
import layout from './template';
import computed from 'ember-computed-decorators';

export default Component.extend({
  layout,
  tagName: 'section',
  classNames: ['recon-pager'],

  /**
   * Index of event.
   * @type Number
   * @default 0
   * @public
   */
  eventIndex: 0,

  /**
   * Total number of all events.
   * @type Number
   * @default 0
   * @public
   */
  eventTotal: 0,

  /**
   * Medium type
   * @type Boolean
   * @default false
   * @public
   */
  isPacket: false,

  /**
   * Number of rendered packets.
   * @type Number
   * @default 0
   * @public
   */
  packetCount: 0,

  /**
   * Total number of possible packets. This could be more than `packetCount` and
   * is configured in the data reducer.
   * @type Number
   * @default 0
   * @public
   * @see /reducers/data-reducers#packetsPageSize
   */
  packetTotal: 0,

  // Resolve to `true` if we have all the packets.
  @computed('packetCount', 'packetTotal')
  isHidden(total = 0, pageSize = 0) {
    return total === pageSize;
  }

});
