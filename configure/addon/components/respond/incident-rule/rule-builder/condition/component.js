import Component from '@ember/component';
import { inject as service } from '@ember/service';
import computed, { alias } from 'ember-computed-decorators';
import operators from 'configure/utils/rules/operators';
import moment from 'moment';
import { connect } from 'ember-redux';
import { updateCondition, removeCondition } from 'configure/actions/creators/respond/incident-rule-creators';
import { getConditionFields } from 'configure/reducers/respond/incident-rules/rule/selectors';
import { isBlank } from '@ember/utils';

const { text, number, date, category } = operators;

const stateToComputed = (state) => {
  return {
    fields: getConditionFields(state)
  };
};

const dispatchToActions = function(dispatch) {
  return {
    updateCondition: (changes) => {
      const conditionId = this.get('info.id');
      dispatch(updateCondition(conditionId, changes));
    },
    removeCondition: (conditionId) => {
      dispatch(removeCondition(conditionId));
    }
  };
};

const Condition = Component.extend({
  classNames: ['rsa-rule-condition'],

  info: null,

  timezone: service(),

  @computed('selectedField.type')
  operators(dataType) {
    let operators;

    switch (dataType) {
      case 'textfield':
        operators = text;
        break;
      case 'numberfield':
        operators = number;
        break;
      case 'datefield':
        operators = date;
        break;
      default:
        operators = category;
    }
    return operators;
  },

  @computed('info.property', 'fields')
  selectedField(fieldName, fields) {
    return fields.findBy('value', fieldName);
  },

  @computed('info.value', 'selectedField.list')
  selectedCategory(value, list) {
    return list.findBy('value', value);
  },

  @computed('info.value')
  localizedDate(value) {
    const timezoneOffset = this.get('timezone.selected.offset');
    const dateValue = moment(value).utcOffset(timezoneOffset).format('YYYY-MM-DD HH:mm:ss');
    return moment(dateValue).valueOf();
  },

  @alias('info.operator') selectedOperator: null,

  actions: {
    handleConditionFieldChange(field) {
      this.send('updateCondition', {
        property: field.value,
        operator: this.get('operators')[0],
        value: null
      });
    },
    handleConditionOperatorChange(operator) {
      this.send('updateCondition', { operator });
    },
    handleCategoryChange(option) {
      this.send('updateCondition', { value: option.value });
    },
    handleInputChange(val) {
      let value = val;
      // if the condition's field is a number, make sure we cast to the proper numerical value, otherwise the value will
      // remain a string (e.g., "50" instead of 50)
      if (this.get('selectedField').type === 'numberfield') {
        // ensure a blank value is treated as null otherwise parseFloat will result in NaN
        value = isBlank(value) ? null : parseFloat(value);
      }
      this.send('updateCondition', { value });
    },
    handleDateChange(value) {
      const timezoneOffset = this.get('timezone.selected.offset');
      const dateValue = moment(value[0]).utcOffset(timezoneOffset, true).utc().format();
      this.send('updateCondition', { value: dateValue });
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(Condition);