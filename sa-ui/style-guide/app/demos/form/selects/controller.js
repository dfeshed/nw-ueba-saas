import Controller from '@ember/controller';

const options = [ 'Foo', 'Bar', 'Baz' ];

const defaultOptionsAsObjects = [
  { label: 'Foo Option', disabled: false, disabledTooltip: '' },
  { label: 'Bar Option', disabled: true, disabledTooltip: 'Bar Option is disabled because blah blah blah...' },
  { label: 'Baz Option', disabled: false, disabledTooltip: '' }
];

const groupedOptions = [
  {
    groupName: 'Group 1',
    options: [
      {
        code: '1.1',
        name: 'Item 1.1'
      },
      {
        code: '1.2',
        name: 'Item 1.2'
      }
    ]
  },
  {
    groupName: 'Group B',
    options: [
      {
        code: 'B.a',
        name: 'Item B.a'
      },
      {
        code: 'B.b',
        name: 'Item B.b'
      }
    ]
  },
  {
    code: 'C',
    name: 'Item C'
  }
];

export default Controller.extend({

  // used for the default-option example
  defaultOptionsAsObjects,
  defaultSelectedAsObject: null,

  selected: null,

  selections: null,

  options,

  groupedOptions,

  isDisabled: true,

  actions: {
    // used for the default-option example
    setSelectObject(val) {
      this.set('defaultSelectedAsObject', val);
    },

    setSelect(val) {
      this.set('selected', val);
    },

    setSelections(selections) {
      this.set('selections', selections);
    }
  }

});
