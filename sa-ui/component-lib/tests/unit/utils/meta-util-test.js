import { module, test } from 'qunit';
import { getSrcFilename } from 'component-lib/utils/meta-util';

module('Unit | Util | meta-util');

test('when multiple filename.src is present, non script file should be returned', async function(assert) {
  const meta = [
    ['OS', 'windows'],
    ['checksum.src', 'test-checksum'],
    ['agent.id', 'abcd'],
    ['filename.src', '[FILELESS_SCRIPT]'],
    ['process.vid.src', 1],
    ['alias.host', 'TestHostName'],
    ['filename.src', 'cmd.exe']
  ];
  const filename = getSrcFilename(meta);
  assert.equal(filename, '[FILELESS_SCRIPT]', 'return non script filename');
});

test('single filename.src is present which is a script file', async function(assert) {
  const meta = [
    ['OS', 'windows'],
    ['checksum.src', 'test-checksum'],
    ['agent.id', 'abcd'],
    ['process.vid.src', 1],
    ['alias.host', 'TestHostName'],
    ['filename.src', 'cmd.exe']
  ];
  const filename = getSrcFilename(meta);
  assert.equal(filename, 'cmd.exe', 'return cmd.exe when single filename.src is present');
});

test('single filename.src is present which is not a script file', async function(assert) {
  const meta = [
    ['OS', 'windows'],
    ['checksum.src', 'test-checksum'],
    ['agent.id', 'abcd'],
    ['process.vid.src', 1],
    ['alias.host', 'TestHostName'],
    ['filename.src', 'hello.exe']
  ];
  const filename = getSrcFilename(meta);
  assert.equal(filename, 'hello.exe', 'return hello.exe : only filename.src');
});