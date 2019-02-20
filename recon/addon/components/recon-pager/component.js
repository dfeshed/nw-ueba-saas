
import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { pageFirst, changePacketsPerPage } from 'recon/actions/data-creators';
import { packetTotal } from 'recon/reducers/header/selectors';
import { thousandFormat } from 'component-lib/utils/numberFormats';

const stateToComputed = ({ recon, recon: { packets } }) => ({
  packetsPageSize: packets.packetsPageSize,
  packetTotal: packetTotal(recon)
});

const dispatchToActions = {
  pageFirst,
  changePacketsPerPage
};

const reconPagerComponent = Component.extend({
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
   * Whether or not this pager is for packets
   * @type Boolean
   * @default false
   * @public
   */
  isPacket: false,

  /**
   * Whether or not this pager is for text
   * @type Boolean
   * @default false
   * @public
   */
  isText: false,

  // Resolve to `true` if we have all the packets.
  @computed('packetCount', 'packetTotal')
  isHidden(total = 0, pageSize = 0) {
    return total === pageSize;
  },

  // Format count with comma delimiters.
  @computed('eventTotal')
  formattedEventTotal(eventTotal) {
    return thousandFormat(eventTotal);
  },

  // Format count with comma delimiters.
  @computed('eventIndex')
  formattedEventIndex(eventIndex) {
    return thousandFormat(eventIndex);
  }

});

export default connect(stateToComputed, dispatchToActions)(reconPagerComponent);