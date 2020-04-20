import classic from 'ember-classic-decorator';
import { classNames } from '@ember-decorators/component';
import { alias } from '@ember/object/computed';
import Component from '@ember/component';
import { get, set, action, computed } from '@ember/object';
import { htmlSafe } from '@ember/string';

const BASE_PADDING = 30;

@classic
@classNames('process-name-column')
class ProcessName extends Component {
  @alias('item.expanded')
  isExpanded;

  @computed('item')
  get style() {
    const left = BASE_PADDING * this.item.level;
    return htmlSafe(`padding-left: ${left}px;`);
  }

  @action
  toggleExpand() {
    const { item, index } = this.getProperties('item', 'index');
    set(item, 'expanded', !get(item, 'expanded'));
    this.onToggleExpand(index, item.level, item);
  }
}

export default ProcessName;
