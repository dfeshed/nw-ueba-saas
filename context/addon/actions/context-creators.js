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
 * This method will be called from initializeContextPanel for pulling data sources details.
 * @private
 */
const _getDataSources = (meta) => {
  return (dispatch) => {
    fetchData(
      {},
      'data-sources',
      false,
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
      ({ meta }) => {
        const error = (meta && meta.message) ? meta.message : 'admin.error';
        dispatch({
          type: ACTION_TYPES.CONTEXT_ERROR,
          payload: `context.error.${error}`
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
    dispatch(_getDataSources(entityType));
    fetchData(
      {
        filter: [
          { field: 'meta', value: entityType },
          { field: 'value', value: entityId }
        ]
      },
      'context',
      true,
      ({ data }) => {
        dispatch({
          type: ACTION_TYPES.GET_LOOKUP_DATA,
          payload: data
        });
      },
      ({ meta }) => {
        const error = (meta && meta.message) ? meta.message : 'admin.error';
        dispatch({
          type: ACTION_TYPES.CONTEXT_ERROR,
          payload: `context.error.${error}`
        });
      }
    );
  };
};

const getContextEntitiesMetas = ({ data }) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.GET_CONTEXT_ENTITIES_METAS,
      payload: data
    });
  };
};

export {
  updateActiveTab,
  initializeContextPanel,
  restoreDefault,
  getContextEntitiesMetas
};
