import Ember from 'ember';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import DatasourceList from 'context/config/im-columns';
import alertData from '../../../data/alert-data';

const {
  Helper
} = Ember;
const dataSourceEnabled = Helper.extend({
  dataSourceEnabled: () => {
    return true;
  },
  compute: () => {
    return ['LIST'];
  }
});

moduleForComponent('context-panel/body', 'Integration | Component | context-panel/body', {
  integration: true,
  beforeEach() {
    this.set('data-source-enabled', dataSourceEnabled);
    this.set('dataSourceEnabled', dataSourceEnabled);
    this.registry.register('helper:data-source-enabled', dataSourceEnabled);
    this.register('helper:dataSourceEnabled', dataSourceEnabled);
  }
});

test('it renders', function(assert) {
  this.set('alertsData', alertData);
  this.set('columns', DatasourceList);
  this.render(hbs`{{context-panel/body contextData=alertsData datasourceList=columns tabdata='overview'}}`);
  assert.equal(this.$('.rsa-data-table-header-cell').length, 6, 'Testing count of data header cells');
});
