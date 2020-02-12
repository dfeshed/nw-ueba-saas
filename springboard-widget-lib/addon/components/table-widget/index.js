import Component from '@glimmer/component';
import { inject as service } from '@ember/service';

const COLUMN_WIDTH = {
  'machineIdentity.machineName': '200px',
  score: '70px'
};


export default class TableWidgetComponent extends Component {

  @service('i18n') i18n;

  get tableData() {
    return this.args.data?.items || [];
  }


  get columns() {
    const cols = this.args.config?.columns || [];
    const newColumns = cols.map((col) => {
      return {
        width: COLUMN_WIDTH[col] || '110px',
        field: col,
        title: this.i18n.t(`springboard.columns.${col}`)
      };
    });
    return [...newColumns];
  }
}
