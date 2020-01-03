import classic from 'ember-classic-decorator';
import { tagName } from '@ember-decorators/component';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { resetExploreSearch } from 'investigate-hosts/actions/data-creators/explore';
import { toggleExploreSearchResults } from 'investigate-hosts/actions/ui-state-creators';
import { searchResultLoading } from 'investigate-hosts/reducers/details/explore/selectors';


const stateToComputed = (state) => ({
  searchValue: state.endpoint.explore.searchValue,
  searchResultLoading: searchResultLoading(state)
});

const dispatchToActions = {
  resetExploreSearch,
  toggleExploreSearchResults
};

@classic
@tagName('')
class SearchLabel extends Component {}

export default connect(stateToComputed, dispatchToActions)(SearchLabel);

