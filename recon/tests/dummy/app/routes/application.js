import Ember from 'ember';
const { Route } = Ember;

export default Route.extend({
  model() {
    return {
      summary: [
        {
          name: 'device',
          value: 'devicename'
        },
        {
          name: 'session',
          value: '15641'
        },
        {
          name: 'type',
          value: 'network session'
        },
        {
          name: 'source',
          value: '192.168.58.6 : 49686'
        },
        {
          name: 'destination',
          value: '216.172.180.58 : 80'
        },
        {
          name: 'service',
          value: '80'
        },
        {
          name: 'first packet time',
          value: '2015-12-08 22:35:41.487'
        },
        {
          name: 'last packet time',
          value: '2015-12-08 22:35:50.124'
        },
        {
          name: 'packet size',
          value: '324,217 bytes'
        },
        {
          name: 'payload size',
          value: '297,401 bytes'
        },
        {
          name: 'packet count',
          value: '406'
        },
        {
          name: 'flags',
          value: 'Keep, Assembled, App Meta, Network Meta'
        }
      ]
    };
  }
});
