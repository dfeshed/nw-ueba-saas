import Component from '@glimmer/component';
import { action } from '@ember/object';

export default class HeaderComponent extends Component {
  @action
  onViewAllClick() {
    if (this.args.navigateTo) {
      this.args.navigateTo();
    }
  }
  @action
  onEditWidgetClick() {
    if (this.args.editWidget) {
      this.args.editWidget();
    }
  }
}