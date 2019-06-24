import Component from '@ember/component';
import { COLUMNS_CONFIG } from './columnsConfig';
import { ITEMS_LIST } from './itemsList';
export default Component.extend({
  classNames: ['card-lc', 'border-panel-lc'],
  columns: COLUMNS_CONFIG,
  itemsList: ITEMS_LIST
});
