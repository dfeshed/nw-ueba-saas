import computed, { alias } from 'ember-computed-decorators';
import SortableItemMixin from 'ember-sortable/mixins/sortable-item';
import Component from '@ember/component';
import { inject as service } from '@ember/service';
import { connect } from 'ember-redux';
import { sourceCountTooltip, getSourceCount } from 'admin-source-management/utils/groups-util';
import {
  previewRankingWithFetch
} from 'admin-source-management/actions/creators/group-wizard-creators';

const dispatchToActions = {
  previewRankingWithFetch
};

/**
 * Extension of the Data Table default row class for supporting selecting and dragging of rows
 * @public
 */
const Row = Component.extend(SortableItemMixin, {
  tagName: 'tr',
  classNameBindings: ['isSelected'],
  @alias('item') model: null, // used by ember-sortable
  handle: 'tr', // used by ember-sortable
  /**
   * Tracks which item is currently selected / highlighted by the user
   * @property isSelected
   * @public
   */
  @computed('selectedItemId', 'item.name')
  isSelected(selectedItemId, itemId) {
    return selectedItemId === itemId;
  },

  i18n: service(),

  @computed('item.sourceCount', 'item.dirty', 'item.lastPublishedOn')
  srcCountTooltip(sourceCount, isDirty, lastPublishedOn) {
    const i18n = this.get('i18n');
    return sourceCountTooltip(i18n, isDirty, sourceCount, lastPublishedOn);
  },

  @computed('item.sourceCount')
  srcCount(sourceCount) {
    return getSourceCount(sourceCount);
  },

  actions: {
    showTip(index) {
      document.getElementsByClassName('tip')[index].classList.add('show');
    },
    hideTip(index) {
      document.getElementsByClassName('tip')[index].classList.remove('show');
    }
  }
});

export default connect(undefined, dispatchToActions)(Row);