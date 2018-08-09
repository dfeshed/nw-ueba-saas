import * as FLAGS from './nw-flags';

/**
 * @private
 * @param {*} num - The flags in their compressed form
 */
export default (num) => {
  const result = {};
  if (num & FLAGS.ERROR) {
    result.error = true;
    result.errorCode = num & 0x0000FFFF;
    return result;
  }
  result.complete = (num & FLAGS.COMPLETE) > 0;
  result.partial = (num & FLAGS.PARTIAL) > 0;

  result.dataType = (num & FLAGS.DATA_TYPE_MASK);

  result.cancel = (num & FLAGS.CANCEL) > 0;
  result.monitor = (num & FLAGS.MONITOR) > 0;
  result.nodeAdded = (num & FLAGS.NODE_ADDED) > 0;
  result.nodeDeleted = (num & FLAGS.NODE_DELETED) > 0;
  result.bufferCrc = (num & FLAGS.BUFFER_CRC) > 0;
  result.statusUpdate = (num & FLAGS.STATUS_UPDATE) > 0;
  result.response = (num & FLAGS.RESPONSE) > 0;

  return result;
};