import Immutable from 'seamless-immutable';

import { RECON_VIEW_TYPES_BY_NAME } from 'recon/utils/reconstruction-types';

const _set = (obj, key, val) => {
  if (obj[key]) {
    obj[key] = val;
    return;
  }

  const keys = key.split('.');
  const firstKey = keys.shift();

  if (!obj[firstKey]) {
    obj[firstKey] = {};
  }

  if (keys.length === 0) {
    obj[firstKey] = val;
    return;
  } else {
    _set(obj[firstKey], keys.join('.'), val);
  }
};

export default class DataHelper {
  constructor(setState) {
    this.state = {};
    this.setState = setState;
  }

  // Trigger setState, also return the resulting state
  // in case it needs to be used/checked
  build() {
    const state = Immutable.from({ recon: this.state });
    this.setState(state);
    return state.asMutable();
  }

  // Data

  isStandalone(setTo) {
    _set(this.state, 'data.isStandalone', setTo);
    return this;
  }

  // META

  meta(meta) {
    _set(this.state, 'meta.meta', meta);
    return this;
  }

  isLogEvent() {
    this.meta([['medium', 32]]);
    return this;
  }

  isNetworkEvent() {
    this.meta([['medium', 1]]);
    return this;
  }

  isEndpointEvent() {
    this.meta([['nwe.callback_id', 'foo']]);
    return this;
  }

  // HEADER

  headerItems(items) {
    _set(this.state, 'header.headerItems', items);
    return this;
  }

  packetTotal(value) {
    this.headerItems([{ name: 'packetCount', key: '', type: 'UInt64', value, id: 'packetCount' }]);
    return this;
  }

  // VISUALS

  isMetaShown(setTo) {
    _set(this.state, 'visuals.isMetaShown', setTo);
    return this;
  }

  isHeaderOpen(setTo) {
    _set(this.state, 'visuals.isHeaderOpen', setTo);
    return this;
  }

  isReconExpanded(setTo) {
    _set(this.state, 'visuals.isReconExpanded', setTo);
    return this;
  }

  isReconOpen(val) {
    _set(this.state, 'visuals.isReconOpen', val);
    return this;
  }

  isRequestShown(val) {
    _set(this.state, 'visuals.isRequestShown', val);
    return this;
  }

  isResponseShown(val) {
    _set(this.state, 'visuals.isResponseShown', val);
    return this;
  }

  _onView(view) {
    _set(this.state, 'visuals.currentReconView', view);
  }

  isTextView() {
    this._onView(RECON_VIEW_TYPES_BY_NAME.TEXT);
    return this;
  }

  isPacketView() {
    this._onView(RECON_VIEW_TYPES_BY_NAME.PACKET);
    return this;
  }

  isFileView() {
    this._onView(RECON_VIEW_TYPES_BY_NAME.FILE);
    return this;
  }

  // Packets

  packetPageSize(setTo) {
    _set(this.state, 'packets.packetsPageSize', setTo);
    return this;
  }

  apiFatalErrorCode(code) {
    _set(this.state, 'data.apiFatalErrorCode', code);
    return this;
  }
}
