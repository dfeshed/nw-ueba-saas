import classic from 'ember-classic-decorator';
import { action } from '@ember/object';
import { classNames, classNameBindings } from '@ember-decorators/component';
import { inject as service } from '@ember/service';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getFileSearchResults } from 'investigate-hosts/actions/data-creators/explore';
import { isEmpty } from '@ember/utils';
import { searchResultLoading, hasValidID } from 'investigate-hosts/reducers/details/explore/selectors';

const stateToComputed = (state) => ({
  searchResultLoading: searchResultLoading(state),
  machineId: hasValidID(state)
});

const dispatchToActions = {
  getFileSearchResults
};

@classic
@classNames('search-field')
@classNameBindings('isError')
class SearchField extends Component {
  @service
  i18n;

  /**
   * Used for search text box validation error message
   * @public
   */
  errMsg = '';

  /**
   * For search text box validation
   * @public
   */
  isError = false;

  /**
   * Used to show or hide the Search results content
   * based on mouse click
   * @public
   */
  showResults = false;

  @action
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
  }

  @action
  onKeyUp(value) {
    if (isEmpty(value)) {
      this.set('isError', false);
    }
  }
}

export default connect(stateToComputed, dispatchToActions)(SearchField);
