import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Immutable from 'seamless-immutable';

const selectors = {
  header: '.endpoint-detail-header',
  eventTime: '.event-time span.datetime',
  hostName: '.hostname',
  user: '.user',
  description: '.endpoint-detail-description span'
};

function prepareDesription(descriptionTags) {
  let description = '';
  descriptionTags.forEach((item) => {
    description += `${item.textContent.trim()} `;
  });
  return description.trim();
}

module('Integration | Component | recon-event-detail/endpoint', function(hooks) {
  setupRenderingTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('renders Process Event view with no filename', async function(assert) {
    const state = {
      recon: {
        meta: {
          meta: [
            ['sessionid', 1],
            ['event.time', '2019-01-14T06:55:07.000+0000'],
            ['alias.host', 'urdhwa-t1'],
            ['category', 'Process Event'],
            ['action', 'createProcess'],
            ['filename.dst', 'msiexec.exe'],
            ['user.src', 'URDHWA-T1\\admin']
          ]
        }
      }
    };
    patchReducer(this, Immutable.from(state));
    await render(hbs`{{recon-event-detail/endpoint}}`);
    assert.equal(find(selectors.header).textContent.trim(), 'Process Event', 'Category is process event');
    assert.equal(find(selectors.hostName).textContent.trim(), 'urdhwa-t1', 'hostName is correct');
    assert.equal(find(selectors.user).textContent.trim(), 'URDHWA-T1\\admin', 'user is rendered');

    const descriptionTags = findAll(selectors.description);
    assert.equal(prepareDesription(descriptionTags),
      'performed createProcess  msiexec.exe', 'text is rendered correctly');
  });

  test('renders Process Event view with non script file when multiple filename.src present', async function(assert) {
    const state = {
      recon: {
        meta: {
          meta: [
            ['sessionid', 1],
            ['event.time', '2019-01-14T06:55:07.000+0000'],
            ['alias.host', 'urdhwa-t1'],
            ['category', 'Process Event'],
            ['filename.src', 'cmd.exe'],
            ['filename.src', 'text.exe'],
            ['action', 'createProcess'],
            ['filename.dst', 'msiexec.exe'],
            ['user.src', 'URDHWA-T1\\admin']
          ]
        }
      }
    };
    patchReducer(this, Immutable.from(state));
    await render(hbs`{{recon-event-detail/endpoint}}`);
    assert.equal(find(selectors.header).textContent.trim(), 'Process Event', 'Category is process event');
    assert.equal(find(selectors.hostName).textContent.trim(), 'urdhwa-t1', 'hostName is correct');
    assert.equal(find(selectors.user).textContent.trim(), 'URDHWA-T1\\admin', 'user is rendered');

    const descriptionTags = findAll(selectors.description);
    assert.equal(prepareDesription(descriptionTags),
      'text.exe performed createProcess  msiexec.exe', 'text is rendered correctly');
  });

  test('renders Process Event view when single filename.src present which is a script file', async function(assert) {
    const state = {
      recon: {
        meta: {
          meta: [
            ['sessionid', 1],
            ['event.time', '2019-01-14T06:55:07.000+0000'],
            ['alias.host', 'urdhwa-t1'],
            ['category', 'Process Event'],
            ['filename.src', 'cmd.exe'],
            ['action', 'createProcess'],
            ['filename.dst', 'msiexec.exe'],
            ['user.src', 'URDHWA-T1\\admin']
          ]
        }
      }
    };
    patchReducer(this, Immutable.from(state));
    await render(hbs`{{recon-event-detail/endpoint}}`);
    assert.equal(find(selectors.header).textContent.trim(), 'Process Event', 'Category is process event');
    assert.equal(find(selectors.hostName).textContent.trim(), 'urdhwa-t1', 'hostName is correct');
    assert.equal(find(selectors.user).textContent.trim(), 'URDHWA-T1\\admin', 'user is rendered');

    const descriptionTags = findAll(selectors.description);
    assert.equal(prepareDesription(descriptionTags),
      'cmd.exe performed createProcess  msiexec.exe', 'text is rendered correctly');
  });

  test('renders Process Event view when single filename.src present which is not a script file', async function(assert) {
    const state = {
      recon: {
        meta: {
          meta: [
            ['sessionid', 1],
            ['event.time', '2019-01-14T06:55:07.000+0000'],
            ['alias.host', 'urdhwa-t1'],
            ['category', 'Process Event'],
            ['filename.src', 'test.exe'],
            ['action', 'createProcess'],
            ['filename.dst', 'msiexec.exe'],
            ['user.src', 'URDHWA-T1\\admin']
          ]
        }
      }
    };
    patchReducer(this, Immutable.from(state));
    await render(hbs`{{recon-event-detail/endpoint}}`);
    assert.equal(find(selectors.header).textContent.trim(), 'Process Event', 'Category is process event');
    assert.equal(find(selectors.hostName).textContent.trim(), 'urdhwa-t1', 'hostName is correct');
    assert.equal(find(selectors.user).textContent.trim(), 'URDHWA-T1\\admin', 'user is rendered');

    const descriptionTags = findAll(selectors.description);
    assert.equal(prepareDesription(descriptionTags),
      'test.exe performed createProcess  msiexec.exe', 'text is rendered correctly');
  });

  test('renders File view', async function(assert) {
    const state = {
      recon: {
        meta: {
          meta: [
            ['sessionid', 1],
            ['event.time', '2019-01-14T06:55:07.000+0000'],
            ['alias.host', 'urdhwa-t1'],
            ['category', 'File'],
            ['filename', 'cmd.exe'],
            ['directory', 'C:\\Users\\']
          ]
        }
      }
    };
    patchReducer(this, Immutable.from(state));
    await render(hbs`{{recon-event-detail/endpoint}}`);
    assert.equal(find(selectors.header).textContent.trim(), 'File', 'Category is File');
    assert.equal(find(selectors.hostName).textContent.trim(), 'urdhwa-t1', 'hostName is correct');

    const descriptionTags = findAll(selectors.description);
    assert.equal(prepareDesription(descriptionTags), 'cmd.exe present in C:\\Users\\', 'text is rendered correctly');
  });

  test('renders Network Event view', async function(assert) {
    const state = {
      recon: {
        meta: {
          meta: [
            ['sessionid', 1],
            ['event.time', '2019-01-14T06:55:07.000+0000'],
            ['alias.host', 'urdhwa-t1'],
            ['user.src', 'NT AUTHORITY\\SYSTEM'],
            ['category', 'Network Event'],
            ['filename.src', 'ntoskrnl.exe'],
            ['ip.dst', '10.40.15.255'],
            ['ip.src', '10.40.14.18'],
            ['domain.dst', 'update.nai.com'],
            ['action', 'createProcess'],
            ['directory', 'C:\\Users\\']
          ]
        }
      }
    };
    patchReducer(this, Immutable.from(state));
    await render(hbs`{{recon-event-detail/endpoint}}`);
    assert.equal(find(selectors.header).textContent.trim(), 'Network Event', 'Category is Network Event');
    assert.equal(find(selectors.hostName).textContent.trim(), 'urdhwa-t1', 'hostName is correct');

    const descriptionTags = findAll(selectors.description);
    assert.equal(prepareDesription(descriptionTags),
      'ntoskrnl.exe made a network connection to 10.40.15.255 resolving to update.nai.com from 10.40.14.18',
      'text is rendered correctly');
  });

  test('renders Network Event view ipv6', async function(assert) {
    const state = {
      recon: {
        meta: {
          meta: [
            ['sessionid', 1],
            ['event.time', '2019-01-14T06:55:07.000+0000'],
            ['alias.host', 'urdhwa-t1'],
            ['user.src', 'NT AUTHORITY\\SYSTEM'],
            ['category', 'Network Event'],
            ['filename.src', 'ntoskrnl.exe'],
            ['ipv6.dst', 'fe80::48f7:f425:19fa:aa0d'],
            ['ipv6.src', 'fe80::514d:198f:224e:b089'],
            ['domain.dst', 'update.nai.com'],
            ['action', 'createProcess'],
            ['directory', 'C:\\Users\\']
          ]
        }
      }
    };
    patchReducer(this, Immutable.from(state));
    await render(hbs`{{recon-event-detail/endpoint}}`);
    assert.equal(find(selectors.header).textContent.trim(), 'Network Event', 'Category is Network Event');
    assert.equal(find(selectors.hostName).textContent.trim(), 'urdhwa-t1', 'hostName is correct');
    const descriptionTags = findAll(selectors.description);
    assert.equal(prepareDesription(descriptionTags),
      'ntoskrnl.exe made a network connection to fe80::48f7:f425:19fa:aa0d resolving to update.nai.com from fe80::514d:198f:224e:b089',
      'text is rendered correctly');
  });

  test('renders Registry Event view', async function(assert) {
    const state = {
      recon: {
        meta: {
          meta: [
            ['sessionid', 1],
            ['event.time', '2019-01-14T06:55:07.000+0000'],
            ['alias.host', 'urdhwa-t1'],
            ['user.src', 'NT AUTHORITY\\SYSTEM'],
            ['category', 'Registry Event'],
            ['filename.src', 'dtf.exe'],
            ['action', 'modifyRegistryValue'],
            ['registry.key', 'HKU\\S-1-5-21-3941629173-3378368440-660712258-1001\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\@WarnOnBadCertRecving']
          ]
        }
      }
    };
    patchReducer(this, Immutable.from(state));
    await render(hbs`{{recon-event-detail/endpoint}}`);
    assert.equal(find(selectors.header).textContent.trim(), 'Registry Event', 'Category is File');
    assert.equal(find(selectors.hostName).textContent.trim(), 'urdhwa-t1', 'hostName is correct');
    const descriptionTags = findAll(selectors.description);
    assert.equal(prepareDesription(descriptionTags),
      'dtf.exe modifyRegistryValue HKU\\S-1-5-21-3941629173-3378368440-660712258-1001\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\@WarnOnBadCertRecving',
      'text is rendered correctly');
  });

  test('renders Autorun view', async function(assert) {
    const state = {
      recon: {
        meta: {
          meta: [
            ['sessionid', 1],
            ['event.time', '2019-01-14T06:55:07.000+0000'],
            ['alias.host', 'urdhwa-t1'],
            ['category', 'Autorun'],
            ['filename', 'vsocklib.dll']
          ]
        }
      }
    };
    patchReducer(this, Immutable.from(state));
    await render(hbs`{{recon-event-detail/endpoint}}`);
    assert.equal(find(selectors.header).textContent.trim(), 'Autorun', 'Category is Autorun');
    const descriptionTags = findAll(selectors.description);
    assert.equal(prepareDesription(descriptionTags), 'vsocklib.dll Set as Autorun', 'text is rendered correctly');
  });

  test('renders Service view', async function(assert) {
    const state = {
      recon: {
        meta: {
          meta: [
            ['sessionid', 1],
            ['event.time', '2019-01-14T06:55:07.000+0000'],
            ['alias.host', 'urdhwa-t1'],
            ['category', 'Service'],
            ['filename', 'spoolsv.exe'],
            ['service.name', 'Spooler']
          ]
        }
      }
    };
    patchReducer(this, Immutable.from(state));
    await render(hbs`{{recon-event-detail/endpoint}}`);
    assert.equal(find(selectors.header).textContent.trim(), 'Service', 'Category is Service');
    const descriptionTags = findAll(selectors.description);
    assert.equal(prepareDesription(descriptionTags), 'spoolsv.exe is running as a service named Spooler', 'text is rendered correctly');
  });

  test('renders Dll view', async function(assert) {
    const state = {
      recon: {
        meta: {
          meta: [
            ['sessionid', 1],
            ['event.time', '2019-01-14T06:55:07.000+0000'],
            ['alias.host', 'urdhwa-t1'],
            ['category', 'Dll'],
            ['filename.dst', 'dtflog.dll'],
            ['filename.src', 'dtf.exe']
          ]
        }
      }
    };
    patchReducer(this, Immutable.from(state));
    await render(hbs`{{recon-event-detail/endpoint}}`);
    assert.equal(find(selectors.header).textContent.trim(), 'Dll', 'Category is Dll');
    const descriptionTags = findAll(selectors.description);
    assert.equal(prepareDesription(descriptionTags), 'dtflog.dll is loaded into dtf.exe', 'text is rendered correctly');
  });

  test('renders Task view', async function(assert) {
    const state = {
      recon: {
        meta: {
          meta: [
            ['sessionid', 1],
            ['event.time', '2019-01-14T06:55:07.000+0000'],
            ['alias.host', 'urdhwa-t1'],
            ['category', 'Task'],
            ['filename', 'cmd.exe'],
            ['task.name', '\\Microsoft\\Windows\\Software Inventory Logging\\Configuration']
          ]
        }
      }
    };
    patchReducer(this, Immutable.from(state));
    await render(hbs`{{recon-event-detail/endpoint}}`);
    assert.equal(find(selectors.header).textContent.trim(), 'Task', 'Category is Task');
    const descriptionTags = findAll(selectors.description);
    assert.equal(prepareDesription(descriptionTags),
      'cmd.exe is running as a task named \\Microsoft\\Windows\\Software Inventory Logging\\Configuration',
      'text is rendered correctly');
  });

  test('renders Process view', async function(assert) {
    const state = {
      recon: {
        meta: {
          meta: [
            ['sessionid', 1],
            ['event.time', '2019-01-14T06:55:07.000+0000'],
            ['alias.host', 'urdhwa-t1'],
            ['category', 'Process'],
            ['filename.dst', 'WmiPrvSE.exe'],
            ['filename.src', 'svchost.exe']
          ]
        }
      }
    };
    patchReducer(this, Immutable.from(state));
    await render(hbs`{{recon-event-detail/endpoint}}`);
    assert.equal(find(selectors.header).textContent.trim(), 'Process', 'Category is Process');
    const descriptionTags = findAll(selectors.description);
    assert.equal(prepareDesription(descriptionTags), 'WmiPrvSE.exe is launched by svchost.exe', 'text is rendered correctly');
  });

  test('renders Console Event view', async function(assert) {
    const state = {
      recon: {
        meta: {
          meta: [
            ['sessionid', 1],
            ['event.time', '2019-01-14T06:55:07.000+0000'],
            ['alias.host', 'urdhwa-t1'],
            ['category', 'Console Event'],
            ['param.src', 'cmd.exe'],
            ['param.src', 'dtf.exe -dll:ioc.dll'],
            ['filename.src', 'cmd.exe']
          ]
        }
      }
    };
    patchReducer(this, Immutable.from(state));
    await render(hbs`{{recon-event-detail/endpoint}}`);
    assert.equal(find(selectors.header).textContent.trim(), 'Console Event', 'Category is Console Event');
    const descriptionTags = findAll(selectors.description);
    assert.equal(prepareDesription(descriptionTags), 'cmd.exe ran dtf.exe -dll:ioc.dll', 'text is rendered correctly');
  });

  test('renders System event view', async function(assert) {
    const state = {
      recon: {
        meta: {
          meta: [
            ['sessionid', 1],
            ['event.time', '2019-01-14T06:55:07.000+0000'],
            ['alias.host', 'urdhwa-t1'],
            ['category', 'System Event'],
            ['event.type', 'ipChange']
          ]
        }
      }
    };
    patchReducer(this, Immutable.from(state));
    await render(hbs`{{recon-event-detail/endpoint}}`);
    assert.equal(find(selectors.header).textContent.trim(), 'System Event', 'Category is System Event');
    const descriptionTags = findAll(selectors.description);
    assert.equal(prepareDesription(descriptionTags), 'triggered event type is ipChange', 'text is rendered correctly');
  });

  test('renders Kernel Hook view', async function(assert) {
    const state = {
      recon: {
        meta: {
          meta: [
            ['sessionid', 1],
            ['event.time', '2019-01-14T06:55:07.000+0000'],
            ['alias.host', 'urdhwa-t1'],
            ['category', 'Kernel Hook'],
            ['filename.src', 'CmgPCS.sys'],
            ['filename.dst', 'ntoskrnl.exe']
          ]
        }
      }
    };
    patchReducer(this, Immutable.from(state));
    await render(hbs`{{recon-event-detail/endpoint}}`);
    assert.equal(find(selectors.header).textContent.trim(), 'Kernel Hook', 'Category is Kernel Hook');
    const descriptionTags = findAll(selectors.description);
    assert.equal(prepareDesription(descriptionTags), 'CmgPCS.sys hooked function in ntoskrnl.exe', 'text is rendered correctly');
  });

  test('renders Image Hook view', async function(assert) {
    const state = {
      recon: {
        meta: {
          meta: [
            ['sessionid', 1],
            ['event.time', '2019-01-14T06:55:07.000+0000'],
            ['alias.host', 'urdhwa-t1'],
            ['category', 'Image Hook'],
            ['filename.src', 'CyMemDef64.dll'],
            ['filename.dst', 'ntdll.dll'],
            ['filename', 'vpnui.exe'],
            ['function', 'fileOpen']
          ]
        }
      }
    };
    patchReducer(this, Immutable.from(state));
    await render(hbs`{{recon-event-detail/endpoint}}`);
    assert.equal(find(selectors.header).textContent.trim(), 'Image Hook', 'Category is Image Hook');
    const descriptionTags = findAll(selectors.description);
    assert.equal(prepareDesription(descriptionTags), 'CyMemDef64.dll hooked ntdll.dll :: fileOpen loaded in vpnui.exe', 'text is rendered correctly');
  });

  test('renders Dylib view', async function(assert) {
    const state = {
      recon: {
        meta: {
          meta: [
            ['sessionid', 1],
            ['event.time', '2019-01-14T06:55:07.000+0000'],
            ['alias.host', 'urdhwa-t1'],
            ['category', 'Dylib'],
            ['filename.dst', 'libma_sensor_service.5.0.dylib'],
            ['filename.src', 'macmnsvc']
          ]
        }
      }
    };
    patchReducer(this, Immutable.from(state));
    await render(hbs`{{recon-event-detail/endpoint}}`);
    assert.equal(find(selectors.header).textContent.trim(), 'Dylib', 'Category is Dylib');
    const descriptionTags = findAll(selectors.description);
    assert.equal(prepareDesription(descriptionTags), 'libma_sensor_service.5.0.dylib is loaded into macmnsvc', 'text is rendered correctly');
  });

  test('renders Loaded library view', async function(assert) {
    const state = {
      recon: {
        meta: {
          meta: [
            ['sessionid', 1],
            ['event.time', '2019-01-14T06:55:07.000+0000'],
            ['alias.host', 'urdhwa-t1'],
            ['category', 'Loaded Library'],
            ['filename.dst', '_heapq.so'],
            ['filename.src', 'python2.7']
          ]
        }
      }
    };
    patchReducer(this, Immutable.from(state));
    await render(hbs`{{recon-event-detail/endpoint}}`);
    assert.equal(find(selectors.header).textContent.trim(), 'Loaded Library', 'Category is Loaded library');
    const descriptionTags = findAll(selectors.description);
    assert.equal(prepareDesription(descriptionTags), '_heapq.so is loaded into python2.7', 'text is rendered correctly');
  });

  test('renders Daemon view', async function(assert) {
    const state = {
      recon: {
        meta: {
          meta: [
            ['sessionid', 1],
            ['event.time', '2019-01-14T06:55:07.000+0000'],
            ['alias.host', 'urdhwa-t1'],
            ['category', 'Daemon'],
            ['filename', 'storedownloadd']
          ]
        }
      }
    };
    patchReducer(this, Immutable.from(state));
    await render(hbs`{{recon-event-detail/endpoint}}`);
    assert.equal(find(selectors.header).textContent.trim(), 'Daemon', 'Category is Daemon');
    const descriptionTags = findAll(selectors.description);
    assert.equal(prepareDesription(descriptionTags), 'storedownloadd is configured as Daemon', 'text is rendered correctly');
  });

  test('renders Initd view', async function(assert) {
    const state = {
      recon: {
        meta: {
          meta: [
            ['sessionid', 1],
            ['event.time', '2019-01-14T06:55:07.000+0000'],
            ['alias.host', 'urdhwa-t1'],
            ['category', 'Initd'],
            ['filename', 'netconsole']
          ]
        }
      }
    };
    patchReducer(this, Immutable.from(state));
    await render(hbs`{{recon-event-detail/endpoint}}`);
    assert.equal(find(selectors.header).textContent.trim(), 'Initd', 'Category is Initd');
    const descriptionTags = findAll(selectors.description);
    assert.equal(prepareDesription(descriptionTags), 'netconsole is configured as Initd service', 'text is rendered correctly');
  });

  test('renders Systemd view', async function(assert) {
    const state = {
      recon: {
        meta: {
          meta: [
            ['sessionid', 1],
            ['event.time', '2019-01-14T06:55:07.000+0000'],
            ['alias.host', 'urdhwa-t1'],
            ['category', 'Systemd'],
            ['filename', 'auth-rpcgss-module.service']
          ]
        }
      }
    };
    patchReducer(this, Immutable.from(state));
    await render(hbs`{{recon-event-detail/endpoint}}`);
    assert.equal(find(selectors.header).textContent.trim(), 'Systemd', 'Category is Systemd');
    const descriptionTags = findAll(selectors.description);
    assert.equal(prepareDesription(descriptionTags), 'auth-rpcgss-module.service is configured as Systemd service', 'text is rendered correctly');
  });

  test('renders Cron view', async function(assert) {
    const state = {
      recon: {
        meta: {
          meta: [
            ['sessionid', 1],
            ['event.time', '2019-01-14T06:55:07.000+0000'],
            ['alias.host', 'urdhwa-t1'],
            ['category', 'Cron'],
            ['filename', 'mlocate']
          ]
        }
      }
    };
    patchReducer(this, Immutable.from(state));
    await render(hbs`{{recon-event-detail/endpoint}}`);
    assert.equal(find(selectors.header).textContent.trim(), 'Cron', 'Category is Cron');
    const descriptionTags = findAll(selectors.description);
    assert.equal(prepareDesription(descriptionTags), 'mlocate is configured as cron job', 'text is rendered correctly');
  });

  test('renders Machine view', async function(assert) {
    const state = {
      recon: {
        meta: {
          meta: [
            ['sessionid', 1],
            ['event.time', '2019-01-14T06:55:07.000+0000'],
            ['alias.host', 'urdhwa-t1'],
            ['category', 'Machine'],
            ['filename', 'mlocate']
          ]
        }
      }
    };
    patchReducer(this, Immutable.from(state));
    await render(hbs`{{recon-event-detail/endpoint}}`);
    assert.equal(find(selectors.header).textContent.trim(), 'Machine', 'Category is Machine');
    const descriptionTags = findAll(selectors.description);
    assert.equal(prepareDesription(descriptionTags), 'Scan Snapshot available in Host View', 'text is rendered correctly');
  });
});
