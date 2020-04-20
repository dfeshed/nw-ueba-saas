import Component from '@ember/component';
import { inject as service } from '@ember/service';

const SortTooltip = Component.extend({
  i18n: service(),

  classNames: ['sort-tooltip'],
  tagName: 'section',

  field: null,
  disableSort: true,
  status: null,

  init() {
    this._super(arguments);
    this.notIndexedAtValue = this.notIndexedAtValue || [];
    this.notSingleton = this.notSingleton || [];
    this.notValid = this.notValid || [];
  }
});

export default SortTooltip;
