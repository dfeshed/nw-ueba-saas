import Component from '@glimmer/component';
import { inject as service } from '@ember/service';
import { DONUT_CHART } from 'springboard-widget-lib/constants/springboard-widget-lib';

export default class TopLeadsComponent extends Component {

  @service('i18n') i18n;

  get columns() {
    const cols = this.args.widget?.tableConfig.columns || [];
    const newColumns = cols.map((col) => {
      return { field: col, title: this.i18n.t(`springboard.columns.${col}`) };
    });
    return newColumns;
  }

  get visualType() {
    return this.args.widget?.visualConfig?.type;
  }

  get chartOptions() {
    if (DONUT_CHART === this.visualType) {
      const column = this.args.widget?.visualConfig.aggregate.column[0];
      return {
        valueProp: 'count',
        height: 240,
        columnName: this.i18n.t(`springboard.columns.${column}`)
      };
    }
    return {};
  }

  get tableData() {
    return this.args.widgetData?.items;
  }

  get chartData() {
    return this.args.widgetData?.aggregate?.data || [];
  }
}
