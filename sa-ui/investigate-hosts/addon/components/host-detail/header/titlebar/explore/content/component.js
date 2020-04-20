import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import { action, computed } from '@ember/object';
import { inject as service } from '@ember/service';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { searchResultNotFound } from 'investigate-hosts/reducers/details/explore/selectors';
import { setSelectedTabData } from 'investigate-hosts/actions/data-creators/explore';
import { toggleExploreSearchResults } from 'investigate-hosts/actions/ui-state-creators';
import moment from 'moment';
import { setScanTime } from 'investigate-hosts/actions/data-creators/details';

const stateToComputed = (state) => ({
  fileSearchResults: state.endpoint.explore.fileSearchResults,
  isDataTruncated: state.endpoint.explore.isDataTruncated,
  searchResultNotFound: searchResultNotFound(state),
  searchKey: state.endpoint.explore.searchValue
});

const dispatchToActions = {
  setSelectedTabData,
  toggleExploreSearchResults,
  setScanTime
};

@classic
@tagName('box')
@classNames('col-xs-12', 'host-explore__content')
class ExploreContent extends Component {
  // click event handler added to stop event bubbling and prevent the search from getting reset.
  click = (event) => {
    event.stopPropagation();
  };

  @service
  timezone;

  @service
  i18n;

  @computed('fileSearchResults')
  get enahancedSearchResult() {
    if (!this.fileSearchResults || !this.fileSearchResults.length) {
      return [];
    }
    return this.fileSearchResults.map((result) => {
      const { scanStartTime, files } = result;
      moment.locale(this.get('i18n').locale);
      const date = moment(scanStartTime);
      const dateTimeForm = date.tz(this.get('timezone')._selected.zoneId).format('YYYY-MM-DD hh:mm:ss.SSS a');
      return { ...result, label: `${ dateTimeForm } (${ files.length })` };
    });
  }

  @action
  navigateToFile(checksum, scanTime, searchKey) {
    const tabName = 'FILES';
    this.send('toggleExploreSearchResults', false);
    this.send('setScanTime', scanTime);
    this.navigateToTab({ tabName, scanTime, checksum, searchKey }, true);
  }
}

export default connect(stateToComputed, dispatchToActions)(ExploreContent);

