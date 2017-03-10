import Ember from 'ember';
import layout from './template';

const {
  isEmpty,
  Component
} = Ember;

export default Component.extend({
  layout,

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
          errorMessage: this.get('i18n').t('context.error.listValidName')
        });
      }
      if (!this.get('isError')) {
        this.get('model.list').push(newList);
        this.resetProperties();
      }
    },

    cancelList() {
      this.set('createList', false);
      this.resetProperties();
    }
  }

});
