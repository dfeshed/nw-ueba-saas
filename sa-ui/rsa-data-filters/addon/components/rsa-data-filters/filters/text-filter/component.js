import classic from 'ember-classic-decorator';
import { classNames, layout as templateLayout } from '@ember-decorators/component';
import { action, computed } from '@ember/object';
import { inject as service } from '@ember/service';
import Component from '@ember/component';
import layout from './template';
import { assign } from '@ember/polyfills';
import { isEmpty } from '@ember/utils';
import { debounce } from '@ember/runloop';

@classic
@templateLayout(layout)
@classNames('text-filter')
export default class TextFilter extends Component {
  @service
  i18n;

  isError = false;
  errorMessage = '';

  @computed('options')
  get filterValue() {
    const { filterValue: { operator, value }, operators, useI18N } = this.get('options');
    let { placeholder } = this.get('options');
    const selectedOperator = operators.findBy('type', operator);
    const val = value.join('||');
    placeholder = useI18N ? this.i18n.t(placeholder) : placeholder;
    const placeholderText = placeholder ? placeholder : this.i18n.t('dataFilters.textPlaceholder');
    return { operator: selectedOperator, value: val, placeholderText };
  }

  set filterValue(value) {
    return value;
  }

  init() {
    super.init(arguments);
    this.defaults = this.defaults || {
      maxLength: 256,
      filterOnBlur: false,
      operators: [
        { type: 'IN', label: 'dataFilters.label.equals' },
        { type: 'LIKE', label: 'dataFilters.label.contains' }
      ],
      filterValue: {
        operator: 'IN',
        value: []
      }
    };
    const options = assign({}, this.get('defaults'), this.get('filterOptions'));
    const { filterValue } = options;
    this.set('value', filterValue);
    this.set('options', options);
    if (this.get('validations') === undefined) {
      this.validations = {};
    }
  }

  didReceiveAttrs() {
    const isReset = this.get('isReset');
    if (isReset) {
      this.notifyPropertyChange('filterValue');
      this.set('isError', false);
      this.set('errorMessage', '');
    }
  }

  _validate() {
    let property;
    const value = this.get('filterValue.value');

    if (isEmpty(value.trim())) {
      return { isValid: true };
    }

    const operator = this.get('filterValue.operator.type');
    const validations = this.get('options.validations');
    for (property in validations) {
      const validatorObject = validations[property];
      if (validatorObject.constructor === Object) {
        const { validator, message, exclude = [] } = validatorObject;
        if (!exclude.includes(operator)) {
          const isInvalid = validator(value);
          if (isInvalid) {
            return {
              isValid: false,
              message
            };
          }
        }
      }
    }
    return { isValid: true };
  }

  _onFilterChange() {
    const { filterValue: { value, operator: { type } }, options: { name }, onChange } = this.getProperties('filterValue', 'options', 'onChange');
    if (onChange) {
      let val = [];
      if (!isEmpty(value)) {
        val = value.split('||');
      }
      onChange({ name, operator: type, value: val });
    }
  }

  _handleFilterChanged(value) {
    this.set('filterValue.value', value);
    const { isValid, message } = this._validate();
    if (isValid) {
      this.set('isError', false);
      this._onFilterChange();
    } else {
      this.set('isError', true);
      this.set('errorMessage', message);
    }
  }

  @action
  handleKeyUp(value = '') {
    debounce(this, this._handleFilterChanged, value, 600);
  }

  @action
  changeOperator(option) {
    this.set('filterValue.operator', option);
    if (!this.get('isError')) {
      this._onFilterChange();
    }
  }
}