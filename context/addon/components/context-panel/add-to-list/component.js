import Ember from 'ember';
import layout from './template';
import computed from 'ember-computed-decorators';


const {
  set,
  Component,
  Logger,
  inject: {
    service
  },
  Object: EmberObject,
  isEmpty: isEmpty
} = Ember;

export default Component.extend({
  layout,
  classNames: 'rsa-context-tree-table',
  active: 'all',
  request: service(),
  eventBus: service(),
  createList: true,
  isError: false,
  errorMessage: null,
  isDisabled: false,

  @computed('model.filterStr', 'model.list')
  getFilteredList(filterStr) {
    const model = this.get('model');
    if (!model) {
      return [];
    }
    if (isEmpty(filterStr)) {
      return model.list;
    } else {
      return model.list.filter((data) => data.name.toUpperCase().match(filterStr.toUpperCase()) ||
                               data.description.toUpperCase().indexOf(filterStr.toUpperCase()) > -1);
    }
  },

  _populateListData(listData, entityId) {
    const listModels = EmberObject.create({
      filterStr: null,
      list: this._buildListData(listData, entityId)
    });
    this.set('model', listModels);
  },

  _buildListData(listData, entityId) {
    return listData.map((list) => {
      const metaInList = list.resultList && list.resultList.length > 0;
      const listObj = list.resultList.filter((listObj) => Object.values(listObj.data).contains(entityId));
      return {
        id: list.datasourceId,
        name: list.dataSourceName,
        description: list.dataSourceDescription,
        enabled: metaInList,
        initialEnabled: metaInList,
        entryId: metaInList && listObj.length > 0 ? listObj.map((entry) => entry.id) : null
      };
    });
  },

  _getListDataForSave() {
    return this.get('model.list').filter((listObj) => {
      return listObj.enabled != listObj.initialEnabled || !listObj.id;
    }).map((listObj) => {
      return {
        isNew: listObj.id ? false : true,
        deleteEntry: listObj.initialEnabled ? listObj.entryId : [],
        id: listObj.id,
        name: listObj.name,
        description: listObj.description
      };
    });
  },

  _getQueryParam() {
    const { entityId, entityType } = this.getProperties('entityId', 'entityType');
    return { filter: [
      { field: 'meta', value: entityType },
      { field: 'value', value: entityId },
      { field: 'data', value: this._getListDataForSave() }
    ] };
  },

  actions: {

    createList() {
      this.set('createList', false);
    },

    /*
     * getting complete list and its details
     * @private
     */

    getAllList() {
      const { entityId, entityType } = this.getProperties('entityId', 'entityType');
      this.set('createList', true);
      this.get('request').streamRequest({
        method: 'stream',
        modelName: 'list',
        query: {
          filter: [
            { field: 'meta', value: entityType },
            { field: 'value', value: entityId }
          ]
        },
        streamOptions: { requireRequestId: false },
        onResponse: ({ data }) => {
          this.set('hasResponse', true);
          this._populateListData(data, entityId);
        },
        onError: (response) => {
          if (this.get('hasResponse') === true) {
            return;
          }
          Logger.error('Error processing stream call for context lookup.', response);
        }
      });
    },

    toggleCheckbox(row) {
      const rows = this.get('model.list');
      set(rows.filterBy('name', row.name)[0], 'enabled', !row.enabled);
      this.set('model.list', rows);
    },

    closeList() {
      this.get('eventBus').trigger('rsa-application-modal-close-addToList');
    },

    saveList() {
      this.set('createList', true);
      this.get('request').promiseRequest({
        method: 'stream',
        modelName: 'save-entries',
        query: this._getQueryParam()
      }).then((response) => {
        this.get('eventBus').trigger('rsa-application-modal-close-addToList');
        Logger.debug(`Successfully saved: ${ response }`);
      }).catch(() => {
        Logger.error('list was not saved.');
      });
    },

    activate(option) {
      this.set('active', option);
    }
  }
});
