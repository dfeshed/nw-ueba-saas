import { createSelector } from 'reselect';
import { RECON_VIEW_TYPES_BY_NAME } from 'recon/utils/reconstruction-types';

const _currentReconView = (recon) => recon.visuals.currentReconView;

const _typeCode = createSelector(
  [_currentReconView],
  (currentReconView) => currentReconView ? currentReconView.code : null
);

export const isRequestShown = (recon) => recon.visuals.isRequestShown;

export const isResponseShown = (recon) => recon.visuals.isResponseShown;

export const allDataHidden = createSelector(
  [isRequestShown, isResponseShown],
  (isRequestShown = true, isResponseShown = true) => !isRequestShown && !isResponseShown
);

export const isTextView = createSelector(
  _typeCode,
  (code) => code === RECON_VIEW_TYPES_BY_NAME.TEXT.code
);

export const isFileView = createSelector(
  _typeCode,
  (code) => code === RECON_VIEW_TYPES_BY_NAME.FILE.code
);

export const isPacketView = createSelector(
  _typeCode,
  (code) => code === RECON_VIEW_TYPES_BY_NAME.PACKET.code
);

export const isEmailView = createSelector(
  _typeCode,
  (code) => code === RECON_VIEW_TYPES_BY_NAME.MAIL.code
);

export const lacksPackets = createSelector(
  isPacketView,
  isTextView,
  (isPacketView, isTextView) => !isPacketView && !isTextView
);
