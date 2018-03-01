import { set } from '@ember/object';
import { isEmpty } from '@ember/utils';
import Component from '@ember/component';
import { inject as service } from '@ember/service';
import layout from './template';
import computed from 'ember-computed-decorators';
import { debug, warn } from '@ember/debug';

export default Component.extend({
  layout,
  request: service(),
  eventBus: service(),

  active: 'all',
  resetProperties() {
    this.setProperties({
      isError: false,
      errorMessage: null,
      isDisabled: false
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

  _getListDataForSave() {
    return this.get('model.list').filter((listObj) => {
      return (typeof listObj.initialEnabled === 'undefined') ? listObj.enabled : (listObj.enabled != listObj.initialEnabled || !listObj.id);
    }).map((listObj) => {
      return {
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
      return list.filter((data) => {
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
      this.resetProperties();
    },

    saveList() {
      this.setProperties({
        isDisabled: true,
        createList: true
      });
      this.get('request').promiseRequest({
        method: 'stream',
        modelName: 'save-entries',
        query: this._getQueryParam()
      }).then(({ data }) => {
        this.resetProperties();
        this.get('eventBus').trigger('rsa-application-modal-close-addToList');
        debug(`Successfully saved: ${ data }`);
      }).catch(({ meta }) => {
        const error = meta ? meta.message : 'admin.error';
        warn(`Meta value is not saved ${ error }`);
        this.setProperties({
          isDisabled: false,
          isError: true,
          errorMessage: this.get('i18n').t(`context.error.${error}`)
        });
      });
    },

    toggleCheckbox(row) {
      const rows = this.get('model.list');
      set(rows.filterBy('name', row.name)[0], 'enabled', !row.enabled);
      this.set('model.list', rows);
    }
  }

});
