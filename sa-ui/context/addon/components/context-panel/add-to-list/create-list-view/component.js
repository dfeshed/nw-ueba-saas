import { computed } from '@ember/object';
import layout from './template';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { isEmpty } from '@ember/utils';
import { addToList, resetList, createNewList, setErrorOnList, resetError } from 'context/actions/list-creators';
import { listData, isError, errorMessage, isDisabled } from 'context/reducers/list/selectors';

const stateToComputed = (state) => ({
  listData: listData(state),
  isError: isError(state),
  errorMessageKey: errorMessage(state),
  isDisabled: isDisabled(state)
});

const dispatchToActions = {
  addToList,
  resetList,
  createNewList,
  setErrorOnList,
  resetError
};

const CreateListViewComponent = Component.extend({
  layout,
  errorMessageForList: null,

  errorMessage: computed('errorMessageKey', function() {
    return this.get('i18n').t(`context.error.${this.errorMessageKey}`);
  }),

  actions: {

    checkListName(name) {
      this.send('resetError');
      const rows = this.get('listData');
      const isDuplicateName = rows.find((list) => list.name.toUpperCase() === name.toUpperCase().trim());
      if (isDuplicateName) {
        this.send('setErrorOnList', 'listDuplicateName');
      }
    },

    appendList() {
      const newList = {
        enabled: true,
        id: null,
        name: this.get('name').trim(),
        description: this.get('description')
      };
      if (isEmpty(newList.name) || newList.name.length > 255) {
        this.send('setErrorOnList', 'listValidName');
      } else {
        this.send('createNewList', { newList });
      }
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(CreateListViewComponent);
