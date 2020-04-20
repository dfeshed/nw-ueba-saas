import { findAll, find } from '@ember/test-helpers';
import { selectors, generic } from './selectors';

const ENTITY_CLASS = 'entity';
const DATA_ENTITY_ID = 'data-entity-id';
const DATA_META_KEY = 'data-meta-key';

export const assertRowPresent = (assert) => {
  assert.equal(findAll(selectors.row).length, 1);
  assert.equal(findAll(selectors.genericHeader).length, 1);
  assert.equal(findAll(selectors.genericDetail).length, 0);
  assert.equal(findAll(selectors.caption).length, 1);
  assert.equal(find(selectors.caption).textContent, 'Event Source and Target');
};

export const assertRowAlertDetails = (assert, { name, summary, score }) => {
  assert.equal(find(selectors.alertName).textContent.trim(), name);
  assert.equal(find(selectors.alertScore).textContent.trim(), score);
  assert.equal(find(selectors.eventSummary).textContent.trim(), summary);
};

export const assertRowHeaderContext = (assert, { detectorIp, fileName, fileHash }) => {
  if (detectorIp) {
    assert.equal(find(generic.eventDetectorIpValue).attributes[DATA_ENTITY_ID].nodeValue, detectorIp);
    assert.equal(find(generic.eventDetectorIpValue).attributes[DATA_META_KEY].nodeValue, 'ip_address');
    assert.equal(find(generic.eventDetectorIpValue).classList.contains(ENTITY_CLASS), true);
  } else {
    assert.equal(find(generic.eventDetectorIpValue).classList.contains(ENTITY_CLASS), false);
  }
  if (fileName) {
    assert.equal(find(generic.eventFileNameValue).attributes[DATA_ENTITY_ID].nodeValue, fileName);
    assert.equal(find(generic.eventFileNameValue).attributes[DATA_META_KEY].nodeValue, 'filename');
    assert.equal(find(generic.eventFileNameValue).classList.contains(ENTITY_CLASS), true);
  } else {
    assert.equal(find(generic.eventFileNameValue).classList.contains(ENTITY_CLASS), false);
  }
  if (fileHash) {
    assert.equal(find(generic.eventFileHashValue).attributes[DATA_ENTITY_ID].nodeValue, fileHash);
    assert.equal(find(generic.eventFileHashValue).attributes[DATA_META_KEY].nodeValue, 'hash');
    assert.equal(find(generic.eventFileHashValue).classList.contains(ENTITY_CLASS), true);
  } else {
    assert.equal(find(generic.eventFileHashValue).classList.contains(ENTITY_CLASS), false);
  }
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

export const assertTableSourceContext = (assert, { ip, mac, user }) => {
  if (ip) {
    assert.equal(find(generic.eventSourceIpValue).attributes[DATA_ENTITY_ID].nodeValue, ip);
    assert.equal(find(generic.eventSourceIpValue).attributes[DATA_META_KEY].nodeValue, 'ip_address');
    assert.equal(find(generic.eventSourceIpValue).classList.contains(ENTITY_CLASS), true);
  } else {
    assert.equal(find(generic.eventSourceIpValue).classList.contains(ENTITY_CLASS), false);
  }
  if (mac) {
    assert.equal(find(generic.eventSourceMacValue).attributes[DATA_ENTITY_ID].nodeValue, mac);
    assert.equal(find(generic.eventSourceMacValue).attributes[DATA_META_KEY].nodeValue, 'mac_address');
    assert.equal(find(generic.eventSourceMacValue).classList.contains(ENTITY_CLASS), true);
  } else {
    assert.equal(find(generic.eventSourceMacValue).classList.contains(ENTITY_CLASS), false);
  }
  if (user) {
    assert.equal(find(generic.eventSourceUserValue).attributes[DATA_ENTITY_ID].nodeValue, user);
    assert.equal(find(generic.eventSourceUserValue).attributes[DATA_META_KEY].nodeValue, 'username');
    assert.equal(find(generic.eventSourceUserValue).classList.contains(ENTITY_CLASS), true);
  } else {
    assert.equal(find(generic.eventSourceUserValue).classList.contains(ENTITY_CLASS), false);
  }
};

export const assertTableSource = (assert, { ip, port, host, mac, user }) => {
  assert.equal(find(generic.eventSourceLabel).textContent.trim(), 'Source');
  assert.equal(find(generic.eventSourceIpValue).textContent.trim(), ip);
  assert.equal(find(generic.eventSourcePortValue).textContent.trim(), port);
  assert.equal(find(generic.eventSourceHostValue).textContent.trim(), host);
  assert.equal(find(generic.eventSourceMacValue).textContent.trim(), mac);
  assert.equal(find(generic.eventSourceUserValue).textContent.trim(), user);
};

export const assertTableTargetContext = (assert, { ip, mac, user }) => {
  if (ip) {
    assert.equal(find(generic.eventTargetIpValue).attributes[DATA_ENTITY_ID].nodeValue, ip);
    assert.equal(find(generic.eventTargetIpValue).attributes[DATA_META_KEY].nodeValue, 'ip_address');
    assert.equal(find(generic.eventTargetIpValue).classList.contains(ENTITY_CLASS), true);
  } else {
    assert.equal(find(generic.eventTargetIpValue).classList.contains(ENTITY_CLASS), false);
  }
  if (mac) {
    assert.equal(find(generic.eventTargetMacValue).attributes[DATA_ENTITY_ID].nodeValue, mac);
    assert.equal(find(generic.eventTargetMacValue).attributes[DATA_META_KEY].nodeValue, 'mac_address');
    assert.equal(find(generic.eventTargetMacValue).classList.contains(ENTITY_CLASS), true);
  } else {
    assert.equal(find(generic.eventTargetMacValue).classList.contains(ENTITY_CLASS), false);
  }
  if (user) {
    assert.equal(find(generic.eventTargetUserValue).attributes[DATA_ENTITY_ID].nodeValue, user);
    assert.equal(find(generic.eventTargetUserValue).attributes[DATA_META_KEY].nodeValue, 'username');
    assert.equal(find(generic.eventTargetUserValue).classList.contains(ENTITY_CLASS), true);
  } else {
    assert.equal(find(generic.eventTargetUserValue).classList.contains(ENTITY_CLASS), false);
  }
};

export const assertTableTarget = (assert, { ip, port, host, mac, user }) => {
  assert.equal(find(generic.eventTargetLabel).textContent.trim(), 'Target');
  assert.equal(find(generic.eventTargetIpValue).textContent.trim(), ip);
  assert.equal(find(generic.eventTargetPortValue).textContent.trim(), port);
  assert.equal(find(generic.eventTargetHostValue).textContent.trim(), host);
  assert.equal(find(generic.eventTargetMacValue).textContent.trim(), mac);
  assert.equal(find(generic.eventTargetUserValue).textContent.trim(), user);
};
