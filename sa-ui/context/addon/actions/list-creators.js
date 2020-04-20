import * as ACTION_TYPES from './types';
import { fetchData } from './fetch';
import { warn } from '@ember/debug';
import { lookup } from 'ember-dependency-lookup';
import _ from 'lodash';
import { listData, entityType } from 'context/reducers/list/selectors';

const _intializeList = (entity) => ({ type: ACTION_TYPES.INITIALIZE_ADD_TO_LIST_PARAM, payload: entity });

const openCreateList = () => ({ type: ACTION_TYPES.TOGGLE_LIST_VIEW, payload: false });

const setErrorOnList = (error) => ({ type: ACTION_TYPES.LIST_ERROR, payload: error });

const resetError = () => ({ type: ACTION_TYPES.RESET_ERROR });

const _settingMetaInList = (list) => ({ type: ACTION_TYPES.SET_ALL_LIST, payload: list });

const _settingListProperties = (lists, entityId) => {
  if (lists) {
    return lists.map((list) => {
      if (list.resultList) {
        const metaInList = list.resultList.length > 0;
        const listObj = list.resultList.filter((listObj) => _.map(listObj.data).includes(entityId));
        return {
          id: list.datasourceId,
          name: list.dataSourceName,
          description: list.dataSourceDescription,
          enabled: metaInList,
          initialEnabled: metaInList,
          entryId: metaInList && listObj.length > 0 ? listObj.map((entry) => entry.id) : null
        };
      }
    });
  }
};

const openAddToList = (entity) => {
  return (dispatch) => {
    dispatch(_intializeList(entity));
    fetchData(
      {
        filter: [
          { field: 'meta', value: entity.type },
          { field: 'value', value: entity.id }
        ]
      },
      'list',
      false,
      ({ data }) => {
        dispatch(_settingMetaInList(_settingListProperties(data, entity.id)));
      },
      (error) => {
        const errorMessage = error ? error.message : 'context.error';
        warn(`Error processing stream call for getting list. ${ errorMessage }`, { id: 'context.components.context-panel.add-to-list.component' });
        dispatch(setErrorOnList(errorMessage));
      }
    );
  };
};

const createNewList = ({ newList }) => {
  return (dispatch) => {
    fetchData(
      {
        filter: [
          { field: 'name', value: newList.name },
          { field: 'description', value: newList.description }
        ]
      },
      'create-list',
      true,
      ({ data }) => {
        newList.id = data.id;
        dispatch({
          type: ACTION_TYPES.CREATE_LIST,
          payload: newList
        });
      },
      ({ meta }) => {
        const error = meta ? meta.message : 'context.error';
        warn(`List is not created: ${ error }`, { id: 'context.components.context-panel.add-to-list.create-list-view.component' });
        dispatch(setErrorOnList(error));
      }
    );
  };
};

const resetList = () => {
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.RESET_ERROR });
    dispatch({ type: ACTION_TYPES.TOGGLE_LIST_VIEW, payload: true });
  };
};

const _getListDataForSave = (listData) => {
  return listData.filter((listObj) => {
    return (typeof listObj.initialEnabled === 'undefined') ? listObj.enabled : (listObj.enabled != listObj.initialEnabled || !listObj.id);
  }).map((listObj) => {
    return {
      deleteEntry: listObj.initialEnabled ? listObj.entryId : [],
      id: listObj.id,
      name: listObj.name,
      description: listObj.description
    };
  });
};

const saveList = () => {
  return (dispatch, getState) => {
    const data = _getListDataForSave(listData(getState()));
    const entity = entityType(getState());
    fetchData(
      {
        filter: [
          { field: 'meta', value: entity.type },
          { field: 'value', value: entity.id },
          { field: 'data', value: data }
        ]
      },
      'save-entries',
      false,
      () => {
        dispatch(resetList());
        const eventBus = lookup('service:event-bus');
        eventBus.trigger('rsa-application-modal-close-addToList');
      },
      ({ meta }) => {
        const error = meta ? meta.message : 'context.error';
        warn(`Meta value is not saved ${ error }`, { id: 'context.components.context-panel.add-to-list.list-view.component' });
        dispatch(setErrorOnList(error));
      }
    );
  };
};

const enableNewList = (selectedRow) => {
  return (dispatch, getState) => {
    const lists = listData(getState());
    const newList = lists.map((list) => {
      if (list.name === selectedRow.name) {
        list = list.set('enabled', !selectedRow.enabled);
      }
      return list;
    });
    dispatch(_settingMetaInList(newList));
  };
};

export {
  openAddToList,
  createNewList,
  saveList,
  resetList,
  openCreateList,
  setErrorOnList,
  resetError,
  enableNewList
};
