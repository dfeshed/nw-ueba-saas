import Component from '@glimmer/component';
import { inject as service } from '@ember/service';

export default class TableWidgetComponent extends Component {

  @service('i18n') i18n;

  get columns() {
    const cols = this.args.config?.columns || [];
    const newColumns = cols.map((col) => {
      return { field: col, title: this.i18n.t(`springboard.columns.${col}`) };
    });
    return newColumns;
  }
}
