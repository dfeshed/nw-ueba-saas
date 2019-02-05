import Component from '@ember/component';
import { inject as service } from '@ember/service';
import computed from 'ember-computed-decorators';
import { COLUMNS_CONFIG } from './columnsConfig';

export default Component.extend({
  label: null,
  animate: true,

  tagName: 'vbox',
  classNames: ['dashboard-banner'],

  moduleName: null,
  devicesStreamHandle: null,
  devices: null,
  data: null,

  intervalHandle: null,

  xProp: 'time_',
  yProp: 'session_rate',

  domainExtents: {
    x: { fixed: [ 0, 30000 ] },
    y: { fixed: [ 0 ] }
  },

  transport: service(),

  columns: COLUMNS_CONFIG,

  formatters: COLUMNS_CONFIG.reduce((formatters, column) => {
    if (column.dataType === 'numeric') {
      formatters[column.field] = parseFloat;
    } else if (column.field === 'time_last') {
      // time.last is seconds since epoch as a string
      formatters[column.field] = (v) => new Date(parseFloat(v) * 1000).toLocaleString('en-US', { hour12: false });
    }
    return formatters;
  }, {}),

  actions: {
    toggleColumnSelection(field) {
      this.set('yProp', field);
    }
  },

  init() {
    this._super(...arguments);

    this.setProperties({ devices: [], data: [] });
    const path = `/${this.moduleName}/devices`;
    this.devicesStreamHandle = this.transport.stream({
      path,
      message: {
        message: 'mon',
        params: { depth: '1' }
      },
      messageCallback: (message) => {
        const nodes = message.nodes || (message.node ? [ message.node ] : []);
        nodes.forEach((node) => {
          if (node.action && node.action === 'added') {
            this.addDevice(node);
          } else if (node.action && node.action === 'deleted') {
            this.deleteDevice(node);
          } else if (node.path !== path) {
            // this is the initial state, add device
            this.addDevice(node);
          }
        });
      },
      errorCallback: () => {
        throw new Error('Unexpected transport API error');
      }
    });

    // start the interval to update series data
    this.set('intervalHandle', setInterval(() => this.updateSeries(), 1000));
  },

  addDevice(node) {
    const { name } = node;
    // const devicePath = `${message.node.path}/stats`;
    const { data, devices } = this.getProperties('data', 'devices');
    if (devices.find((device) => device.name === name)) {
      // don't allow duplicate devices!
      return;
    }
    const streamHandle = this.monitorDeviceStats(name);
    const device = { name, streamHandle, values: {} };
    this.set('devices', devices.concat(device));

    // append empty data series to ensure devices and data indices remain consistent
    this.set('data', data.concat([]));
  },

  deleteDevice(node) {
    const { data, devices } = this.getProperties('data', 'devices');
    const index = devices.findIndex((device) => device.name === node.name);
    if (index !== -1) {
      const device = devices[index];

      // remove both the device and the its series data
      this.set('devices', devices.filter((d, i) => index === i));
      this.set('data', data.filter((d, i) => index === i));

      // stop the stream
      this.get('transport').stopStream(device.streamHandle);
    }
  },

  monitorDeviceStats(deviceName) {
    const deviceStatsPath = `/${this.moduleName}/devices/${deviceName}/stats`;
    const transport = this.get('transport');
    return transport.stream({
      path: deviceStatsPath,
      message: {
        message: 'mon',
        params: { depth: '1' }
      },
      messageCallback: (message) => {
        const nodes = message.nodes || (message.node ? [ message.node ] : []);
        const devices = this.get('devices');
        const formatters = this.get('formatters');
        const updated = devices.map((device) => {
          if (device.name === deviceName) {
            nodes.forEach((node) => {
              const name = node.name.replace('.', '_');
              const value = formatters[name] ? formatters[name](node.value) : node.value;
              device.values[name] = value;
            });
          }
          return device;
        });

        this.set('devices', updated);
      },
      errorCallback: () => {
        throw new Error('Unexpected transport API error');
      }
    });
  },

  willDestroy() {
    const { intervalHandle, transport, devicesStreamHandle, devices } =
      this.getProperties('intervalHandle', 'transport', 'devicesStreamHandle', 'devices');
    clearInterval(intervalHandle);
    transport.stopStream(devicesStreamHandle);
    devices.forEach((device) => transport.stopStream(device.streamHandle));
  },

  @computed('devices')
  items: (devices) => {
    return devices.map((d) => {
      return { ...d.values, device: d.name };
    });
  },

  updateSeries() {
    const time_ = Date.now();
    const { data, devices } = this.getProperties('data', 'devices');
    const series = devices.map((device, index) => {
      return (data[index] || []).concat({ ...device.values, time_ }).slice(-301);
    });
    this.set('data', series);
    // force y-axis to always show 0 and x axis to show 5 minutes
    this.set('domainExtents', {
      y: { fixed: [0] },
      x: { fixed: [time_ - 300 * 1000, time_] }
    });
  }

});
