import Component from 'ember-component';
import layout from './template';
import connect from 'ember-redux/components/connect';
import { storyDatasheet } from 'respond/selectors/storyline';
import computed from 'ember-computed-decorators';

const stateToComputed = (state) => ({
  items: storyDatasheet(state),
  storyline: state.respond.incident.storyline,
  selection: state.respond.incident.selection
});

const IncidentDatasheet = Component.extend({
  tagName: '',
  layout,
  items: null,
  selection: null,

  columnsConfig: [{
    field: 'timestamp',
    title: 'respond.eventsTable.time',
    width: 150,
    visible: true
  }, {
    field: 'type',
    title: 'respond.eventsTable.type',
    visible: true
  }, {
    field: 'source.device.ip_address',
    title: 'respond.eventsTable.sourceIP',
    visible: true
  }, {
    field: 'source.device.dns_hostname',
    title: 'respond.eventsTable.sourceHost',
    visible: false
  }, {
    field: 'source.device.mac_address',
    title: 'respond.eventsTable.sourceMAC',
    visible: false
  }, {
    field: 'destination.device.ip_address',
    title: 'respond.eventsTable.destinationIP',
    visible: true
  }, {
    field: 'destination.device.dns_hostname',
    title: 'respond.eventsTable.destinationHost',
    visible: false
  }, {
    field: 'destination.device.mac_address',
    title: 'respond.eventsTable.destinationMAC',
    visible: false
  }, {
    field: 'detector.ip_address',
    title: 'respond.eventsTable.detectorIP',
    visible: true
  }, {
    field: 'source.user.username',
    title: 'respond.eventsTable.sourceUser',
    visible: true
  }, {
    field: 'destination.user.username',
    title: 'respond.eventsTable.destinationUser',
    visible: true
  }, {
    field: 'data.[0].filename',
    title: 'respond.eventsTable.fileName',
    visible: true
  }, {
    field: 'data.[0].hash',
    title: 'respond.eventsTable.fileHash',
    width: 150,
    visible: true
  }],

  @computed('selection.{type,ids}', 'storyline.[]')
  selectedIndicatorName(type, ids, storyline) {
    if (type === 'storyPoint') {
      const indicator = storyline.findBy('id', ids[0]);
      return indicator && indicator.alert && indicator.alert.name;
    } else {
      return '';
    }
  }
});

export default connect(stateToComputed, undefined)(IncidentDatasheet);
