import Ember from 'ember';
const {
  Object: EmberObject,
  Route
} = Ember;

export default Route.extend({
  columnsWithCheckboxConfig: [
    EmberObject.create({
      title: '',
      class: 'rsa-form-row-checkbox',
      width: '21',
      dataType: 'checkbox',
      componentClass: 'rsa-form-checkbox',
      visible: true,
      disableSort: true,
      headerComponentClass: 'rsa-form-checkbox'
    }),
    EmberObject.create({
      field: 'foo',
      title: 'Foo Column'
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
      title: 'Foo Column'
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
      width: 350
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
  model() {
    return {
      columnsWithCheckboxConfig: this.columnsWithCheckboxConfig,
      columnsWithoutCheckboxConfig: this.columnsWithoutCheckboxConfig,
      columnsWithWidthConfig: this.columnsWithWidthConfig,
      items: this.items,
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
