import { module, test } from 'qunit';
import {
  ROW_HEIGHT,
  determineVisibleBytes
} from 'recon/components/recon-event-detail/single-packet/util';

module('Unit | Component | Single Packet | Util');

// Packet props
const BYTES_PER_ROW = 16;
const BYTE_ROWS = 4;
const TOTAL_BYTES = BYTES_PER_ROW * BYTE_ROWS;

// Viewport sizes
const FULL_HEIGHT = ROW_HEIGHT * BYTE_ROWS;
const HALF_HEIGHT = FULL_HEIGHT * 0.5;

// Create data for determineVisibleBytes. It's OK that the arrays are empty
// since we only use Array.length.
const createData = () => {
  const totalBytes = TOTAL_BYTES;
  const rows = new Array(BYTE_ROWS);
  return {
    bytes: new Array(totalBytes),
    byteRows: rows.map((row) => row.push(new Array(BYTES_PER_ROW)))
  };
};

const packet = {
  ...createData(),
  id: 1234
};

test('determineVisibleBytes shows full packet for large viewport', function(assert) {
  const { chunkedPacket, packetByteCount } = determineVisibleBytes(FULL_HEIGHT, packet, 0);
  assert.equal(chunkedPacket.length, 1, 'Full packet returned');
  assert.equal(chunkedPacket[0].byteRows.length, BYTE_ROWS, 'Four rows of data');
  assert.equal(packetByteCount, TOTAL_BYTES, 'correct number of total bytes');
});

test('determineVisibleBytes shows partial packet for small viewport', function(assert) {
  const { chunkedPacket, packetByteCount } = determineVisibleBytes(HALF_HEIGHT, packet, 0);
  assert.equal(chunkedPacket.length, 2, 'Two packets returned');
  assert.equal(chunkedPacket[0].byteRows.length, BYTE_ROWS * 0.5, 'Half number of rows');
  assert.equal(chunkedPacket[1].byteRows.length, BYTE_ROWS * 0.5, 'Half number of rows');
  assert.equal(packetByteCount, TOTAL_BYTES, 'correct number of total bytes');
});