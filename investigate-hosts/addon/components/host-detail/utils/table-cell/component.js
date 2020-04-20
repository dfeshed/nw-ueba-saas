import classic from 'ember-classic-decorator';
import { classNameBindings } from '@ember-decorators/component';
import { computed } from '@ember/object';
import BodyCell from 'component-lib/components/rsa-data-table/body-cell/component';

@classic
@classNameBindings('columnName')
export default class TableCell extends BodyCell {
  item = null;
  column = null;

  @computed('column.field')
  get columnName() {
    if (this.column?.field == 'fileProperties.score' || this.column?.field === 'score' || this.column?.field === 'machineFileScore') {
      return 'score';
    }
    return '';
  }

  @computed('item')
  get itemStatus() {
    return this.item.status ? this.item.status : this.item.state;
  }
}
