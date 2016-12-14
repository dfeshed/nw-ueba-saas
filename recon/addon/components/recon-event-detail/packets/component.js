import Ember from 'ember';
import connect from 'ember-redux/components/connect';
import layout from './template';

const { Component } = Ember;

const stateToComputed = ({ recon: { data, visuals } }) => ({
  packetFields: data.packetFields,
  packets: data.packets,
  pageSize: data.packetsPageSize,
  isRequestShown: visuals.isRequestShown,
  isResponseShown: visuals.isResponseShown,
  tooltipData: visuals.packetTooltipData
});

const PacketReconComponent = Component.extend({
  layout,
  classNameBindings: [':recon-event-detail-packets']
});

export default connect(stateToComputed)(PacketReconComponent);
