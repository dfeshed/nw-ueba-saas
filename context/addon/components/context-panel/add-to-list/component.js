import Ember from 'ember';
import layout from './template';
import computed from 'ember-computed-decorators';

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

  /**
   * The type and id of the entity which is to be added to a list.
   * @type {{type: String, id: String}}
   * @public
   */
  @computed
  entity: {
    get() {
      return this._entity;
    },
    set(value) {
      const { type, id } = value || {};
      const was = this._entity;
      const { type: wasType, id: wasId } = was || {};
      const changed = (type !== wasType) || (id !== wasId);

      // If entity has changed, request new server data for it.
      if (changed) {
        this._entity = value;
        this._getAllList(value);
      }
      return this._entity;
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
      const listObj = list.resultList.filter((listObj) => Object.values(listObj.data).includes(entityId));
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

  /*
   * getting complete list and its details
   * @private
   */
  _getAllList(entity) {
    const { id: entityId, type: entityType } = entity || {};
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
});
