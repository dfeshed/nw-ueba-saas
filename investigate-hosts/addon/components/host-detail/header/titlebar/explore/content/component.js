import Component from 'ember-component';
import { connect } from 'ember-redux';
import { searchResultNotFound, enahancedSearchResult } from 'investigate-hosts/reducers/details/explore/selectors';
import { setSelectedTabData } from 'investigate-hosts/actions/data-creators/explore';
import { loadDetailsWithExploreInput } from 'investigate-hosts/actions/data-creators/details';

const stateToComputed = (state) => ({
  fileSearchResults: enahancedSearchResult(state),
  searchResultNotFound: searchResultNotFound(state)
});

const dispatchToActions = {
  setSelectedTabData,
  loadDetailsWithExploreInput
};

const ExploreContent = Component.extend({

  tagName: 'box',

  classNames: ['col-xs-12', 'host-explore__content'],

  actions: {
    navigateToFile(checksum, scanTime) {
      const tabName = 'FILES';
      const option = { tabName, checksum };
      this.send('setSelectedTabData', option);
      this.send('loadDetailsWithExploreInput', scanTime, tabName);
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(ExploreContent);

