import * as ACTION_TYPES from './types';

const fileSelected = (fileId) => {
  return {
    type: ACTION_TYPES.FILES_FILE_TOGGLED,
    payload: fileId
  };
};

const downloadFiles = () => {
  return (dispatch, getState) => {

    const fileIdsToDownload =
      getState().recon.data.files.filter((f) => f.selected).map((f) => f.id);

    // eslint-disable-next-line
    console.log('GOING TO DOWNLOAD', fileIdsToDownload);

    // lot more to do here, but just wiring UI interaction for now
    dispatch({
      type: ACTION_TYPES.FILE_DOWNLOAD_SUCCESS
    });
  };
};

export {
  downloadFiles,
  fileSelected
};