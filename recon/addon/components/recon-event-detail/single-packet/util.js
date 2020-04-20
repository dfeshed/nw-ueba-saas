const ROW_HEIGHT = 18; // 12px + 1px padding(2px) + 1px border(2px) + 1px margin(2px)

/**
 * This function is used to determine if all of the bytes within the packet
 * can render on screen. If there are more bytes than can fit, we chop the
 * bytes up into portions that will fit the screen real estate.
 *
 * We use bytes instead of byte rows because, as packets are returned from the
 * server, a row could be partially filled. This doesn't cause a problem when
 * rendering individual packets, but if you enable the "payload only" option,
 * it could result in a half-filled row.
 *
 * @param {number} height - The viewport height. This is the space available to
 * display all packet bytes.
 * @param {Object} packet - The packet.
 * @param {number} packetByteCount - Total number of packet bytes rendered so
 * far.
 * @return {Object} Contains two properties, `chunkedPacket` and
 * `packetByteCount`.
 */
const determineVisibleBytes = (height, packet, packetByteCount) => {
  const { bytes, byteRows } = packet;
  const bytesLength = bytes.length;
  const byteRowsLength = byteRows.length;
  // The max number of packet rows that could be visible on screen given that
  // each row is "ROW_HEIGHT" tall
  const maxVisibleRows = height / ROW_HEIGHT;
  // The total number of bytes that can be rendered in the viewport. There
  // are 16 bytes per row
  const maxVisibleBytes = maxVisibleRows * 16;
  const moreIncomingBytes = bytesLength > packetByteCount;
  const moreBytesThanVisibleSpace = bytesLength > maxVisibleBytes;
  let chunkedPacket;

  if (moreIncomingBytes && moreBytesThanVisibleSpace) {
    let rowStart = 0;
    const packetChunks = [];
    // Iterate through the packet's byteRows and create new "packets" where
    // total byteRows are limited to maxVisibleRows
    while (rowStart < byteRowsLength) {
      const rowEnd = rowStart + maxVisibleRows;
      // All byte-table cares about is byteRows and id, so pull just those out
      const chunk = {
        byteRows: byteRows.slice(rowStart, rowEnd),
        id: packet.id
      };
      packetChunks.push(chunk);
      rowStart = rowEnd;
    }
    chunkedPacket = packetChunks;
  } else {
    // All byteRows will render, so just pass the packet along
    chunkedPacket = [packet];
  }

  return {
    chunkedPacket,
    packetByteCount: bytesLength
  };
};

export {
  ROW_HEIGHT,
  determineVisibleBytes
};