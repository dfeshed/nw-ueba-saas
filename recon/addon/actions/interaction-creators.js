import Ember from 'ember';
import * as ACTION_TYPES from './types';
import { fetchFileExtractJobId } from './fetch';

const {
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

const createFilename = (deviceName, session, files) => {
  let fn;
  if (deviceName && session && isArray(files)) {
    fn = `${deviceName}_SID${session}_FC${files.length}`;
  }
  return fn;
};

const downloadFiles = () => {
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

    const selectedFileNames = (files || [])
      .filterBy('selected', true)
      .map((file) => file.fileName);

    const deviceName = selectHeaderItem(headerItems, 'device');
    const session = selectHeaderItem(headerItems, 'session');
    const filename = createFilename(deviceName, session, selectedFileNames);

    dispatch({
      type: ACTION_TYPES.FILE_EXTRACT_JOB_ID_RETRIEVE,
      promise: fetchFileExtractJobId(endpointId, eventId, selectedFileNames, filename),
      meta: {
        onFailure(response) {
          Logger.error('Error fetching job id for file extraction', { endpointId, eventId }, response);
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
  downloadFiles,
  didDownloadFiles,
  fileSelected,
  showPacketTooltip,
  hidePacketTooltip
};
