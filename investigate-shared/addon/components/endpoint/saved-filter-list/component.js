import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { success, failure } from 'investigate-shared/utils/flash-messages';
import layout from './template';
import { inject as service } from '@ember/service';

/**
 * Toolbar that provides search filtering.
 * @public
 */
export default Component.extend({
  layout,

  classNames: ['saved-filter-list'],

  i18n: service('i18n'),

  selectedFilterId: null,

  savedFilter: null,

  applyFilter: null,

  deleteFilter: null,

  selected: null,

  @computed('savedFilters')
  customFilters(filesFilters = []) {
    return filesFilters.filterBy('systemFilter', false);
  },

  didReceiveAttrs() {
    this._super(arguments);
    const savedFilter = this.get('savedFilter');
    if (savedFilter && savedFilter.id === 1) {
      this.set('selected', null);
    } else {
      this.set('selected', savedFilter);
    }
  },

  actions: {

    deleteSelectedFilter(id) {
      const callbackOptions = {
        onSuccess: () => {
          success('investigateFiles.filter.customFilters.delete.successMessage');
        },
        onFailure: () => failure('investigateFiles.filter.customFilters.delete.errorMessage')
      };
      this.deleteFilter(id, callbackOptions);
    },

    stopPropagation(e) {
      e.stopPropagation();
    },

    applyCustomFilter(option) {
      this.set('selected', option);
      if (option) {
        this.applyFilter(option);
      }
    },

    onClose(data, e) {
      if (e.target.classList.contains('rsa-form-button')) {
        return false;
      }
    }
  }
});
