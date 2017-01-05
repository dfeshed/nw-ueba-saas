import Ember from 'ember';
const { Route } = Ember;

export default Route.extend({
  columnsConfig: [
    {
      field: 'foo',
      title: 'Foo Column'
    },
    {
      field: 'bar',
      title: 'Bar Column'
    },
    {
      field: 'baz',
      title: 'Baz Column'
    }
  ],
  items: [
    {
      foo: 'Test Foo 1',
      bar: 'Test Bar 1',
      baz: 'Test Baz 1'
    },
    {
      foo: 'Test Foo 2',
      bar: 'Test Bar 2',
      baz: 'Test Baz 2'
    },
    {
      foo: 'Test Foo 3',
      bar: 'Test Bar 3',
      baz: 'Test Baz 3'
    }
  ],
  model() {
    return {
      columnsConfig: this.columnsConfig,
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
