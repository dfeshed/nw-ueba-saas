import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import { CATEGORIES, TAB_MAPPING, CATEGORY_NAME } from './categories-map';
import { setSelectedTabData } from 'investigate-hosts/actions/data-creators/explore';
import { toggleExploreSearchResults } from 'investigate-hosts/actions/ui-state-creators';

const dispatchToActions = {
  setSelectedTabData,
  toggleExploreSearchResults
};

const stateToComputed = (state) => ({
  searchKey: state.endpoint.explore.searchValue
});

const FileFound = Component.extend({

  tagName: 'box',

  i18n: service(),

  classNames: ['file-found-categories'],

  @computed('file')
  categories(file) {
    const ranAs = [];
    const i18n = this.get('i18n');
    if (file) {
      for (const cat of file.categories) {
        ranAs.push(i18n.t(CATEGORIES[cat]));
      }
    }
    return ranAs;
  },

  actions: {
    onNavigateToTab(catString) {
      let subTabName = null;
      const category = catString.string.toLowerCase().replace(/\s/g, '');
      const scanTime = this.get('scanTime');
      const checksum = this.get('file').checksumSha256;
      const searchKey = this.get('searchKey');
      const tabName = TAB_MAPPING[category];
      if (['AUTORUNS', 'ANOMALIES'].includes(tabName)) {
        subTabName = CATEGORY_NAME[category];
      }
      this.send('toggleExploreSearchResults', false);
      this.navigateToTab({ tabName, subTabName, scanTime, checksum, searchKey }, true);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(FileFound);
