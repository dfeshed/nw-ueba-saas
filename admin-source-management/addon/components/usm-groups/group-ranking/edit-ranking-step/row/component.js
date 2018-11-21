import computed, { alias } from 'ember-computed-decorators';
import SortableItemMixin from 'ember-sortable/mixins/sortable-item';
import Component from '@ember/component';
import { inject as service } from '@ember/service';
import { sourceCountTooltip, getSourceCount } from 'admin-source-management/utils/groups-util';

/**
 * Extension of the Data Table default row class for supporting selecting and dragging of rows
 * @public
 */
export default Component.extend(SortableItemMixin, {
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

  onRowClick() {},

  click() {
    // Index 0 is top renking. No need to select and set top ranking
    if (this.get('index') !== 0) {
      this.get('onRowClick').apply(this, arguments);
    }
    return false;
  }
});