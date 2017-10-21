import Component from 'ember-component';
import { connect } from 'ember-redux';
import { resetExploreSearch } from 'investigate-hosts/actions/data-creators/explore';
import { toggleExploreSearchResults } from 'investigate-hosts/actions/ui-state-creators';


const stateToComputed = ({ endpoint: { explore: { searchValue } } }) => ({
  searchValue
});

const dispatchToActions = {
  resetExploreSearch,
  toggleExploreSearchResults
};

const SearchLabel = Component.extend({
  tagName: ''
});

export default connect(stateToComputed, dispatchToActions)(SearchLabel);

