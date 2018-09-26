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

  i18n: service('i18n'),

  selectedFilterId: null,

  savedFilter: null,

  applyFilter: null,

  deleteFilter: null,

  allFiles: {
    id: 1,
    name: 'All',
    systemFilter: true,
    criteria: {
      expressionList: []
    }
  },

  @computed('savedFilter')
  filterLabel(savedFilter) {
    return savedFilter ? savedFilter.name : this.get('i18n').t('investigateFiles.filter.allFiles');
  },

  @computed('savedFilters')
  savedFilterGroup(filesFilters = []) {
    const systemFilter = filesFilters.filterBy('systemFilter', true);
    const customFilter = filesFilters.filterBy('systemFilter', false);
    return [
      { groupName: 'System Filter', options: systemFilter },
      { groupName: 'Custom Filter', options: customFilter }
    ];
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
    }
  }
});
