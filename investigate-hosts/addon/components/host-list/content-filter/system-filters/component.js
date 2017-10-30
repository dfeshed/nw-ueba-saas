import Component from 'ember-component';
import layout from './template';
import { connect } from 'ember-redux';
import injectService from 'ember-service/inject';
import { next } from 'ember-runloop';

import {
  addSystemFilter,
  resetFilters,
  deleteSavedSearch
} from 'investigate-hosts/actions/data-creators/filter';

const message = {
  success: 'investigateHosts.savedQueries.delete.successMessage',
  failure: 'investigateHosts.savedQueries.delete.failureMessage'
};

const stateToComputed = ({ endpoint }) => ({
  isFilterReset: endpoint.filter.isFilterReset,
  systemFilterList: endpoint.filter.filters
});

const dispatchToActions = (dispatch) => ({
  addSystemFilter: (list) => dispatch(addSystemFilter(list)),
  resetFilters: () => dispatch(resetFilters()),
  deleteSelected(id) {
    dispatch(deleteSavedSearch(id, {
      onSuccess: () => (this.get('flashMessage').showFlashMessage(message.success)),
      onFailure: () => (this.get('flashMessage').showFlashMessage(message.failure))
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
      } else if (id === 'all') {
        this.send('resetFilters');
      }
      this.send('addSystemFilter', expressionList[0]);
    },

    showConfirmationModal(id) {
      this.set('selectedFilterId', id);
      this.set('showConfirmationDialog', true);
      next(() => {
        this.get('eventBus').trigger('rsa-application-modal-open-confirm-delete');
      });
    },

    closeConfirmDeleteModal() {
      this.set('selectedFilterId', null);
      this.set('showConfirmationDialog', false);
      this._closeModal();
    },

    deleteSearch() {
      this.send('deleteSelected', this.get('selectedFilterId'));
      this._closeModal();
    }

  }
});

export default connect(stateToComputed, dispatchToActions)(FilterList);
