import computed, { alias } from 'ember-computed-decorators';
import SortableItemMixin from 'ember-sortable/mixins/sortable-item';
import Component from '@ember/component';
import { inject as service } from '@ember/service';
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

  @computed('item.sourceCount')
  srcCount(sourceCount) {
    const i18n = this.get('i18n');
    switch (sourceCount) {
      case -1:
        return i18n.t('adminUsm.groups.list.sourceCountPublishedNewGroupTooltip');
      case -2:
        return i18n.t('adminUsm.groups.list.sourceCountPublishedNoEndpointTooltip');
      case -3:
        return i18n.t('adminUsm.groups.list.sourceCountUnpublishedGroupTooltip');
      default:
        return sourceCount;
    }
  },

  onRowClick() {},

  click() {
    this.get('onRowClick').apply(this, arguments);
    return false;
  }
});