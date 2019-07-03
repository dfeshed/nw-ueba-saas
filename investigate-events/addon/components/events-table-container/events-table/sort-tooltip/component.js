import Component from '@ember/component';
import { inject as service } from '@ember/service';

const SortTooltip = Component.extend({
  i18n: service(),

  classNames: ['sort-tooltip'],
  tagName: 'section',

  field: null,
  disableSort: true,
  notIndexedAtValue: [],
  notSingleton: [],
  notValid: [],
  status: null
});

export default SortTooltip;
