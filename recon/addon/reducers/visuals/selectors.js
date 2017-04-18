import reselect from 'reselect';

import { RECON_VIEW_TYPES_BY_NAME } from 'recon/utils/reconstruction-types';

const { createSelector } = reselect;

const typeCode = (recon) => recon.visuals.currentReconView.code;

export const isTextView = createSelector(
  typeCode,
  (code) => code === RECON_VIEW_TYPES_BY_NAME.TEXT.code
);

export const isFileView = createSelector(
  typeCode,
  (code) => code === RECON_VIEW_TYPES_BY_NAME.FILE.code
);

export const isPacketView = createSelector(
  typeCode,
  (code) => code === RECON_VIEW_TYPES_BY_NAME.PACKET.code
);

export const lacksPackets = createSelector(
  isPacketView,
  isTextView,
  (isPacketView, isTextView) => !isPacketView && !isTextView
);