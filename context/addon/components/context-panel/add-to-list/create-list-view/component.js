import Ember from 'ember';
import layout from './template';
import service from 'ember-service/inject';
import Component from 'ember-component';

const {
  isEmpty,
  Logger
} = Ember;

export default Component.extend({
  layout,
  request: service(),

  resetProperties() {
    this.setProperties({
      createList: true,
      name: null,
      description: null,
      isError: false,
      errorMessage: null,
      isDisabled: false
    });
  },

  createNewList(list) {
    this.set('isDisabled', true);
    this.get('request').promiseRequest({
      method: 'stream',
      modelName: 'create-list',
      query: {
        filter: [
          { field: 'name', value: list.name },
          { field: 'description', value: list.description }
        ]
      }
    }).then(({ data }) => {
      Logger.debug(`Successfully created list: ${ data }`);
      list.id = data.id;
      this.get('model.list').push(list);
      this.resetProperties();
    }).catch(({ meta }) => {
      const error = meta ? meta.message : 'admin.error';
      Logger.error(`List is not created: ${ error }`);
      this.setProperties({
        isDisabled: false,
        isError: true,
        errorMessage: this.get('i18n').t(`context.error.${error}`)
      });
    });
  },

  actions: {

    checkListName(name) {
      const rows = this.get('model.list');
      const isDuplicateName = rows.find((list) => list.name.toUpperCase() == name.toUpperCase().trim());
      this.setProperties({
        isError: !!isDuplicateName,
        errorMessage: isDuplicateName ? this.get('i18n').t('context.error.listDuplicateName') : null,
        isDisabled: !!isDuplicateName
      });
    },

    appendList() {
      const newList = {
        enabled: true,
        id: null,
        name: this.get('name'),
        description: this.get('description')
      };
      if (isEmpty(newList.name) || newList.name.length > 255) {
        this.setProperties({
          isError: true,
          errorMessage: this.get('i18n').t('context.error.listValidName'),
          isDisabled: true
        });
      }
      if (!this.get('isError') || !this.get('isDisabled')) {
        this.createNewList(newList);
      }
    },

    cancelList() {
      this.set('createList', false);
      this.resetProperties();
    }
  }

});
