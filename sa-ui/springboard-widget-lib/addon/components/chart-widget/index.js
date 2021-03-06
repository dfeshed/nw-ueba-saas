import Component from '@glimmer/component';
import { assert } from '@ember/debug';
import { DONUT_CHART, DONUT_CHART_COMPONENT } from 'springboard-widget-lib/constants/springboard-widget-lib';
import { inject as service } from '@ember/service';
import { mashUpData } from 'springboard-widget-lib/selectors/data-selector';

export default class ChartWidgetComponent extends Component {

  @service('i18n') i18n;

  get chartOptions() {
    const { config, config: { chartType } } = this.args;
    if (DONUT_CHART === chartType) {
      const column = config?.aggregate.columns[0];
      return {
        valueProp: 'count',
        height: 240,
        innerRadius: 105,
        columnName: this.i18n.t(`springboard.columns.${column}`)
      };
    }
    return {};
  }

  get chartData() {
    return mashUpData({ data: this.args.data, widget: this.args.config }) || [];
  }

  get chartComponent() {
    assert('<ChartWidget> requires a `@config.chartType` argument', this.args.config.chartType);
    let chartComponent;
    const { config: { chartType } } = this.args;
    if (DONUT_CHART === chartType) {
      chartComponent = DONUT_CHART_COMPONENT;
    }
    return chartComponent;
  }
}

