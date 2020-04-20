import { module, test } from 'qunit';

import { doesStateHaveViewData } from 'recon/utils/reconstruction-types';
module('Unit | Utility | Reconstruction types');

const packetKey = {
  code: 1,
  id: 'packet',
  name: 'PACKET',
  component: 'recon-event-detail/packets',
  dataKey: 'packets.packets'
};
const textKey = {
  id: 'text',
  name: 'TEXT',
  component: 'recon-event-detail/text-content',
  dataKey: 'text.textContent'
};
const fileKey = {
  code: 2,
  id: 'file',
  name: 'FILE',
  component: 'recon-event-detail/files',
  dataKey: 'files.files'
};

test('doesStateHaveViewData returns false when content is null ', function(assert) {
  const reconState = {
    text: {
      textContent: null
    },
    packets: {
      packets: null
    },
    files: {
      files: null
    }
  };
  assert.notOk(doesStateHaveViewData(reconState, textKey));
  assert.notOk(doesStateHaveViewData(reconState, packetKey));
  assert.notOk(doesStateHaveViewData(reconState, fileKey));
});

test('doesStateHaveViewData returns false when content is empty array ', function(assert) {
  const reconState = {
    text: {
      textContent: []
    },
    packets: {
      packets: []
    },
    files: {
      files: []
    }
  };
  assert.notOk(doesStateHaveViewData(reconState, textKey));
  assert.notOk(doesStateHaveViewData(reconState, packetKey));
  assert.notOk(doesStateHaveViewData(reconState, fileKey));
});

test('doesStateHaveViewData returns true when there is some content ', function(assert) {
  const reconState = {
    text: {
      textContent: ['a']
    },
    packets: {
      packets: ['a']
    },
    files: {
      files: ['a']
    }
  };
  assert.ok(doesStateHaveViewData(reconState, textKey));
  assert.ok(doesStateHaveViewData(reconState, packetKey));
  assert.ok(doesStateHaveViewData(reconState, fileKey));
});