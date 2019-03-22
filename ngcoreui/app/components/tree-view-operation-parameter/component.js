import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { updateParameter } from 'ngcoreui/actions/actions';

const dispatchToActions = {
  updateParameter
};

const defaultValue = (type) => {
  if (type.isEnumAny) {
    return [];
  } else if (type.isBoolean) {
    return false;
  } else {
    return '';
  }
};

const treeViewOperationParameter = Component.extend({
  // Passed in, contains meta about the param
  param: null,
  // Action function to call when the value of this param changes
  paramUpdateAction: null,

  value: null,
  selections: null,
  dateTime: null,

  // Necessary to pass to the date-time picker because there is no
  // dateFormat service in this app
  dateFormat: {
    selected: {
      key: 'MM/dd/yyyy'
    }
  },

  @computed('param')
  type: (param) => ({
    isText: param.type === 'text',
    isNumber: param.type === 'number',
    isBoolean: param.type === 'boolean',
    isEnumAny: param.type === 'enum-any',
    isEnumOne: param.type === 'enum-one',
    isDateTime: param.type === 'date-time'
  }),

  actions: {
    setSelect(item) {
      this.set('value', item);
      this.send('updateValue');
    },

    setDateTime(value) {
      // value is an array of JS Date objects
      this.set('dateTime', value);
      // Convert from JS time to UNIX/epoc time
      this.set('value', Math.floor(value[0].getTime() / 1000).toString());
      this.send('updateValue');
    },

    setSelections(items) {
      // power-select-multiple can return duplicates,
      // but we don't want them here. This removes them.
      items = items.filter((item, index) => {
        return items.lastIndexOf(item, index - items.length - 1) < 0;
      });
      this.set('selections', items);
      items = items.map((item) => {
        return item.name;
      });
      this.set('value', items.join(','));
      this.send('updateValue');
    },

    updateValue() {
      const param = this.get('param');
      let value = this.get('value');
      if (!value) {
        const type = this.get('type');
        value = defaultValue(type);
      }
      this.get('paramUpdateAction')({
        name: param.name,
        value
      });
    },

    toggleValue() {
      const newBool = this.toggleProperty('bool');
      this.set('value', newBool.toString());
      this.send('updateValue');
    },

    deleteOrHideSelf() {
      const { optional } = this.get('param');
      if (optional) {
        this.send('updateParameter', {
          name: this.get('param').name,
          method: 'hide'
        });

      } else {
        this.send('updateParameter', {
          name: this.get('param').name,
          method: 'delete'
        });
      }
    }
  }
});

export default connect(undefined, dispatchToActions)(treeViewOperationParameter);
