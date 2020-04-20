import { findAll, find } from '@ember/test-helpers';
import { selectors, process } from './selectors';

const ENTITY_CLASS = 'entity';
const DATA_ENTITY_ID = 'data-entity-id';
const DATA_META_KEY = 'data-meta-key';

export const assertRowPresent = (assert) => {
  assert.equal(findAll(selectors.row).length, 1);
  assert.equal(findAll(selectors.processHeader).length, 1);
  assert.equal(findAll(selectors.processDetail).length, 0);
  assert.equal(findAll(selectors.caption).length, 1);
};

export const assertRowAlertDetails = (assert, { name, summary, score }) => {
  assert.equal(find(selectors.alertName).textContent.trim(), name);
  assert.equal(find(selectors.alertScore).textContent.trim(), score);
  assert.equal(find(selectors.eventSummary).textContent.trim(), summary);
};

export const assertRowHeaderContext = (assert, { username }) => {
  if (username) {
    assert.equal(find(process.eventUsernameValue).attributes[DATA_ENTITY_ID].nodeValue, username);
    assert.equal(find(process.eventUsernameValue).attributes[DATA_META_KEY].nodeValue, 'username');
    assert.equal(find(process.eventUsernameValue).classList.contains(ENTITY_CLASS), true);
  } else {
    assert.equal(find(process.eventUsernameValue).classList.contains(ENTITY_CLASS), false);
  }
};

export const assertRowHeader = (assert, { eventType, category, username, operationType, dataSource }) => {
  assert.equal(find(process.eventTimeLabel).textContent.trim(), 'EVENT TIME');
  assert.ok(find(process.eventTimeValue).textContent.trim() !== '');
  assert.equal(find(process.eventTypeLabel).textContent.trim(), 'EVENT TYPE');
  assert.equal(find(process.eventTypeValue).textContent.trim(), eventType);
  assert.equal(find(process.eventCategoryLabel).textContent.trim(), 'CATEGORY');
  assert.equal(find(process.eventCategoryValue).textContent.trim(), category);
  assert.equal(find(process.eventUsernameLabel).textContent.trim(), 'USERNAME');
  assert.equal(find(process.eventUsernameValue).textContent.trim(), username);
  assert.equal(find(process.eventOperationTypeLabel).textContent.trim(), 'OPERATION TYPE');
  assert.equal(find(process.eventOperationTypeValue).textContent.trim(), operationType);
  assert.equal(find(process.eventDataSourceLabel).textContent.trim(), 'DATA SOURCE');
  assert.equal(find(process.eventDataSourceValue).textContent.trim(), dataSource);
};

export const assertTableColumns = (assert) => {
  assert.equal(find(process.eventTableFileNameLabel).textContent.trim(), 'FILE NAME');
  assert.equal(find(process.eventTableChecksumLabel).textContent.trim(), 'CHECKSUM');
  assert.equal(find(process.eventTableDirectoryLabel).textContent.trim(), 'DIRECTORY');
  assert.equal(find(process.eventTableUsernameLabel).textContent.trim(), 'USERNAME');
  assert.equal(find(process.eventTableCategoriesLabel).textContent.trim(), 'CATEGORIES');
};

export const assertTableSource = (assert, { fileName, checksum, directory, username, categories }) => {
  assert.equal(find(process.eventSourceLabel).textContent.trim(), 'SOURCE');
  assert.equal(find(process.eventSourceFileNameValue).textContent.trim(), fileName);
  assert.equal(find(process.eventSourceChecksumValue).textContent.trim(), checksum);
  assert.equal(find(process.eventSourceDirectoryValue).textContent.trim(), directory);
  assert.equal(find(process.eventSourceUsernameValue).textContent.trim(), username);
  assert.equal(find(process.eventSourceCategoriesValue).textContent.trim(), categories);
};

export const assertTableTarget = (assert, { fileName, checksum, directory, username, categories }) => {
  assert.equal(find(process.eventTargetLabel).textContent.trim(), 'DESTINATION');
  assert.equal(find(process.eventTargetFileNameValue).textContent.trim(), fileName);
  assert.equal(find(process.eventTargetChecksumValue).textContent.trim(), checksum);
  assert.equal(find(process.eventTargetDirectoryValue).textContent.trim(), directory);
  assert.equal(find(process.eventTargetUsernameValue).textContent.trim(), username);
  assert.equal(find(process.eventTargetCategoriesValue).textContent.trim(), categories);
};

export const assertTableSourceContext = (assert, { fileName, checksum, username }) => {
  const fileNameElem = find(process.eventSourceFileNameValue);
  const checksumElem = find(process.eventSourceChecksumValue);
  const usernameElem = find(process.eventSourceUsernameValue);
  if (fileName) {
    assert.equal(fileNameElem.attributes[DATA_ENTITY_ID].nodeValue, fileName);
    assert.equal(fileNameElem.attributes[DATA_META_KEY].nodeValue, 'filename.src');
    assert.equal(fileNameElem.classList.contains(ENTITY_CLASS), true);
  } else {
    assert.equal(fileNameElem.classList.contains(ENTITY_CLASS), false);
  }
  if (checksum) {
    assert.equal(checksumElem.attributes[DATA_ENTITY_ID].nodeValue, checksum);
    assert.equal(checksumElem.attributes[DATA_META_KEY].nodeValue, 'checksum.src');
    assert.equal(checksumElem.classList.contains(ENTITY_CLASS), true);
  } else {
    assert.equal(checksumElem.classList.contains(ENTITY_CLASS), false);
  }
  if (username) {
    assert.equal(usernameElem.attributes[DATA_ENTITY_ID].nodeValue, username);
    assert.equal(usernameElem.attributes[DATA_META_KEY].nodeValue, 'user.src');
    assert.equal(usernameElem.classList.contains(ENTITY_CLASS), true);
  } else {
    assert.equal(usernameElem.classList.contains(ENTITY_CLASS), false);
  }
};

export const assertTableTargetContext = (assert, { fileName, checksum, username }) => {
  const fileNameElem = find(process.eventTargetFileNameValue);
  const checksumElem = find(process.eventTargetChecksumValue);
  const usernameElem = find(process.eventTargetUsernameValue);
  if (fileName) {
    assert.equal(fileNameElem.attributes[DATA_ENTITY_ID].nodeValue, fileName);
    assert.equal(fileNameElem.attributes[DATA_META_KEY].nodeValue, 'filename.dst');
    assert.equal(fileNameElem.classList.contains(ENTITY_CLASS), true);
  } else {
    assert.equal(fileNameElem.classList.contains(ENTITY_CLASS), false);
  }
  if (checksum) {
    assert.equal(checksumElem.attributes[DATA_ENTITY_ID].nodeValue, checksum);
    assert.equal(checksumElem.attributes[DATA_META_KEY].nodeValue, 'checksum.dst');
    assert.equal(checksumElem.classList.contains(ENTITY_CLASS), true);
  } else {
    assert.equal(checksumElem.classList.contains(ENTITY_CLASS), false);
  }
  if (username) {
    assert.equal(usernameElem.attributes[DATA_ENTITY_ID].nodeValue, username);
    assert.equal(usernameElem.attributes[DATA_META_KEY].nodeValue, 'user.dst');
    assert.equal(usernameElem.classList.contains(ENTITY_CLASS), true);
  } else {
    assert.equal(usernameElem.classList.contains(ENTITY_CLASS), false);
  }
};