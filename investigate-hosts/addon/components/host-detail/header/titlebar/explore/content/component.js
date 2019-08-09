import Component from '@ember/component';
import { connect } from 'ember-redux';
import { searchResultNotFound } from 'investigate-hosts/reducers/details/explore/selectors';
import { setSelectedTabData } from 'investigate-hosts/actions/data-creators/explore';
import { toggleExploreSearchResults } from 'investigate-hosts/actions/ui-state-creators';
import computed from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import moment from 'moment';

const stateToComputed = (state) => ({
  fileSearchResults: state.endpoint.explore.fileSearchResults,
  isDataTruncated: state.endpoint.explore.isDataTruncated,
  searchResultNotFound: searchResultNotFound(state),
  searchKey: state.endpoint.explore.searchValue
});

const dispatchToActions = {
  setSelectedTabData,
  toggleExploreSearchResults
};

const ExploreContent = Component.extend({

  tagName: 'box',

  classNames: ['col-xs-12', 'host-explore__content'],
  // click event handler added to stop event bubbling and prevent the search from getting reset.
  click: (event) => {
    event.stopPropagation();
  },

  timezone: service(),
  i18n: service(),

  @computed('fileSearchResults')
  enahancedSearchResult(fileSearchResults) {
    if (!fileSearchResults || !fileSearchResults.length) {
      return [];
    }
    return fileSearchResults.map((result) => {
      const { scanStartTime, files } = result;
      moment.locale(this.get('i18n').locale);
      const date = moment(scanStartTime);
      const dateTimeForm = date.tz(this.get('timezone')._selected.zoneId).format('YYYY-MM-DD hh:mm:ss.SSS a');
      return { ...result, label: `${ dateTimeForm } (${ files.length })` };
    });
  },

  actions: {
    navigateToFile(checksum, scanTime, searchKey) {
      const tabName = 'FILES';
      this.send('toggleExploreSearchResults', false);
      this.navigateToTab({ tabName, scanTime, checksum, searchKey }, true);
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(ExploreContent);

