import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';

let setState;

module('Integration | Component | process-details/events-table/table', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-process-analysis')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('process-events-table renders', async function(assert) {
    const eventsData = [
      {
        sessionId: 45328,
        time: 1525950159000,
        metas: null,
        sessionid: 45328,
        size: 41,
        'forward.ip': '10.40.12.6',
        'ip.all': '10.40.12.59',
        medium: 32,
        'device.type': 'nwendpoint',
        features: 'windows',
        checksum: '08c450382abdc53a93590df05b884e12',
        directory: 'C:\\Windows\\Microsoft.NET\\Framework64\\v4.0.30319\\',
        filename: 'mscoreei.dll',
        'filename.all': 'Launcher.exe',
        extension: 'dll',
        'file.attributes': 'archive',
        'directory.src': 'C:\\Users\\ecat\\Desktop\\lab\\',
        'filename.src': 'Launcher.exe',
        category: 'Dll',
        'nwe.callback_id': 'nwe-call-back-id-here',
        OS: 'Microsoft Windows 10 Enterprise',
        'alias.ip': '10.40.12.59',
        netname: 'private misc',
        agentId: 'FC48BDDF-9BA7-C3D2-D072-62025DCC968E',
        'agent.id': 'FC48BDDF-9BA7-C3D2-D072-62025DCC968E',
        'alias.host': 'DESKTOP-OC7FKKS',
        'host.all': 'DESKTOP-OC7FKKS',
        'event.time': 1525974660000,
        'msg.id': 'nwendpoint',
        'device.disc': 30,
        'device.disc.type': 'nwendpoint',
        kig_thread: '0',
        did: 'nodex',
        rid: 45328,
        childCount: 0,
        id: 'event_3'
      },
      {
        sessionId: 45337,
        time: 1525950159000,
        metas: null,
        sessionid: 45337,
        size: 41,
        'forward.ip': '10.40.12.6',
        'ip.all': '10.40.12.59',
        medium: 32,
        'device.type': 'nwendpoint',
        features: 'installer',
        checksum: '08c450382abdc53a93590df05b884e12',
        directory: 'C:\\Program Files\\ESET\\ESET Endpoint Antivirus\\',
        filename: 'eplgHooks.dll',
        'filename.all': 'Launcher.exe',
        extension: 'dll',
        'file.attributes': 'archive',
        'directory.src': 'C:\\Users\\ecat\\Desktop\\lab\\',
        'filename.src': 'Launcher.exe',
        category: 'Dll',
        'nwe.callback_id': 'nwe-call-back-id-here',
        OS: 'Microsoft Windows 10 Enterprise',
        'alias.ip': '10.40.12.59',
        netname: 'private misc',
        agentId: 'FC48BDDF-9BA7-C3D2-D072-62025DCC968E',
        'agent.id': 'FC48BDDF-9BA7-C3D2-D072-62025DCC968E',
        'alias.host': 'DESKTOP-OC7FKKS',
        'host.all': 'DESKTOP-OC7FKKS',
        'event.time': 1525974660000,
        'msg.id': 'nwendpoint',
        'device.disc': 30,
        'device.disc.type': 'nwendpoint',
        kig_thread: '0',
        did: 'nodex',
        rid: 45337,
        childCount: 0,
        id: 'event_4'
      }];

    new ReduxDataHelper(setState).eventsData(eventsData).build();
    const timezone = this.owner.lookup('service:timezone');
    const timeFormat = this.owner.lookup('service:timeFormat');
    const dateFormat = this.owner.lookup('service:dateFormat');

    timezone.set('_selected', { zoneId: 'UTC' });
    timeFormat.set('_selected', { format: 'hh:mm:ss' });
    dateFormat.set('_selected', { format: 'YYYY-MM-DD' });

    await render(hbs`{{process-details/events-table/table}}`);
    assert.equal(findAll('.rsa-data-table-body-row').length, 2, 'Expected to render 2 rows');
  });
});
