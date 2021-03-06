import GroupHeader from 'respond/components/rsa-group-table/group-header/component';
import layout from './template';
import { and, equal } from '@ember/object/computed';
import { set, setProperties, computed } from '@ember/object';

/**
 * @class Alerts Table Alert header component
 * Renders an Alert row for an Alerts group table.
 *
 * The markup rendered by this component includes:
 * (1) an arrow for expanding/collapsing the alert's child items;
 * (2) a tab for showing the alert's events as the child items; and
 * (3) a tab for showing the alert's enrichments as the child items.
 *
 * By clicking the tabs mentioned above, the end-user can choose what they want to see as the "child items" of this
 * group when the group is expanded ("open").
 * @public
 */
export default GroupHeader.extend({
  layout,
  classNames: ['rsa-alerts-table-alert-header'],
  classNameBindings: ['isFirst', 'isLast', 'isSelected'],
  testId: 'alertsTableHeader',
  attributeBindings: ['testId:test-id'],

  // True if this group is the first group in the entire table's `groups` array.
  isFirst: equal('index', 0),

  // True if this group is the last group in the entire table's `groups` array.
  isLast: computed('group', 'table.groups.lastObject', function() {
    return this.group === this.table?.groups?.lastObject;
  }),

  // Computes whether or not the Enrichments tab should be active.
  isEnrichmentsTabActive: and('group.isOpen', 'group.showEnrichmentsAsItems'),

  // Computes whether or not the Events tab should be active.
  isEventsTabActive: computed('group.isOpen', 'group.showEnrichmentsAsItems', function() {
    return this.group?.isOpen && !this.group?.showEnrichmentsAsItems;
  }),

  // Determines if this group is selected by searching for the group's id in the parent table's selections hash.
  isSelected: computed(
    'group.id',
    'table.selections.areGroups',
    'table.selectionsHash',
    function() {
      return !!this.table?.selections?.areGroups && !!this.table?.selectionsHash && (this.group?.id in this.table?.selectionsHash);
    }
  ),

  actions: {

    /**
     * Handles clicks on the Events tab.
     * If Events tab is already active, we collapse it; otherwise we make it active and open it.
     * @public
     */
    toggleEvents() {
      const group = this.get('group');
      if (!group) {
        return;
      }
      if (this.get('isEventsTabActive')) {
        set(group, 'isOpen', false);
      } else {
        setProperties(group, {
          showEnrichmentsAsItems: false,
          isOpen: true
        });
      }
    },

    /**
     * Handles clicks on the Enrichments tab.
     * If Enrichments tab is already active, we collapse it; otherwise we make it active and open it.
     * @public
     */
    toggleEnrichments() {
      const group = this.get('group');
      if (!group) {
        return;
      }
      if (this.get('isEnrichmentsTabActive')) {
        set(group, 'isOpen', false);
      } else {
        setProperties(group, {
          showEnrichmentsAsItems: true,
          isOpen: true
        });
      }
    }
  }
});
