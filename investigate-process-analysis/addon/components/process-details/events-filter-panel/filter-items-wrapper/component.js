import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import { updateFilterValue, updateActionFilterItems } from 'investigate-process-analysis/actions/creators/process-filter';
import { selectedFilterItemsArray } from 'investigate-process-analysis/reducers/process-filter/selectors';

const stateToComputed = (state) => ({
  listOfFiltersSelected: selectedFilterItemsArray(state),
  selectedProcess: state.processAnalysis.processTree.selectedProcess
});

const dispatchToActions = {
  updateFilterValue,
  updateActionFilterItems
};

const filterItemsWrapper = Component.extend({

  classNames: ['filterWrapper', 'filterItem'],

  classNameBindings: ['isSelected'],

  @computed('listOfFiltersSelected', 'option')
  isSelected: (listOfFiltersSelected, filterOption) => {
    return listOfFiltersSelected.includes(filterOption);
  },

  actions: {
    toggleSelection(filterName, optionSelected) {
      const isSelected = !this.get('isSelected');
      const selectedProcess = this.get('selectedProcess');
      if (filterName === 'category') {
        this.send('updateActionFilterItems', { isSelected, optionSelected });
      }
      this.send('updateFilterValue', { filterName, optionSelected, isSelected, selectedProcess });
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(filterItemsWrapper);