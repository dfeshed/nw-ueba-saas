import Component from '@ember/component';
import { empty } from 'ember-computed-decorators';

export default Component.extend({
  tagName: 'section',
  classNames: 'rsa-explorer-table',
  classNameBindings: ['hasNoFocusedItem::has-focused-item'],
  useLazyRendering: true,

  /**
   * @property hasNoFocusedIncidents
   * @type boolean
   * @public
   */
  @empty('focusedItem') hasNoFocusedItem: true,

  actions: {
    sort(column, isSortDescending) {
      if (!column.disableSort) {
        const sortField = column.sortField || column.field;
        this.sendAction('sortBy', sortField, !isSortDescending);
      }
    },
    handleRowClickAction(item, index, event) {
      const $eventTarget = this.$(event.target);

      // Do not send the action if the checkbox is being selected
      if (!$eventTarget.is('.rsa-form-checkbox') && !$eventTarget.is('.rsa-form-checkbox-label')) {
        this.sendAction('focus', item);
      }
      return false;
    }
  }
});
