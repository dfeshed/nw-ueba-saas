import Component from '@ember/component';
import { connect } from 'ember-redux';
import { hostListForScanning, hasMachineId } from 'investigate-hosts/reducers/hosts/selectors';
import { inject as service } from '@ember/service';
import { getPageOfMachines } from 'investigate-hosts/actions/data-creators/host';
import { createCustomSearch, applyFilters, applySavedFilters, deleteFilter } from 'investigate-hosts/actions/data-creators/filter-creators';
import { FILTER_TYPES } from './filter-types';
import { selectedFilterId, savedFilter } from 'investigate-shared/selectors/endpoint-filters/selectors';

const stateToComputed = (state) => ({
  selectedHostList: hostListForScanning(state),
  hasMachineId: hasMachineId(state),
  filter: state.endpoint.filter,
  selectedFilterId: selectedFilterId(state.endpoint),
  savedFilter: savedFilter(state.endpoint),
  hostFilters: state.endpoint.filter.savedFilterList
});

const dispatchToActions = {
  getPageOfMachines,
  createCustomSearch,
  applySavedFilters,
  applyFilters,
  deleteFilter
};

const Container = Component.extend({

  eventBus: service(),

  tagName: 'hbox',

  classNames: 'host-engine host-container',

  classNameBindings: ['hasMachineId'],

  filterTypes: FILTER_TYPES,

  init() {
    this._super(...arguments);
  },

  click(event) {
    // this trigger is required to open start/stop scan modal window
    this.get('eventBus').trigger('rsa-application-click', event.target);
  }

});

export default connect(stateToComputed, dispatchToActions)(Container);
