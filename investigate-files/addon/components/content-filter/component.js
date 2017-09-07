import { connect } from 'ember-redux';
import { capitalize } from 'ember-string';
import Component from 'ember-component';
import computed from 'ember-computed-decorators';
import run from 'ember-runloop';
import service from 'ember-service/inject';

import layout from './template';
import { listWithoutDefault, appliedFilters } from 'investigate-files/reducers/filter/selectors';
import {
  removeFilter,
  resetFilters,
  addFilter
} from 'investigate-files/actions/data-creators';

const stateToComputed = ({ files }) => ({
  allFilters: listWithoutDefault(files), // Excluding the default filter from the list
  appliedFilters: appliedFilters(files)
});

const dispatchToActions = {
  removeFilter,
  resetFilters,
  addFilter
};

const ContentFilter = Component.extend({
  layout,

  tagName: 'hbox',

  eventBus: service(),

  classNames: 'content-filter filter-content-holder',

  /**
   * Search the filter control based on user entered text
   * @public
   */
  @computed('allFilters', 'searchTerm')
  filterList(allFilters, searchTerm) {
    const list = [ ...allFilters ]; // Don't want to modify the orignal filter list
    if (searchTerm && searchTerm.length > 3) {
      return list.filter((item) => {
        const name = this.get('i18n').t(item.get('label')) || '';
        return capitalize(name.toString()).includes(capitalize(searchTerm));
      });
    } else {
      return list;
    }
  },

  actions: {

    /**
     * Action to handle filter selection from more filter dropdown. On selection of the checkbox 'addFilter' action will
     * be called with default expression. If checkbox is unchecked 'removeFilter' action will called
     * @param control
     * @public
     */
    onSelection({ selected, panelId, propertyName }) {
      if (!selected) {
        this.set('activeButton', panelId);
        this.get('eventBus').trigger('rsa-content-tethered-panel-toggle-moreOptions');
        this.send('addFilter', { propertyName, propertyValues: null });
      } else {
        run.next(() => {
          this.send('removeFilter', propertyName);
        });
      }
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ContentFilter);