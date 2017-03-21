import Ember from 'ember';

import layout from './template';
import { BYTES_PER_ROW } from 'recon/selectors/packet-selectors';

const { Component } = Ember;

export default Component.extend({
  layout,
  tagName: 'section',
  classNames: 'rsa-offset-table',

  byteRows: null,
  bytesPerRow: BYTES_PER_ROW,
  digits: 6
});
