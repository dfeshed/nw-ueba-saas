import Component from 'ember-component';
import { connect } from 'ember-redux';
import injectService from 'ember-service/inject';

import {
  addSystemFilter,
  setSystemFilterFlag,
  deleteFilter
} from 'investigate-files/actions/data-creators';

const stateToComputed = ({ files }) => ({
  isFilterReset: files.filter.isFilterReset,
  filesFilters: files.filter.fileFilters,
  isSystemFilter: files.filter.isSystemFilter
});
const dispatchToActions = {
  addSystemFilter,
  setSystemFilterFlag,
  deleteFilter
};

const CustomFilterList = Component.extend({
  tagName: 'ul',

  classNames: ['filter-list'],

  activeFilter: null,

  flashMessage: injectService(),

  actions: {
    applyCustomFilter(filter) {
      const { criteria: { expressionList } } = filter;
      const filterId = filter.id;
      this.set('activeFilter', filterId);
      this.send('setSystemFilterFlag', false);
      this.send('addSystemFilter', expressionList);
    },
    deleteSelectedFilter(id) {
      const callbackOptions = {
        onSuccess: () => {
          this.get('flashMessage').showFlashMessage('investigateFiles.filter.customFilters.delete.successMessage');
        },
        onFailure: ({ meta: message }) => this.get('flashMessage').showErrorMessage(message.message)
      };
      this.send('deleteFilter', id, callbackOptions);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(CustomFilterList);