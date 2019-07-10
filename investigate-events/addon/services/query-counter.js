import Service from '@ember/service';

const DEFAULT_TAB_COUNT = 0;

export default Service.extend({

  metaTabCount: DEFAULT_TAB_COUNT,
  recentQueryTabCount: DEFAULT_TAB_COUNT,


  setMetaTabCount(count = DEFAULT_TAB_COUNT) {
    this.set('metaTabCount', count);
  },

  setRecentQueryTabCount(count = DEFAULT_TAB_COUNT) {
    this.set('recentQueryTabCount', count);
  },

  resetAllTabCounts() {
    this.setMetaTabCount(DEFAULT_TAB_COUNT);
    this.setRecentQueryTabCount(DEFAULT_TAB_COUNT);
  }
});
