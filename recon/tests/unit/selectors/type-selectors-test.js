import { RECON_VIEW_TYPES_BY_NAME } from 'recon/utils/reconstruction-types';
import { isTextView, isFileView, isPacketView, lacksPackets } from 'recon/selectors/type-selectors';
import { module, test } from 'qunit';

module('Unit | Mixin | event-type-selector');

const generateTests = function(selector) {
  return {
    textView: selector({
      data: {
        currentReconView: {
          code: RECON_VIEW_TYPES_BY_NAME.TEXT.code
        }
      }
    }),
    fileView: selector({
      data: {
        currentReconView: {
          code: RECON_VIEW_TYPES_BY_NAME.FILE.code
        }
      }
    }),
    packetView: selector({
      data: {
        currentReconView: {
          code: RECON_VIEW_TYPES_BY_NAME.PACKET.code
        }
      }
    })
  };
};

test('isTextView', function(assert) {
  assert.expect(3);

  const tests = generateTests(isTextView);

  assert.equal(tests.textView, true, 'isTextView should return true when is text view');
  assert.equal(tests.fileView, false, 'isTextView should return false when is file view');
  assert.equal(tests.packetView, false, 'isTextView should return false when is packet view');
});

test('isFileView', function(assert) {
  assert.expect(3);

  const tests = generateTests(isFileView);

  assert.equal(tests.textView, false, 'isFileView should return false when is text view');
  assert.equal(tests.fileView, true, 'isFileView should return true when is file view');
  assert.equal(tests.packetView, false, 'isFileView should return false when is packet view');
});

test('isPacketView', function(assert) {
  assert.expect(3);

  const tests = generateTests(isPacketView);

  assert.equal(tests.textView, false, 'isPacketView should return false when is text view');
  assert.equal(tests.fileView, false, 'isPacketView should return false when is file view');
  assert.equal(tests.packetView, true, 'isPacketView should return true when is packet view');
});

test('lacksPackets', function(assert) {
  assert.expect(3);

  const tests = generateTests(lacksPackets);

  assert.equal(tests.textView, false, 'lacksPackets should return false when is text view');
  assert.equal(tests.fileView, true, 'lacksPackets should return true when is file view');
  assert.equal(tests.packetView, false, 'lacksPackets should return false when is packet view');
});