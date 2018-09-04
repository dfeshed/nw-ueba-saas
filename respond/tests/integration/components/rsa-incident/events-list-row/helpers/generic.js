import { findAll, find } from '@ember/test-helpers';
import { selectors, generic } from './selectors';

export const assertRowPresent = (assert) => {
  assert.equal(findAll(selectors.row).length, 1);
  assert.equal(findAll(selectors.genericRow).length, 1);
  assert.equal(findAll(selectors.genericMain).length, 1);
  assert.equal(findAll(selectors.genericDetail).length, 0);
};

export const assertRowHeader = (assert, { eventType, detectorIp, fileName, fileHash }) => {
  assert.equal(find(generic.eventTimeLabel).textContent.trim(), 'EVENT TIME');
  assert.ok(find(generic.eventTimeValue).textContent.trim() !== '');
  assert.equal(find(generic.eventTypeLabel).textContent.trim(), 'EVENT TYPE');
  assert.equal(find(generic.eventTypeValue).textContent.trim(), eventType);
  assert.equal(find(generic.eventDetectorIpLabel).textContent.trim(), 'DETECTOR IP');
  assert.equal(find(generic.eventDetectorIpValue).textContent.trim(), detectorIp);
  assert.equal(find(generic.eventFileNameLabel).textContent.trim(), 'FILE NAME');
  assert.equal(find(generic.eventFileNameValue).textContent.trim(), fileName);
  assert.equal(find(generic.eventFileHashLabel).textContent.trim(), 'FILE HASH');
  assert.equal(find(generic.eventFileHashValue).textContent.trim(), fileHash);
};

export const assertTableColumns = (assert) => {
  assert.equal(find(generic.eventTableIpLabel).textContent.trim(), 'IP');
  assert.equal(find(generic.eventTablePortLabel).textContent.trim(), 'PORT');
  assert.equal(find(generic.eventTableHostLabel).textContent.trim(), 'HOST');
  assert.equal(find(generic.eventTableMacLabel).textContent.trim(), 'MAC');
  assert.equal(find(generic.eventTableUserLabel).textContent.trim(), 'USER');
};

export const assertTableSource = (assert, { ip, port, host, mac, user }) => {
  assert.equal(find(generic.eventSourceLabel).textContent.trim(), 'Source');
  assert.equal(find(generic.eventSourceIpValue).textContent.trim(), ip);
  assert.equal(find(generic.eventSourcePortValue).textContent.trim(), port);
  assert.equal(find(generic.eventSourceHostValue).textContent.trim(), host);
  assert.equal(find(generic.eventSourceMacValue).textContent.trim(), mac);
  assert.equal(find(generic.eventSourceUserValue).textContent.trim(), user);
};

export const assertTableTarget = (assert, { ip, port, host, mac, user }) => {
  assert.equal(find(generic.eventTargetLabel).textContent.trim(), 'Target');
  assert.equal(find(generic.eventTargetIpValue).textContent.trim(), ip);
  assert.equal(find(generic.eventTargetPortValue).textContent.trim(), port);
  assert.equal(find(generic.eventTargetHostValue).textContent.trim(), host);
  assert.equal(find(generic.eventTargetMacValue).textContent.trim(), mac);
  assert.equal(find(generic.eventTargetUserValue).textContent.trim(), user);
};
