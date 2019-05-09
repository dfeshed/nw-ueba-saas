import Component from '@ember/component';
import { connect } from 'ember-redux';
import { setSearchScroll } from 'investigate-events/actions/interaction-creators';
import {
  searchMatchesCount,
  searchScrollDisplay
} from 'investigate-events/reducers/investigate/event-results/selectors';

const stateToComputed = (state) => ({
  searchTerm: state.investigate.eventResults.searchTerm,
  searchScrollIndex: state.investigate.eventResults.searchScrollIndex,
  searchScrollDisplay: searchScrollDisplay(state),
  searchMatchesCount: searchMatchesCount(state)
});

const dispatchToActions = {
  setSearchScroll
};

const SearchControls = Component.extend({
  classNames: 'search-controls',
  tagName: 'div'
});

export default connect(stateToComputed, dispatchToActions)(SearchControls);
