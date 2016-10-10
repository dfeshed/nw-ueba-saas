import * as ACTION_TYPES from './types';

const fileSelected = (fileId) => {
  return {
    type: ACTION_TYPES.RECON_FILES_FILE_TOGGLED,
    payload: fileId
  };
};

const downloadFiles = () => {
  return (dispatch, getState) => {

    const fileIdsToDownload =
      getState().data.files.filter((f) => f.selected).map((f) => f.id);

    // eslint-disable-next-line
    console.log('GOING TO DOWNLOAD', fileIdsToDownload);

    // lot more to do here, but just wiring UI interaction for now
    dispatch({
      type: ACTION_TYPES.RECON_FILE_DOWNLOAD_SUCCESS
    });
  };
};

export {
  downloadFiles,
  fileSelected
};