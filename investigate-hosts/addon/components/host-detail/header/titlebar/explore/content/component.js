import Component from 'ember-component';
import { connect } from 'ember-redux';
import { searchResultNotFound } from 'investigate-hosts/reducers/details/explore/selectors';
import { setSelectedTabData } from 'investigate-hosts/actions/data-creators/explore';
import { loadDetailsWithExploreInput } from 'investigate-hosts/actions/data-creators/details';
import computed from 'ember-computed-decorators';
import service from 'ember-service/inject';
import moment from 'moment';

const stateToComputed = (state) => ({
  fileSearchResults: state.endpoint.explore.fileSearchResults,
  searchResultNotFound: searchResultNotFound(state)
});

const dispatchToActions = {
  setSelectedTabData,
  loadDetailsWithExploreInput
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
    navigateToFile(checksum, scanTime) {
      const tabName = 'FILES';
      const option = { tabName, checksum };
      this.send('setSelectedTabData', option);
      this.send('loadDetailsWithExploreInput', scanTime, tabName);
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(ExploreContent);

