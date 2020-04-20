import Component from '@glimmer/component';

export default class TableWidgetPillComponent extends Component {

  get fillStyle() {
    const riskScore = this.args.score;
    if (riskScore <= 30) {
      return 'is-low';
    } else if (riskScore <= 69) {
      return 'is-medium';
    } else if (riskScore <= 99) {
      return 'is-high';
    } else if (riskScore > 99) {
      return 'is-danger';
    } else {
      return 'is-low';
    }
  }
}
