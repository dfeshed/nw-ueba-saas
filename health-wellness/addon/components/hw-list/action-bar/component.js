import Component from '@ember/component';
import { action } from '@ember/object';
import { classNames, layout as templateLayout } from '@ember-decorators/component';
import classic from 'ember-classic-decorator';
import layout from './template';

@classic
@classNames('action-bar')
@templateLayout(layout)
export default class ActionBar extends Component {

  isExpanded = false;

  @action
  toggleExpand() {
    this.toggleProperty('isExpanded');
  }
}