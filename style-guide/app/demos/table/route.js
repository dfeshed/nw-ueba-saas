import EmberObject from '@ember/object';
import Route from '@ember/routing/route';

export default Route.extend({
  columnsWithCheckboxConfig: [
    EmberObject.create({
      title: '',
      class: 'rsa-form-row-checkbox',
      width: '18',
      dataType: 'checkbox',
      componentClass: 'rsa-form-checkbox',
      visible: true,
      disableSort: true,
      headerComponentClass: 'rsa-form-checkbox'
    }),
    EmberObject.create({
      field: 'foo',
      title: 'Foo Column',
      disableSort: true
    }),
    EmberObject.create({
      field: 'bar',
      title: 'Bar Column'
    }),
    EmberObject.create({
      field: 'baz',
      title: 'Baz Column'
    })
  ],
  columnsWithoutCheckboxConfig: [
    EmberObject.create({
      field: 'foo',
      title: 'Foo Column',
      disableSort: true
    }),
    EmberObject.create({
      field: 'bar',
      title: 'Bar Column'
    }),
    EmberObject.create({
      field: 'baz',
      title: 'Baz Column'
    })
  ],
  columnsWithWidthConfig: [
    EmberObject.create({
      field: 'foo',
      title: 'Foo Column',
      width: 350,
      disableSort: true
    }),
    EmberObject.create({
      field: 'bar',
      title: 'Bar Column',
      width: 150
    }),
    EmberObject.create({
      field: 'baz',
      title: 'Baz Column',
      width: 150
    })
  ],
  emptyItems: [],
  items: [
    EmberObject.create({
      foo: 'Really really really very very long Test Foo 1',
      bar: 'Test Bar 1',
      baz: 'Test Baz 1'
    }),
    EmberObject.create({
      foo: 'Test Foo 2',
      bar: 'Test Bar 2',
      baz: 'Test Baz 2'
    }),
    EmberObject.create({
      foo: 'Test Foo 3',
      bar: 'Test Bar 3',
      baz: 'Test Baz 3'
    })
  ],
  sortableItems: [
    EmberObject.create({
      foo: 'Test Foo 1',
      bar: 1,
      baz: 3
    }),
    EmberObject.create({
      foo: 'Test Foo 2',
      bar: 2,
      baz: 2
    }),
    EmberObject.create({
      foo: 'Test Foo 3',
      bar: 3,
      baz: 1
    })
  ],
  model() {
    return {
      columnsWithCheckboxConfig: this.columnsWithCheckboxConfig,
      columnsWithoutCheckboxConfig: this.columnsWithoutCheckboxConfig,
      columnsWithWidthConfig: this.columnsWithWidthConfig,
      emptyItems: this.emptyItems,
      items: this.items,
      sortableItems: this.sortableItems,
      title: 'Table',
      subtitle: 'An all purpose table component',
      description: '',
      testFilter: 'rsa-data-table',
      jsRepo: 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/components/rsa-data-table/',
      styleRepo: 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/app/styles/component-lib/base/content/_data-table.scss',
      templateRepo: 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/components/rsa-data-table/'
    };
  }
});
