import Component from 'ember-component';
import layout from './template';
import { connect } from 'ember-redux';
import injectService from 'ember-service/inject';

import {
  addSystemFilter,
  resetFilters,
  deleteSavedSearch,
  addExternalFilter
} from 'investigate-hosts/actions/data-creators/filter';

const stateToComputed = ({ endpoint }) => ({
  isFilterReset: endpoint.filter.isFilterReset,
  systemFilterList: endpoint.filter.filters
});

const dispatchToActions = (dispatch) => ({
  addSystemFilter: (list) => dispatch(addSystemFilter(list)),
  addExternalFilter: (list) => dispatch(addExternalFilter(list)),
  resetFilters: () => dispatch(resetFilters()),
  deleteSelected(id) {
    dispatch(deleteSavedSearch(id, {
      onSuccess: () => (this.get('flashMessage').showFlashMessage('investigateHosts.savedQueries.delete.successMessage')),
      onFailure: ({ meta: message }) => (this.get('flashMessage').showErrorMessage(message.message))
    }));
  }
});

const FilterList = Component.extend({
  layout,

  tagName: 'ul',

  classNames: ['filter-list'],

  flashMessage: injectService(),

  eventBus: injectService(),

  showConfirmationDialog: false,

  activeFilter: null,

  selectedFilterId: null,

  _closeModal() {
    this.get('eventBus').trigger('rsa-application-modal-close-confirm-delete');
  },

  actions: {
    applyFilter({ id, criteria }) {
      this.set('activeFilter', id);
      let expressionList = [];
      if (criteria) {
        expressionList = criteria.expressionList;
        this.send('addExternalFilter', expressionList);
      } else if (id === 'all') {
        this.send('resetFilters');
      }
    },

    deleteSearch(id) {
      this.send('deleteSelected', id);
      this._closeModal();
    }

  }
});

export default connect(stateToComputed, dispatchToActions)(FilterList);
