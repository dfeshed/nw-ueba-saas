import * as ACTION_TYPES from './types';
import { fetchData } from './fetch';

const updateActiveTab = (activeTab) => {
  return {
    type: ACTION_TYPES.UPDATE_ACTIVE_TAB,
    payload: activeTab
  };
};

/**
 * Subscribe to notifications. Notifications tell us when any file downloads are finished/failed.
 * Eventually we will have a standalone notifications UI outside of recon, but for now it's all handled internally in recon.
 * @public
 */
const getDataSources = () => {
  return (dispatch) => {
    fetchData(
      {},
      'data-sources',
      // some job has finished and is ready for download
      ({ data }) => {
        const dataSources = data.map((v) => {
          if (v.enabled) {
            return v.dataSourceGroup;
          }
        }).filter((v, i, a) => a.indexOf(v) === i);
        dispatch({
          type: ACTION_TYPES.GET_ALL_DATA_SOURCES,
          payload: dataSources
        });
      },
      // some job failed
      () => {
        dispatch({
          type: ACTION_TYPES.GET_ALL_DATA_SOURCES,
          payload: 'context.error.error'
        });
      }
    );
  };
};

const initializeContextPanel = ({ entityId, entityType }) => {
  return (dispatch) => {
    dispatch(getDataSources());
    fetchData(
      {
        filter: [
          { field: 'meta', value: entityType },
          { field: 'value', value: entityId }
        ]
      },
      'context',
      // some job has finished and is ready for download
      ({ data }) => {
        dispatch({
          type: ACTION_TYPES.GET_LOOKUP_DATA,
          payload: data
        });
      },
      // some job failed
      () => {
        dispatch({
          type: ACTION_TYPES.GET_LOOKUP_DATA,
          payload: 'error'
        });
      }
    );
  };
};

export {
  updateActiveTab,
  getDataSources,
  initializeContextPanel
};