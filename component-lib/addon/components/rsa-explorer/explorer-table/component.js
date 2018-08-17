import Component from '@ember/component';
import { empty } from 'ember-computed-decorators';
import layout from './template';

export default Component.extend({
  layout,
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
        this.sortBy(sortField, !isSortDescending);
      }
    },
    handleRowClickAction(item, index, event) {
      const $eventTarget = this.$(event.target);

      // Do not send the action if the checkbox is being selected
      if (!$eventTarget.is('.rsa-form-checkbox') && !$eventTarget.is('.rsa-form-checkbox-label')) {
        this.focus(item);
      }
      return false;
    }
  }
});
