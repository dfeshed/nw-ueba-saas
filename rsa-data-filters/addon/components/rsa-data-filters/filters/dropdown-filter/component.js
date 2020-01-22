import classic from 'ember-classic-decorator';
import { classNames, layout as templateLayout } from '@ember-decorators/component';
import { action, computed } from '@ember/object';
import { alias } from '@ember/object/computed';
import Component from '@ember/component';
import layout from './template';
import { assign } from '@ember/polyfills';

@classic
@templateLayout(layout)
@classNames('dropdown-filter')
export default class DropdownFilter extends Component {
  @alias('options.multiSelect')
  isMultiSelect;

  @computed('options', 'isMultiSelect')
  get filterValue() {
    const { filterValue, listOptions = [] } = this.get('options');
    const values = listOptions.filter((opt) => {
      return filterValue && filterValue.includes(opt.name);
    });
    return this.get('isMultiSelect') ? values : values[0];
  }

  set filterValue(key) {
    return key;
  }

  didReceiveAttrs() {
    super.didReceiveAttrs(...arguments);
    const isReset = this.get('isReset');
    if (isReset) {
      this.notifyPropertyChange('filterValue');
    }
  }

  init() {
    super.init(arguments);
    this.defaults = this.defaults || {
      multiSelect: false,
      searchEnabled: false,
      filterValue: []
    };
    const options = assign({}, this.get('defaults'), this.get('filterOptions'));
    this.set('options', options);
  }

  @action
  changeOption(option) {
    const { name } = this.get('filterOptions');
    const onChange = this.get('onChange');
    const isMultiSelect = this.get('isMultiSelect');

    let value;

    if (option) {
      if (isMultiSelect) {
        value = option.mapBy('name');
      } else {
        value = [option.name];
      }
    }

    this.set('filterValue', option);

    if (onChange) {
      onChange({ name, operator: 'IN', value });
    }
  }
}