import * as ACTION_TYPES from './types';

export const serviceSelected = (serviceId) => ({
  type: ACTION_TYPES.SERVICE_SELECTED,
  payload: serviceId
});

export const setMetaPanelSize = (size) => ({
  type: ACTION_TYPES.SET_META_PANEL_SIZE,
  payload: size
});

export const setReconPanelSize = (size) => ({
  type: ACTION_TYPES.SET_RECON_PANEL_SIZE,
  payload: size
});
