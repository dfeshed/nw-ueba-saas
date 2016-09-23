import computed from 'ember-computed-decorators';
import Ember from 'ember';

import HasTableParent from '../mixins/has-table-parent';
import CellMixin from '../mixins/is-cell';
import SortableItem from 'ember-sortable/components/sortable-item';
import layout from './template';

const {
  inject: {
    service
  }
} = Ember;

export default SortableItem.extend(HasTableParent, CellMixin, {
  layout,
  classNames: 'rsa-data-table-header-cell',
  i18n: service(),

  @computed('column.title', 'column.field', 'translateTitle')
  displayTitle(title, field, translateTitle) {
    if (title) {
      if (translateTitle) {
        return this.get('i18n').t(title);
      } else {
        return title;
      }
    } else {
      return field;
    }
  }
});
