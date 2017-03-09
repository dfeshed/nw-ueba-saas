import Ember from 'ember';
import layout from './template';


const {
  Component,
  Logger,
  inject: {
    service
  },
  Object: EmberObject
} = Ember;

export default Component.extend({
  layout,
  classNames: 'rsa-context-tree-table',
  request: service(),
  eventBus: service(),
  createList: true,
  isError: false,
  errorMessage: null,
  isDisabled: false,

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

  actions: {

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
    }
  }
});
