import Ember from 'ember';
import layout from './template';
import { TYPES } from '../../utils/reconstruction-types';
import { equal } from 'ember-computed-decorators';
const { Component } = Ember;

export default Component.extend({
  layout,
  classNameBindings: [':recon-event-content'],
  tagName: 'fill',

  // INPUTS
  contentError: null,
  endpointId: null,
  eventId: null,
  meta: null,
  packetFields: null,
  showMetaDetails: null,
  reconstructionType: null,
  // END INPUTS

  @equal('reconstructionType', TYPES.PACKET) isPackets: null,
  @equal('reconstructionType', TYPES.FILE) isFile: null
});