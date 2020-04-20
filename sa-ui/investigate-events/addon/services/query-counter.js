import Service from '@ember/service';

const DEFAULT_TAB_COUNT = 0;

export default Service.extend({

  metaTabCount: DEFAULT_TAB_COUNT,
  recentQueryTabCount: DEFAULT_TAB_COUNT,

  /**
   * In the case when user backpaces quickly, usually the response for previous call is returned after
   * the user has completly removed chars from the input, updating the result count.
   * We want to avoid such race conditions and thus guard the queryCount with this flag.
   * This flag should be set to true once a api request is made.
   * And should be set to false once the reponse is recieved.
   * And if this flag is set to false due to some side effect, api response needs to respect that.
   */
  isExpectingResponse: false,


  setMetaTabCount(count = DEFAULT_TAB_COUNT) {
    this.set('metaTabCount', count);
  },

  setRecentQueryTabCount(count = DEFAULT_TAB_COUNT) {
    this.set('recentQueryTabCount', count);
  },

  setResponseFlag(flag) {
    this.set('isExpectingResponse', flag);
  },

  resetAllTabCounts() {
    this.setResponseFlag(false);
    this.setMetaTabCount(DEFAULT_TAB_COUNT);
    this.setRecentQueryTabCount(DEFAULT_TAB_COUNT);
  }
});
