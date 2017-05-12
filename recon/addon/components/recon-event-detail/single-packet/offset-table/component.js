import Component from 'ember-component';
import layout from './template';
import { BYTES_PER_ROW } from 'recon/reducers/packets/util';

export default Component.extend({
  layout,
  tagName: 'section',
  classNames: 'rsa-offset-table',
  byteRows: null,
  bytesPerRow: BYTES_PER_ROW,
  digits: 6
});
