import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import { action, computed } from '@ember/object';
import { inject as service } from '@ember/service';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { CATEGORIES, TAB_MAPPING, CATEGORY_NAME } from './categories-map';
import { setSelectedTabData } from 'investigate-hosts/actions/data-creators/explore';
import { toggleExploreSearchResults } from 'investigate-hosts/actions/ui-state-creators';
import { setScanTime } from 'investigate-hosts/actions/data-creators/details';

const dispatchToActions = {
  setSelectedTabData,
  toggleExploreSearchResults,
  setScanTime
};

const stateToComputed = (state) => ({
  searchKey: state.endpoint.explore.searchValue
});

@classic
@tagName('box')
@classNames('file-found-categories')
class FileFound extends Component {
  @service
  i18n;

  @computed('file')
  get categories() {
    const ranAs = [];
    const i18n = this.get('i18n');
    if (this.file) {
      for (const cat of this.file.categories) {
        ranAs.push(i18n.t(CATEGORIES[cat]));
      }
    }
    return ranAs;
  }

  @action
  onNavigateToTab(catString) {
    let subTabName = null;
    const category = catString.toLowerCase().replace(/\s/g, '');
    const scanTime = this.get('scanTime');
    const checksum = this.get('file').checksumSha256;
    const searchKey = this.get('searchKey');
    const tabName = TAB_MAPPING[category];
    if (['AUTORUNS', 'ANOMALIES'].includes(tabName)) {
      subTabName = CATEGORY_NAME[category];
    }
    this.send('setScanTime', scanTime);
    this.send('toggleExploreSearchResults', false);
    this.navigateToTab({ tabName, subTabName, scanTime, checksum, searchKey }, true);
  }
}

export default connect(stateToComputed, dispatchToActions)(FileFound);
