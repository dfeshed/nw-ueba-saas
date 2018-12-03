import BodyCell from 'component-lib/components/rsa-data-table/body-cell/component';
import computed from 'ember-computed-decorators';

export default BodyCell.extend({

  item: null,

  column: null,

  classNameBindings: ['columnName'],

  @computed('column.field')
  columnName(column) {
    if (column == 'fileProperties.score' || column === 'score') {
      return 'score';
    }
  },

  @computed('item')
  itemStatus: ((item) => item.status ? item.status : item.state)
});
