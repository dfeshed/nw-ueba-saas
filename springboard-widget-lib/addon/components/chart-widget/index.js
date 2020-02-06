import Component from '@glimmer/component';
import { assert } from '@ember/debug';
import { DONUT_CHART, DONUT_CHART_COMPONENT } from 'springboard-widget-lib/constants/springboard-widget-lib';

export default class ChartWidgetComponent extends Component {

  get chartOptions() {
    return this.args.options || {};
  }

  get chartComponent() {
    assert('<ChartWidget> requires an `@chartType` argument', this.args.chartType);
    let chartComponent;
    const { chartType } = this.args;
    if (DONUT_CHART === chartType) {
      chartComponent = DONUT_CHART_COMPONENT;
    }
    return chartComponent;
  }
}

