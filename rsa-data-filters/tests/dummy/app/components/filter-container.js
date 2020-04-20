import Component from '@ember/component';

export default Component.extend({

  classNames: ['filter-container'],

  config: [
    {
      type: 'text',
      label: 'File Name'
    },
    {
      type: 'range',
      label: 'Size'
    },
    {
      type: 'list',
      label: 'File Status',
      listOptions: [
        { name: '1', label: 'One' },
        { name: '2', label: 'Two' },
        { name: '3', label: 'Three' },
        { name: '4', label: 'Four' },
        { name: '5', label: 'Five' }
      ]
    },
    {
      type: 'number',
      units: [
        {
          type: 'bytes',
          label: 'Bytes'
        },
        {
          type: 'MB',
          label: 'Mega Bytes'
        }
      ],
      label: 'Size'
    },
    {
      type: 'dropdown',
      label: 'Signature',
      multiSelect: true,
      listOptions: [
        { name: '1', label: 'One' },
        { name: '2', label: 'Two' },
        { name: '3', label: 'Three' },
        { name: '4', label: 'Four' },
        { name: '5', label: 'Five' }
      ]
    }
  ],

  actions: {
    onFilterChange() {

    }
  }
});
