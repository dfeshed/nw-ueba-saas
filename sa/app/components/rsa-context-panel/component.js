import Ember from 'ember';
import DatasourceList from 'sa/context/datasource-list';

const {
    Component
} = Ember;

export default Component.extend({
  classNames: 'rsa-context-panel',

  contextData: null,
  columnHeader: {
    datasourceList: DatasourceList
  }

});
