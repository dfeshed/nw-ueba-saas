import Component from 'ember-component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import service from 'ember-service/inject';
import { CATEGORIES, CATEGORY_NAME } from './categories-map';
import { setSelectedTabData } from 'investigate-hosts/actions/data-creators/explore';
import { loadDetailsWithExploreInput } from 'investigate-hosts/actions/data-creators/details';

const dispatchToActions = {
  setSelectedTabData,
  loadDetailsWithExploreInput
};

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
    navigateToTab(category) {
      let tabName = CATEGORY_NAME[category.string.toLowerCase()];
      let secondaryTab = null;
      const childTabs = ['AUTORUNS', 'SERVICES', 'TASKS'];
      const scanTime = this.get('scanTime');
      const checksum = this.get('file').checksumSha256;
      const option = { tabName, checksum };
      this.send('setSelectedTabData', option);
      if (childTabs.some((t) => (t === tabName))) {
        secondaryTab = tabName;
        tabName = 'AUTORUNS';
      }
      this.send('loadDetailsWithExploreInput', scanTime, tabName, secondaryTab);
    }
  }
});

export default connect(undefined, dispatchToActions)(FileFound);
