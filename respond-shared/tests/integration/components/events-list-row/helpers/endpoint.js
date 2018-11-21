import { findAll, find } from '@ember/test-helpers';
import { selectors, endpoint } from './selectors';

const ENTITY_CLASS = 'entity';
const DATA_ENTITY_ID = 'data-entity-id';
const DATA_META_KEY = 'data-meta-key';

export const assertRowPresent = (assert) => {
  assert.equal(findAll(selectors.row).length, 1);
  assert.equal(findAll(selectors.endpointHeader).length, 1);
  assert.equal(findAll(selectors.genericDetail).length, 0);
  assert.equal(findAll(selectors.caption).length, 1);
  assert.equal(find(selectors.caption).textContent, 'Event Source and Target');
};

export const assertRowAlertDetails = (assert, { name, summary, score }) => {
  assert.equal(find(selectors.alertName).textContent.trim(), name);
  assert.equal(find(selectors.alertScore).textContent.trim(), score);
  assert.equal(find(selectors.eventSummary).textContent.trim(), summary);
};

export const assertRowHeaderContext = (assert, { hostname, userAccount, fileHash }) => {
  if (hostname) {
    assert.equal(find(endpoint.eventHostnameValue).attributes[DATA_ENTITY_ID].nodeValue, hostname);
    assert.equal(find(endpoint.eventHostnameValue).attributes[DATA_META_KEY].nodeValue, 'dns_hostname');
    assert.equal(find(endpoint.eventHostnameValue).classList.contains(ENTITY_CLASS), true);
  } else {
    assert.equal(find(endpoint.eventHostnameValue).classList.contains(ENTITY_CLASS), false);
  }
  if (userAccount) {
    assert.equal(find(endpoint.eventUserAccountValue).attributes[DATA_ENTITY_ID].nodeValue, userAccount);
    assert.equal(find(endpoint.eventUserAccountValue).attributes[DATA_META_KEY].nodeValue, 'username');
    assert.equal(find(endpoint.eventUserAccountValue).classList.contains(ENTITY_CLASS), true);
  } else {
    assert.equal(find(endpoint.eventUserAccountValue).classList.contains(ENTITY_CLASS), false);
  }
  if (fileHash) {
    assert.equal(find(endpoint.eventFileHashValue).attributes[DATA_ENTITY_ID].nodeValue, fileHash);
    assert.equal(find(endpoint.eventFileHashValue).attributes[DATA_META_KEY].nodeValue, 'hash');
    assert.equal(find(endpoint.eventFileHashValue).classList.contains(ENTITY_CLASS), true);
  } else {
    assert.equal(find(endpoint.eventFileHashValue).classList.contains(ENTITY_CLASS), false);
  }
};

export const assertRowHeader = (assert, { eventType, category, action, hostname, userAccount, operatingSystem, fileHash }) => {
  assert.equal(find(endpoint.eventTimeLabel).textContent.trim(), 'EVENT TIME');
  assert.ok(find(endpoint.eventTimeValue).textContent.trim() !== '');
  assert.equal(find(endpoint.eventTypeLabel).textContent.trim(), 'EVENT TYPE');
  assert.equal(find(endpoint.eventTypeValue).textContent.trim(), eventType);
  assert.equal(find(endpoint.eventCategoryLabel).textContent.trim(), 'CATEGORY');
  assert.equal(find(endpoint.eventCategoryValue).textContent.trim(), category);
  assert.equal(find(endpoint.eventActionLabel).textContent.trim(), 'ACTION');
  assert.equal(find(endpoint.eventActionValue).textContent.trim(), action);
  assert.equal(find(endpoint.eventHostnameLabel).textContent.trim(), 'HOSTNAME');
  assert.equal(find(endpoint.eventHostnameValue).textContent.trim(), hostname);
  assert.equal(find(endpoint.eventUserAccountLabel).textContent.trim(), 'USER ACCOUNT');
  assert.equal(find(endpoint.eventUserAccountValue).textContent.trim(), userAccount);
  assert.equal(find(endpoint.eventOperatingSystemLabel).textContent.trim(), 'OPERATING SYSTEM');
  assert.equal(find(endpoint.eventOperatingSystemValue).textContent.trim(), operatingSystem);
  assert.equal(find(endpoint.eventFileHashLabel).textContent.trim(), 'FILE HASH');
  assert.equal(find(endpoint.eventFileHashValue).textContent.trim(), fileHash);
};

export const assertTableColumns = (assert) => {
  assert.equal(find(endpoint.eventTableFileNameLabel).textContent.trim(), 'FILE NAME');
  assert.equal(find(endpoint.eventTableLaunchLabel).textContent.trim(), 'LAUNCH ARGUMENT');
  assert.equal(find(endpoint.eventTablePathLabel).textContent.trim(), 'PATH');
  assert.equal(find(endpoint.eventTableHashLabel).textContent.trim(), 'HASH');
};

export const assertTableSourceContext = (assert, { fileName, hash }) => {
  if (fileName) {
    assert.equal(find(endpoint.eventSourceFileNameValue).attributes[DATA_ENTITY_ID].nodeValue, fileName);
    assert.equal(find(endpoint.eventSourceFileNameValue).attributes[DATA_META_KEY].nodeValue, 'filename');
    assert.equal(find(endpoint.eventSourceFileNameValue).classList.contains(ENTITY_CLASS), true);
  } else {
    assert.equal(find(endpoint.eventSourceFileNameValue).classList.contains(ENTITY_CLASS), false);
  }
  if (hash) {
    assert.equal(find(endpoint.eventSourceHashValue).attributes[DATA_ENTITY_ID].nodeValue, hash);
    assert.equal(find(endpoint.eventSourceHashValue).attributes[DATA_META_KEY].nodeValue, 'hash');
    assert.equal(find(endpoint.eventSourceHashValue).classList.contains(ENTITY_CLASS), true);
  } else {
    assert.equal(find(endpoint.eventSourceHashValue).classList.contains(ENTITY_CLASS), false);
  }
};

export const assertTableSource = (assert, { fileName, launch, path, hash }) => {
  assert.equal(find(endpoint.eventSourceLabel).textContent.trim(), 'Source');
  assert.equal(find(endpoint.eventSourceFileNameValue).textContent.trim(), fileName);
  assert.equal(find(endpoint.eventSourceLaunchValue).textContent.trim(), launch);
  assert.equal(find(endpoint.eventSourcePathValue).textContent.trim(), path);
  assert.equal(find(endpoint.eventSourceHashValue).textContent.trim(), hash);
};

export const assertTableTargetContext = (assert, { fileName, hash }) => {
  if (fileName) {
    assert.equal(find(endpoint.eventTargetFileNameValue).attributes[DATA_ENTITY_ID].nodeValue, fileName);
    assert.equal(find(endpoint.eventTargetFileNameValue).attributes[DATA_META_KEY].nodeValue, 'filename');
    assert.equal(find(endpoint.eventTargetFileNameValue).classList.contains(ENTITY_CLASS), true);
  } else {
    assert.equal(find(endpoint.eventTargetFileNameValue).classList.contains(ENTITY_CLASS), false);
  }
  if (hash) {
    assert.equal(find(endpoint.eventTargetHashValue).attributes[DATA_ENTITY_ID].nodeValue, hash);
    assert.equal(find(endpoint.eventTargetHashValue).attributes[DATA_META_KEY].nodeValue, 'hash');
    assert.equal(find(endpoint.eventTargetHashValue).classList.contains(ENTITY_CLASS), true);
  } else {
    assert.equal(find(endpoint.eventTargetHashValue).classList.contains(ENTITY_CLASS), false);
  }
};

export const assertTableTarget = (assert, { fileName, launch, path, hash }) => {
  assert.equal(find(endpoint.eventTargetLabel).textContent.trim(), 'Target');
  assert.equal(find(endpoint.eventTargetFileNameValue).textContent.trim(), fileName);
  assert.equal(find(endpoint.eventTargetLaunchValue).textContent.trim(), launch);
  assert.equal(find(endpoint.eventTargetPathValue).textContent.trim(), path);
  assert.equal(find(endpoint.eventTargetHashValue).textContent.trim(), hash);
};
