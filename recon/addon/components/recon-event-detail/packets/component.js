import Ember from 'ember';
import connect from 'ember-redux/components/connect';
import layout from './template';

const { Component } = Ember;

const stateToComputed = ({ data }) => ({
  packetFields: data.packetFields,
  packets: data.packets
});

const PacketReconComponent = Component.extend({
  layout,
  classNameBindings: [':recon-event-detail-packets']
});

export default connect(stateToComputed)(PacketReconComponent);