import classic from 'ember-classic-decorator';
import { classNames, layout as templateLayout } from '@ember-decorators/component';
import Component from '@ember/component';
import layout from './template';
import { set, action, computed } from '@ember/object';

@classic
@templateLayout(layout)
@classNames('list-filter')
export default class ListFilter extends Component {
  @computed('filterOptions')
  get filterValue() {
    const { filterValue, listOptions } = this.get('filterOptions');
    return listOptions.map((opt) => {
      const selected = filterValue && filterValue.includes(opt.name);
      return { ...opt, selected };
    });
  }

  set filterValue(value) {
    return value;
  }

  didReceiveAttrs() {
    super.didReceiveAttrs(...arguments);
    const isReset = this.get('isReset');
    if (isReset) {
      this.notifyPropertyChange('filterValue');
    }
  }

  @action
  toggleSelected(option, event) {
    if (event.charCode !== undefined && event.charCode !== 32) {
      return true;
    }
    const { selected } = option;
    const { name } = this.get('filterOptions');
    const onChange = this.get('onChange');

    set(option, 'selected', !selected);

    const selectedValues = this.get('filterValue').filterBy('selected', true);
    const value = selectedValues.mapBy('name');

    if (onChange) {
      onChange({ name, operator: 'IN', value });
    }
    event.preventDefault();
  }
}