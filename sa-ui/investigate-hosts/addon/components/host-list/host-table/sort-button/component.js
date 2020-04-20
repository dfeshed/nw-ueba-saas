import classic from 'ember-classic-decorator';
import { classNameBindings, tagName, layout as templateLayout } from '@ember-decorators/component';
import { computed } from '@ember/object';
import Component from '@ember/component';

import layout from './template';

@classic
@templateLayout(layout)
@tagName('button')
@classNameBindings('columnClass')
export default class SortButton extends Component {
  sortField = null;
  column = null;
  sortBy = null;
  isSortDescending = false;

  @computed('sortField', 'column.field', 'column.sortField')
  get columnClass() {
    let columnClass = 'column-sort hideSort expand';
    if (this.sortField === this.column?.field || this.sortField === this.column?.sortField) {
      columnClass = 'column-sort expand';
    }
    return columnClass;
  }

  @computed('isSortDescending')
  get iconName() {
    return this.isSortDescending ? 'arrow-down-7' : 'arrow-up-7';
  }

  click() {
    const { column, isSortDescending } = this.getProperties('column', 'isSortDescending');
    const field = column.sortField || column.field;
    const descending = !isSortDescending;
    this.sortBy({ key: field, descending });
  }
}
