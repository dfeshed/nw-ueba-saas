import Ember from 'ember';
import * as ACTION_TYPES from './types';
import { fetchExtractJobId } from './fetch';

const {
  A,
  Logger,
  isArray
} = Ember;

const fileSelected = (fileId) => {
  return {
    type: ACTION_TYPES.FILES_FILE_TOGGLED,
    payload: fileId
  };
};

const deselectAllFiles = () => ({ type: ACTION_TYPES.FILES_SELECT_ALL });

const selectAllFiles = () => ({ type: ACTION_TYPES.FILES_DESELECT_ALL });

const selectHeaderItem = (headerItems, item) => {
  let v;
  if (isArray(headerItems)) {
    const d = headerItems.find((hi) => hi.name === item);

    if (d && d.hasOwnProperty('value')) {
      v = d.value;
    }
  }
  return v;
};

const createFilename = (deviceName, session, files = []) => {
  let fileName = `${deviceName}_SID${session}`;
  if (files.length) {
    fileName += `_FC${files.length}`;
  }
  return fileName;
};

const extractFiles = (type = 'FILES') => {
  return (dispatch, getState) => {
    const {
      recon: {
        data: {
          endpointId,
          eventId,
          files,
          headerItems
        }
      }
    } = getState();
    const deviceName = selectHeaderItem(headerItems, 'device');
    const session = selectHeaderItem(headerItems, 'session');
    const selectedFileNames = (files || A([]))
      .filterBy('selected', true)
      .map((file) => file.fileName);
    const filename = createFilename(deviceName, session, selectedFileNames);

    dispatch({
      type: ACTION_TYPES.FILE_EXTRACT_JOB_ID_RETRIEVE,
      promise: fetchExtractJobId(endpointId, eventId, type, filename, selectedFileNames),
      meta: {
        onFailure(response) {
          Logger.error('Error fetching job id for extraction', { endpointId, eventId }, response);
        }
      }
    });
  };
};

const didDownloadFiles = () => ({ type: ACTION_TYPES.FILE_EXTRACT_JOB_DOWNLOADED });

const showPacketTooltip = (tootipData) => ({
  type: ACTION_TYPES.SHOW_PACKET_TOOLTIP,
  payload: tootipData
});

const hidePacketTooltip = () => ({ type: ACTION_TYPES.HIDE_PACKET_TOOLTIP });

export {
  deselectAllFiles,
  selectAllFiles,
  extractFiles,
  didDownloadFiles,
  fileSelected,
  showPacketTooltip,
  hidePacketTooltip
};
