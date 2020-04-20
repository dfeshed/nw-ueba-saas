import classic from 'ember-classic-decorator';
import { classNames, layout as templateLayout } from '@ember-decorators/component';
import { action, computed } from '@ember/object';
import Component from '@ember/component';
import layout from './template';
import { assign } from '@ember/polyfills';


@classic
@templateLayout(layout)
@classNames('range-filter')
export default class RangeFilter extends Component {
  @computed('options')
  get filterValue() {
    const options = this.get('options');
    const { filterValue } = options;

    let { min: start, max: end } = options;
    if (filterValue && filterValue.length) {
      start = filterValue[0];
      end = filterValue[1];
    }
    return [start, end];
  }

  set filterValue(value) {
    return value;
  }

  init() {
    super.init(arguments);
    this.defaults = this.defaults || {
      filterValue: { },
      max: 100,
      min: 0,
      pips: {
        mode: 'values',
        values: [],
        density: 10
      }
    };
    const options = assign({}, this.get('defaults'), this.get('filterOptions'));
    this.set('options', options);
  }

  didReceiveAttrs() {
    super.didReceiveAttrs(...arguments);
    const isReset = this.get('isReset');
    if (isReset) {
      this.notifyPropertyChange('filterValue');
    }
  }

  @action
  onSliderChange(value) {
    const onChange = this.get('onChange');
    const name = this.get('options.name');
    if (onChange) {
      onChange({ name, operator: 'BETWEEN', value });
    }
  }
}