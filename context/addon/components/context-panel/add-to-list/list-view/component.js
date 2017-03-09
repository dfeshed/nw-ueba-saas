import Ember from 'ember';
import layout from './template';
import computed from 'ember-computed-decorators';

const {
  set,
  isEmpty,
  Component,
  Logger,
  inject: {
    service
  }
} = Ember;

export default Component.extend({
  layout,
  request: service(),
  eventBus: service(),

  active: 'all',

  _getQueryParam() {
    const { entityId, entityType } = this.getProperties('entityId', 'entityType');
    return { filter: [
      { field: 'meta', value: entityType },
      { field: 'value', value: entityId },
      { field: 'data', value: this._getListDataForSave() }
    ] };
  },

  _getListDataForSave() {
    return this.get('model.list').filter((listObj) => {
      return listObj.enabled != listObj.initialEnabled || !listObj.id;
    }).map((listObj) => {
      return {
        isNew: !listObj.id,
        deleteEntry: listObj.initialEnabled ? listObj.entryId : [],
        id: listObj.id,
        name: listObj.name,
        description: listObj.description
      };
    });
  },

  @computed('model.filterStr', 'model.list')
  getFilteredList(filterStr, list) {
    if (isEmpty(filterStr)) {
      return list;
    } else {
      const filterStrCaps = filterStr.toUpperCase();
      return list.filter((data) =>{
        const name = data.name.toUpperCase().match(filterStrCaps);
        const desc = data.description && data.description.toUpperCase().match(filterStrCaps);
        return name || desc;
      });
    }
  },

  actions:
  {
    createList() {
      this.set('createList', false);
    },

    activate(option) {
      this.set('active', option);
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

    toggleCheckbox(row) {
      const rows = this.get('model.list');
      set(rows.filterBy('name', row.name)[0], 'enabled', !row.enabled);
      this.set('model.list', rows);
    }
  }

});
