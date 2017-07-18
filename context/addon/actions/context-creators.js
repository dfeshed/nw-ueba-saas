import * as ACTION_TYPES from './types';
import { fetchData } from './fetch';
import dataSourceMetaMap from 'context/config/dynamic-tab';
import dataSourceCoulmns from 'context/config/data-sources';

const restoreDefault = () => {
  return {
    type: ACTION_TYPES.RESTORE_DEFAULT
  };
};

const updateActiveTab = (activeTab) => {
  return {
    type: ACTION_TYPES.UPDATE_ACTIVE_TAB,
    payload: activeTab
  };
};

const updateIsConfigured = (dataSources, tab) => {
  if (tab.dataSourceType === 'Endpoint') {
    return dataSources.includes('Machines') || dataSources.includes('Modules') || dataSources.includes('IOC');
  }
  return dataSources.includes(tab.dataSourceType);
};

const updateDataSourceDetails = (tab, dataSources) => {
  if (tab.dataSourceType === 'Endpoint') {
    return {
      Machines: {
        details: dataSourceCoulmns.MACHINES,
        isConfigured: dataSources.includes('Machines')
      },
      IOC: {
        details: dataSourceCoulmns.IOC,
        isConfigured: dataSources.includes('IOC')
      },
      Modules: {
        details: dataSourceCoulmns.MODULES,
        isConfigured: dataSources.includes('Modules')
      }
    };
  }
  return dataSourceCoulmns[tab.dataSourceType.toUpperCase()];
};

const findDataSource = (dataSources, meta) => {
  return dataSourceMetaMap.find((dataSource) => {
    return dataSource.tabType === meta;
  }).columns.map((tab) => ({
    ...tab,
    isConfigured: updateIsConfigured(dataSources, tab),
    details: updateDataSourceDetails(tab, dataSources)
  }));
};
/**
 * Subscribe to notifications. Notifications tell us when any file downloads are finished/failed.
 * Eventually we will have a standalone notifications UI outside of recon, but for now it's all handled internally in recon.
 * @public
 */
const getDataSources = (meta) => {
  return (dispatch) => {
    fetchData(
      {},
      'data-sources',
      false,
      // some job has finished and is ready for download
      ({ data }) => {
        const dataSources = data.map((v) => {
          if (v.enabled) {
            return v.dataSourceGroup;
          }
        }).filter((v, i, a) => a.indexOf(v) === i);
        if (dataSources.length === 0) {
          dispatch({
            type: ACTION_TYPES.CONTEXT_ERROR,
            payload: 'context.error.noDataSource'
          });
        } else {
          dispatch({
            type: ACTION_TYPES.GET_ALL_DATA_SOURCES,
            payload: findDataSource(dataSources, meta)
          });
        }
      },
      // some job failed
      () => {
        dispatch({
          type: ACTION_TYPES.CONTEXT_ERROR,
          payload: 'context.error.dataSourcesFailed'
        });
      }
    );
  };
};

const initializeContextPanel = ({ entityId, entityType }) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.INITIALIZE_CONTEXT_PANEL,
      payload: { lookupKey: entityId, meta: entityType }
    });
    dispatch(getDataSources(entityType));
    fetchData(
      {
        filter: [
          { field: 'meta', value: entityType },
          { field: 'value', value: entityId }
        ]
      },
      'context',
      true,
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
          type: ACTION_TYPES.CONTEXT_ERROR,
          payload: 'context.error.error'
        });
      }
    );
  };
};

const getContextEntitiesMetas = ({ coreCatalog }) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.GET_CONTEXT_ENTITIES_METAS,
      payload: coreCatalog
    });
  };
};

export {
  updateActiveTab,
  getDataSources,
  initializeContextPanel,
  restoreDefault,
  getContextEntitiesMetas
};