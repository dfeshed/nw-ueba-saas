import { module, test } from 'qunit';
import { transform } from 'investigate-shared/utils/meta-util';

module('Unit | Utils | Meta Utils');
test('transform returns fields from map or specific object', function(assert) {
  const event = {
    metas: [['sessionid', 116414],
      ['time', '2018-12-07T05:19:22.000+0000'],
      ['size', 41],
      ['forward.ip', '10.40.14.108'],
      ['ip.all', '10.40.14.108'],
      ['device.type', 'nwendpoint'],
      ['checksum.src', '822e401c0d0612810c4398838fd5cf2bdec21cd35f2f24295a331b61e92bc5ef'],
      ['checksum.all', '822e401c0d0612810c4398838fd5cf2bdec21cd35f2f24295a331b61e92bc5ef'],
      ['filename.dst', 'POWERSHELL.EXE'],
      ['filename.all', 'POWERSHELL.EXE'],
      ['process.vid.src', '8760110666849825628'],
      ['filename.src', 'dtf.exe'],
      ['filename.all', 'dtf.exe'],
      ['param.dst', "POWERSHELL.EXE add-content -path 'C:\\Users\\admin\\D…ient.DownloadString('http://myip.dnsomatic.com/')"],
      ['action', 'createProcess'],
      ['param.src', 'dtf.exe  -dll:ioc.dll -testcase:334'],
      ['user.src', 'SUPERNOVAWIN4\\admin'],
      ['user.all', 'SUPERNOVAWIN4\\admin'],
      ['directory.dst', 'C:\\Windows\\system32\\WindowsPowerShell\\v1.0\\'],
      ['directory.all', 'C:\\Windows\\system32\\WindowsPowerShell\\v1.0\\'],
      ['checksum.dst', '840e1f9dc5a29bebf01626822d7390251e9cf05bb3560ba7b68bdb8a41cf08e3'],
      ['checksum.all', '840e1f9dc5a29bebf01626822d7390251e9cf05bb3560ba7b68bdb8a41cf08e3'],
      ['process.vid.dst', '-3646183813079877108'],
      ['directory.src', 'C:\\Users\\admin\\Downloads\\archive\\archive\\amd64\\'],
      ['directory.all', 'C:\\Users\\admin\\Downloads\\archive\\archive\\amd64\\'],
      ['event.time', '2018-12-07T05:15:15.000+0000'],
      ['checksum.src', '09a1afb374069223e1ec1d2609a42e87'],
      ['checksum.all', '09a1afb374069223e1ec1d2609a42e87'],
      ['checksum.dst', 'c031e215b8b08c752bf362f6d4c5d3ad'],
      ['checksum.all', 'c031e215b8b08c752bf362f6d4c5d3ad'],
      ['OS', 'windows'],
      ['alias.ip', '10.40.14.97'],
      ['ip.all', '10.40.14.97'],
      ['agent.id', '4A608C20-F3F0-8166-4149-1DEF156C13F2'],
      ['alias.host', 'SUPERNOVAWIN4'],
      ['host.all', 'SUPERNOVAWIN4'],
      ['category', 'Process Event'],
      ['nwe.callback_id', 'nwe://12f0cd3e-40c8-42f2-90fa-85777897b2e9'],
      ['msg.id', 'nwendpoint'],
      ['device.disc', 30],
      ['device.disc.type', 'nwendpoint']
    ] };
  const result = transform(event);
  const keys = Object.keys(result);
  assert.equal(keys[0], 'event_source_id', 'sessionid => event_source_id');
  assert.equal(keys[1], 'timestamp', 'time => timestamp');
  assert.equal(keys[2], 'size', 'size => size');
  assert.equal(keys[3], 'IP', 'ip.all => IP');
  assert.equal(keys[4], 'device_type', 'device.type => device_type');
  assert.equal(keys[5], 'file_SHA256', 'checksum.src => file_SHA256');
  assert.equal(keys[6], 'action', 'action => action');
  assert.equal(keys[7], 'user', 'user.all => user');
  assert.equal(keys[8], 'operating_system', 'OS => operating_system');
  assert.equal(keys[9], 'agent_id', 'agent.id => agent_id');
  assert.equal(keys[10], 'hostname', 'alias.host => hostname');
  assert.equal(keys[11], 'category', 'category => category');
  assert.equal(keys[12], 'type', 'type => type');
  assert.equal(keys[14], 'event_source', 'event_source_id is present');
  assert.equal(keys[15], 'source', 'source info is present');
  assert.equal(keys[16], 'destination', 'destination info is present');
  assert.equal(keys[17], 'detector', 'detector info is present');
  assert.equal(keys[18], 'related_links', 'related_links info is present');

  assert.equal(result.related_links.length, 2, '2 related links present');
  assert.equal(result.related_links[0].url, '/investigation/host/10.40.14.108:50005/navigate/event/AUTO/116414', 'Original event link is correct');
  assert.equal(result.related_links[1].url,
    '/investigation/10.40.14.108:50005/navigate/query/alias.host%3D\'SUPERNOVAWIN4\'%2Fdate%2F2018-12-07T05%3A10%3A00.999Z%2F2018-12-07T05%3A20%3A00.999Z',
    'Destination domain link is correct');
  assert.equal(result.source.hash, '822e401c0d0612810c4398838fd5cf2bdec21cd35f2f24295a331b61e92bc5ef', 'Source Sha256 is set');
  assert.equal(result.destination.hash, '840e1f9dc5a29bebf01626822d7390251e9cf05bb3560ba7b68bdb8a41cf08e3', 'Destination hash is Sha256');
});


test('md5 is set for checksum when sha256 is not there', function(assert) {
  const event = {
    metas: [['sessionid', 116414],
      ['time', '2018-12-07T05:19:22.000+0000'],
      ['checksum.dst', '822e401c0d0612810c4398838fd5cf2bdec21cd35f2f24295a331b61e92bc5ef'],
      ['checksum.src', '09a1afb374069223e1ec1d2609a42e87'],
      ['checksum.all', '09a1afb374069223e1ec1d2609a42e87'],
      ['checksum.dst', 'c031e215b8b08c752bf362f6d4c5d3ad'],
      ['checksum.all', 'c031e215b8b08c752bf362f6d4c5d3ad']
    ] };
  const result = transform(event);
  assert.equal(result.source.hash, '09a1afb374069223e1ec1d2609a42e87', 'Source hash (md5) is set');
  assert.equal(result.destination.hash, '822e401c0d0612810c4398838fd5cf2bdec21cd35f2f24295a331b61e92bc5ef', 'Destination hash is Sha256');
});