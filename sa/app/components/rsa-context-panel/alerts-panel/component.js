import Ember from 'ember';
import AlertColumns from 'sa/context/alert-columns';
const {
    Component
    } = Ember;

export default Component.extend({
  classNames: 'rsa-context-panel__im',
  modulesColumnListConfig: AlertColumns
});
