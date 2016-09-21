import Ember from 'ember';
import IncidentColumns from 'sa/context/incident-columns';
const {
    Component
    } = Ember;
export default Component.extend({
  classNames: 'rsa-context-panel__im',
  incidentsColumnListConfig: IncidentColumns
});
