import Ember from 'ember';

const { Controller } = Ember;

export default Controller.extend({
  eventId: '12345678',
  headerItems: [
  { key: 'SERVICE', value: 'EMEA-Broker' },
  { key: 'EVENT ID', value: '12345678' },
  { key: 'EVENT TYPE', value: 'Network Session' },
  { key: 'FIRST PACKET TIME', value: '12:00' },
  { key: 'SIZE', value: '7 KB' },
  { key: 'SOURCE IP', value: '192.168.4.24' },
  { key: 'SOURCE PORT', value: '36033' },
  { key: 'DESTINATION IP', value: '192.168.4.24' },
  { key: 'DESTINATION PORT', value: '80' }
  ],
  title: 'Event Reconstruction (3 of 2567)'
});
