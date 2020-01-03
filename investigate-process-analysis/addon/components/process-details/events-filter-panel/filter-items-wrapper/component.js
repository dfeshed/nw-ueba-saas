import classic from 'ember-classic-decorator';
import { classNames, classNameBindings } from '@ember-decorators/component';
import { action, computed } from '@ember/object';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { updateFilterValue, updateActionFilterItems } from 'investigate-process-analysis/actions/creators/process-filter';
import { selectedFilterItemsArray, isWindowsAgent } from 'investigate-process-analysis/reducers/process-filter/selectors';

const stateToComputed = (state) => ({
  listOfFiltersSelected: selectedFilterItemsArray(state),
  selectedProcess: state.processAnalysis.processTree.selectedProcess,
  isWindowsAgent: isWindowsAgent(state)
});

const dispatchToActions = {
  updateFilterValue,
  updateActionFilterItems
};

@classic
@classNames('filterWrapper', 'filterItem')
@classNameBindings('isSelected')
class filterItemsWrapper extends Component {
  @computed('listOfFiltersSelected', 'option')
  get isSelected() {
    return this.listOfFiltersSelected.includes(this.option);
  }

  @action
  toggleSelection(filterName, optionSelected) {
    const isSelected = !this.get('isSelected');
    const selectedProcess = this.get('selectedProcess');
    if (filterName === 'category') {
      this.send('updateActionFilterItems', { isSelected, optionSelected, isWindowsAgent });
    }
    this.send('updateFilterValue', { filterName, optionSelected, isSelected, selectedProcess });
  }
}

export default connect(stateToComputed, dispatchToActions)(filterItemsWrapper);