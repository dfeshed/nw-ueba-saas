import { module, test } from 'qunit';
import { transform } from 'investigate-shared/utils/meta-util';

module('Unit | Utils | Meta Utils');
test('transform returns fields from map or camelizes', function(assert) {
  const event = {
    metas: [['action', 'createProcess'],
      ['OS', 'windows'],
      ['device.type', 'nwendpoint'],
      ['alias.host', 'INENSANDRA'],
      ['user.src', 'sandra'],
      ['filename.src', 'REG.exe'],
      ['filename.dst', 'dtf.exe'],
      ['directory.src', 'C:\\Windows'],
      ['directory.dst', 'C:\\'],
      ['param.src', 'REG.exe ADD HKLM\\System\\CurrentControlSet\\Control\\SecurityProviders\\WDigest /V USELOGONCREDENTIAL /T REG_DWORD /D 1 /f'],
      ['param.dst', 'dtf.exe -testcase:355,364,401,402,404,406,407,341 -dll:ioc.dll -log:ioc.log'],
      ['checksum.src', 'e3dacf0b31841fa02064b4457d44b357'],
      ['checksum.dst', '071a4c73e30f066962850d6b5d00553a'],
      ['sessionid', '100'],
      ['file.attributes', 'xyz']
    ] };
  const result = transform(event);
  const keys = Object.keys(result);
  assert.equal(keys[0], 'action', 'action => action');
  assert.equal(keys[1], 'operating_system', 'OS => operating_system');
  assert.equal(keys[2], 'device_type', 'device.type => device_type');
  assert.equal(keys[3], 'hostname', 'alias.host => hostname');
  assert.equal(keys[4], 'user_account', 'user.src => user_account');
  assert.equal(keys[5], 'source_filename', 'filename.src => source_filename');
  assert.equal(keys[6], 'target_filename', 'filename.dst => target_filename');
  assert.equal(keys[7], 'source_path', 'directory.src => source_path');
  assert.equal(keys[8], 'target_path', 'directory.dst => target_path');
  assert.equal(keys[9], 'launch_argument_src', 'param.src => launch_argument_src');
  assert.equal(keys[10], 'launch_argument_dst', 'param.dst => launch_argument_dst');
  assert.equal(keys[11], 'source_hash', 'checksum.src => source_hash');
  assert.equal(keys[12], 'target_hash', 'checksum.dst => target_hash');
  assert.equal(keys[13], 'id', 'sessionid => id');
  assert.equal(keys[14], 'fileAttributes', 'file.attributes => fileAttirbutes');
});