import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getFileSearchResults } from 'investigate-hosts/actions/data-creators/explore';
import { isEmpty } from '@ember/utils';
import { inject as service } from '@ember/service';
import { searchResultLoading, hasValidID } from 'investigate-hosts/reducers/details/explore/selectors';

const stateToComputed = (state) => ({
  searchResultLoading: searchResultLoading(state),
  machineId: hasValidID(state)
});

const dispatchToActions = {
  getFileSearchResults
};

const SearchField = Component.extend({

  tagName: '',

  i18n: service(),

  /**
   * Used for search text box validation error message
   * @public
   */
  errMsg: '',

  /**
   * For search text box validation
   * @public
   */
  isError: false,

  /**
   * Used to show or hide the Search results content
   * based on mouse click
   * @public
   */
  showResults: false,

  actions: {
    defaultAction() {
      const text = this.get('searchText');
      const i18n = this.get('i18n');
      if (isEmpty(text) || text.trim().length < 3) {
        this.set('isError', true);
        this.set('errMsg', i18n.t('investigateHosts.hosts.explore.search.minimumtext.required'));
      } else if (text.trim().length >= 3) {
        this.set('isError', false);
        this.set('errMsg', '');
        this.send('getFileSearchResults', text);
      }
    },
    submitSearch(e) {
      if (e.key == 'Enter') {
        this.send('defaultAction');
        return false;
      }
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(SearchField);
