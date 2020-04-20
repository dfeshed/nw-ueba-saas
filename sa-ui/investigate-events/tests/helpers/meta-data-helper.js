export const metaIsIndexedByNoneUInt16 = {
  format: 'UInt16',
  metaName: 'lifetime',
  flags: -2147483631,
  displayName: 'Session Lifetime',
  formattedName: 'lifetime (Session Lifetime)',
  disabled: true,
  isIndexedByKey: false,
  isIndexedByNone: true,
  isIndexedByValue: false
};

export const metaIsIndexedByNoneText = {
  format: 'Text',
  metaName: 'alert',
  flags: -2147483631,
  displayName: 'Alert',
  formattedName: 'Alert',
  disabled: true,
  isIndexedByKey: false,
  isIndexedByNone: true,
  isIndexedByValue: false
};

export const metaIsIndexedByValueUInt16 = {
  format: 'UInt16',
  metaName: 'tcp.dstport',
  flags: -2147482541,
  displayName: 'TCP Destination Port',
  formattedName: 'tcp.dstport (TCP Destination Port)',
  isIndexedByKey: false,
  isIndexedByNone: false,
  isIndexedByValue: true
};

export const metaIsIndexedByValueText = {
  format: 'Text',
  metaName: 't.dstport',
  flags: -2147482541,
  displayName: 'T Destination Port',
  formattedName: 't.dstport (T Destination Port)',
  isIndexedByKey: false,
  isIndexedByNone: false,
  isIndexedByValue: true
};

export const metaIsIndexedByKeyUInt64 = {
  format: 'UInt64',
  metaName: 'filename.size',
  flags: -2147482878,
  displayName: 'File Size',
  formattedName: 'filename.size (File Size)',
  isIndexedByKey: true,
  isIndexedByNone: false,
  isIndexedByValue: false
};

export const metaIsIndexedByKeyText = {
  format: 'Text',
  metaName: 'filename',
  flags: -2147482878,
  displayName: 'File Name',
  formattedName: 'filename (File Name)',
  isIndexedByKey: true,
  isIndexedByNone: false,
  isIndexedByValue: false
};

export const metaSessionidIsIndexedByNone = {
  format: 'UInt64',
  metaName: 'sessionid',
  flags: -2147483631,
  displayName: 'Session ID',
  formattedName: 'sessionid (Session ID)',
  isIndexedByKey: false,
  isIndexedByNone: true,
  isIndexedByValue: false
};

/**
 * returns powerselect options
 * with randomly assigned type: indexed by none, value, key
 */
export const getPowerSelectOptions = () => {

  const powerSelectAPIOptions = [
    { count: 0, format: 'Text', metaName: 'action', displayName: 'Action Event' },
    { count: 0, format: 'Text', metaName: 'ad.computer.dst', displayName: 'Active Directory Workstation Destination' },
    { count: 0, format: 'Text', metaName: 'ad.computer.src', displayName: 'Active Directory Workstation Source' },
    { count: 0, format: 'Text', metaName: 'ad.domain.dst', displayName: 'Active Directory Domain Destination' },
    { count: 0, format: 'Text', metaName: 'ad.domain.src', displayName: 'Active Directory Domain Source' },
    { count: 0, format: 'Text', metaName: 'ad.username.dst', displayName: 'Active Directory Username Destination' },
    { count: 0, format: 'Text', metaName: 'ad.username.src', displayName: 'Active Directory Username Source' },
    { count: 0, format: 'Text', metaName: 'alias.host', displayName: 'Hostname Aliases' },
    { count: 0, format: 'Text', metaName: 'attachment', displayName: 'Attachment' },
    { count: 0, format: 'Text', metaName: 'browser', displayName: 'Browsers' },
    { count: 0, format: 'Text', metaName: 'city.dst', displayName: 'Destination City' },
    { count: 0, format: 'Text', metaName: 'city.src', displayName: 'Source City' }
  ];

  powerSelectAPIOptions.forEach((option) => {
    const randomIndexedBy = Math.floor(Math.random() * 3) + 1;

    switch (randomIndexedBy) {
      // isIndexedByKey
      case 1:
        option.isIndexedByKey = true;
        option.isIndexedByNone = false;
        option.isIndexedByValue = false;
        option.disabled = false;
        break;

        // isIndexedByNone
      case 2:
        option.isIndexedByKey = false;
        option.isIndexedByNone = true;
        option.isIndexedByValue = false;
        option.disabled = true;
        break;

        // isIndexedByValue
      case 3:
        option.isIndexedByKey = false;
        option.isIndexedByNone = false;
        option.isIndexedByValue = true;
        option.disabled = false;
    }
  });

  return powerSelectAPIOptions;
};

/**
 * returns a powerSelectAPI
 * @param {*} powerSelectAPIOptions
 * @param {number} resultsSliceEnd or results will be of random length
 */
export const getPowerSelectAPI = (powerSelectAPIOptions, resultsSliceEnd) => {
  const options1 = (powerSelectAPIOptions && powerSelectAPIOptions.length > 0) ?
    [...powerSelectAPIOptions] : getPowerSelectOptions();

  // if `resultsSliceEnd` exists, use it to slice `options1` to produce `results1`
  // if not, random index
  const index = (resultsSliceEnd && resultsSliceEnd < powerSelectAPIOptions.length) ?
    resultsSliceEnd : Math.floor(Math.random() * (options1.length - 1)) + 1;

  // `results1` is a section of `options1`
  const results1 = options1.slice(0, index);

  return {
    options: options1,
    results: results1,
    resultsCount: results1.length
  };
};

/**
 * returns array of meta options generated
 * metas of randomly assigned type: none, key, value
 * @param {number} length
 */
export const getArrayOfRandomMeta = (length = 10) => {
  const toReturn = [];
  for (let i = 0; i < length; i++) {
    const randomIndexedBy = Math.floor(Math.random() * 3) + 1;
    const randomNumber = Date.now();
    const temp = {
      displayName: `meta-${randomNumber}`,
      format: 'Text',
      formattedName: `meta-${randomNumber}`,
      metaName: 'action'
    };

    switch (randomIndexedBy) {
      case 1:
        temp.isIndexedByKey = true;
        temp.isIndexedByNone = false;
        temp.isIndexedByValue = false;
        break;
      case 2:
        temp.isIndexedByKey = false;
        temp.isIndexedByNone = true;
        temp.isIndexedByValue = false;
        temp.disabled = true;
        break;
      case 3:
        temp.isIndexedByKey = false;
        temp.isIndexedByNone = false;
        temp.isIndexedByValue = true;
    }
    toReturn.push(temp);
  }
  return toReturn;
};
