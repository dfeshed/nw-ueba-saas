import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import { action, computed } from '@ember/object';
import Component from '@ember/component';

@classic
@tagName('hbox')
@classNames('host-title-bar flexi-fit rsa-nav-tab-group heading-tabs')
export default class TitleBar extends Component {
  @computed('tabs')
  get visibleTabs() {
    return this.tabs.filter((tab) => !tab.hidden);
  }

  @action
  activate(tabName) {
    this.defaultAction(tabName);
  }
}
