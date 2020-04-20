import { computed } from '@ember/object';
import { isEmpty } from '@ember/utils';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import layout from './template';
import { saveList, resetList, openCreateList, enableNewList } from 'context/actions/list-creators';
import { inject as service } from '@ember/service';
import { listData, entityType, isError, errorMessage, isDisabled } from 'context/reducers/list/selectors';

const stateToComputed = (state) => ({
  listData: listData(state),
  entityType: entityType(state),
  isError: isError(state),
  errorMessageKey: errorMessage(state),
  isDisabled: isDisabled(state)
});

const dispatchToActions = {
  saveList,
  resetList,
  openCreateList,
  enableNewList
};

const ListViewComponent = Component.extend({
  layout,
  active: 'all',
  filterStr: null,
  eventBus: service(),
  selectedRow: null,
  isDisabled: false,

  errorMessage: computed('errorMessageKey', function() {
    return this.get('i18n').t(`context.error.${this.errorMessageKey}`);
  }),

  getFilteredList: computed('filterStr', 'listData', function() {
    if (isEmpty(this.filterStr)) {
      return this.listData;
    } else {
      const filterStrCaps = this.filterStr.toUpperCase();
      return this.listData.filter((data) => {
        const name = data.name.toUpperCase().match(filterStrCaps);
        const desc = data.description && data.description.toUpperCase().match(filterStrCaps);
        return name || desc;
      });
    }
  }),

  actions:
  {
    activate(option) {
      this.set('active', option);
    },

    closeList() {
      this.get('eventBus').trigger('rsa-application-modal-close-addToList');
      this.send('resetList');
    },

    saveListAction() {
      this.send('saveList');
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ListViewComponent);
