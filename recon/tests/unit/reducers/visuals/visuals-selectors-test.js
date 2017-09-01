import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';

import { RECON_VIEW_TYPES_BY_NAME } from 'recon/utils/reconstruction-types';
import {
  isTextView,
  isFileView,
  isPacketView,
  lacksPackets,
  hasReconView,
  allDataHidden
} from 'recon/reducers/visuals/selectors';

module('Unit | selector | visuals');

const generateReconViewData = function(selector) {
  return {
    textView: selector(Immutable.from({
      visuals: {
        currentReconView: {
          code: RECON_VIEW_TYPES_BY_NAME.TEXT.code
        }
      }
    })),
    fileView: selector(Immutable.from({
      visuals: {
        currentReconView: {
          code: RECON_VIEW_TYPES_BY_NAME.FILE.code
        }
      }
    })),
    packetView: selector(Immutable.from({
      visuals: {
        currentReconView: {
          code: RECON_VIEW_TYPES_BY_NAME.PACKET.code
        }
      }
    })),
    noView: selector(Immutable.from({
      visuals: {
        currentReconView: null
      }
    }))
  };
};

test('hasReconView', function(assert) {
  assert.expect(4);

  const tests = generateReconViewData(hasReconView);

  assert.equal(tests.textView, true, 'hasReconView should return true when is text view');
  assert.equal(tests.fileView, true, 'hasReconView should return true when is file view');
  assert.equal(tests.packetView, true, 'hasReconView should return true when is packet view');
  assert.equal(tests.noView, false, 'hasReconView should return false when there is no view selected');
});

test('isTextView', function(assert) {
  assert.expect(4);

  const tests = generateReconViewData(isTextView);

  assert.equal(tests.textView, true, 'isTextView should return true when is text view');
  assert.equal(tests.fileView, false, 'isTextView should return false when is file view');
  assert.equal(tests.packetView, false, 'isTextView should return false when is packet view');
  assert.equal(tests.noView, false, 'isTextView should return false when there is no view selected');
});

test('isFileView', function(assert) {
  assert.expect(4);

  const tests = generateReconViewData(isFileView);

  assert.equal(tests.textView, false, 'isFileView should return false when is text view');
  assert.equal(tests.fileView, true, 'isFileView should return true when is file view');
  assert.equal(tests.packetView, false, 'isFileView should return false when is packet view');
  assert.equal(tests.noView, false, 'isFileView should return false when there is no view selected');

});

test('isPacketView', function(assert) {
  assert.expect(4);

  const tests = generateReconViewData(isPacketView);

  assert.equal(tests.textView, false, 'isPacketView should return false when is text view');
  assert.equal(tests.fileView, false, 'isPacketView should return false when is file view');
  assert.equal(tests.packetView, true, 'isPacketView should return true when is packet view');
  assert.equal(tests.noView, false, 'isPacketView should return false when there is no view selected');
});

test('lacksPackets', function(assert) {
  assert.expect(4);

  const tests = generateReconViewData(lacksPackets);

  assert.equal(tests.textView, false, 'lacksPackets should return false when is text view');
  assert.equal(tests.fileView, true, 'lacksPackets should return true when is file view');
  assert.equal(tests.packetView, false, 'lacksPackets should return false when is packet view');
  assert.equal(tests.packetView, false, 'lacksPackets should return false when is no view');
});


test('allDataHidden', function(assert) {
  assert.expect(5);

  const tests = {
    bothShown: allDataHidden(Immutable.from({
      visuals: {
        isRequestShown: true,
        isResponseShown: true
      }
    })),
    requestShown: allDataHidden(Immutable.from({
      visuals: {
        isRequestShown: true,
        isResponseShown: false
      }
    })),
    responseShown: allDataHidden(Immutable.from({
      visuals: {
        isRequestShown: false,
        isResponseShown: true
      }
    })),
    bothHidden: allDataHidden(Immutable.from({
      visuals: {
        isRequestShown: false,
        isResponseShown: false
      }
    })),
    missing: allDataHidden(Immutable.from({
      visuals: {}
    }))
  };

  assert.equal(tests.bothShown, false, 'allDataHidden should return false, when both shown');
  assert.equal(tests.requestShown, false, 'allDataHidden should return false, when request shown');
  assert.equal(tests.responseShown, false, 'allDataHidden should return false, when response shown');
  assert.equal(tests.bothHidden, true, 'allDataHidden should return true, when both hidden');
  assert.equal(tests.missing, false, 'allDataHidden should return false, when data absent');
});