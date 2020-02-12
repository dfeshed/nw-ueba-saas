import Component from '@glimmer/component';
import { inject as service } from '@ember/service';

const COLUMN_WIDTH = {
  hostName: 200,
  score: 90
};


export default class TableWidgetComponent extends Component {

  @service('i18n') i18n;

  get tableData() {
    return this.args.data?.items;
  }


  get columns() {
    const cols = this.args.config?.columns || [];
    const newColumns = cols.map((col) => {
      return {
        width: COLUMN_WIDTH[col] || 130,
        field: col,
        title: this.i18n.t(`springboard.columns.${col}`)
      };
    });
    return [...newColumns];
  }
}
