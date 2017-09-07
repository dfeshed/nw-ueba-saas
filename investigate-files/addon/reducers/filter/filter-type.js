/**
 * A set of types representing the time range since a starting point time (e.g., since 7 days ago)
 * @public
 * @type {[*]}
 */
const FILTER_TYPES = [
  {
    'propertyName': 'firstFileName',
    'label': 'investigateFiles.fields.firstFileName',
    'filterControl': 'content-filter/text-filter',
    'panelId': 'firstFileName',
    'selected': true,
    'isDefault': true
  },
  {
    'propertyName': 'size',
    'label': 'investigateFiles.fields.size',
    'filterControl': 'content-filter/number-filter',
    'panelId': 'size',
    'isDefault': true,
    'selected': true,
    'showMemUnit': true
  },
  {
    'propertyName': 'entropy',
    'label': 'investigateFiles.fields.entropy',
    'filterControl': 'content-filter/number-filter',
    'selected': false,
    'panelId': 'entropy',
    'isDefault': false

  },
  {
    'propertyName': 'format',
    'label': 'investigateFiles.fields.format',
    'values': ['pe', 'linux', 'macho', 'scripts'],
    'filterControl': 'content-filter/list-filter',
    'selected': false,
    'panelId': 'format',
    'isDefault': false
  },
  {
    'propertyName': 'pe.resources.company',
    'label': 'investigateFiles.fields.companyName',
    'filterControl': 'content-filter/text-filter',
    'selected': false,
    'panelId': 'company',
    'isDefault': false
  },
  {
    'propertyName': 'signature.features',
    'label': 'investigateFiles.fields.signature.features',
    'filterControl': 'content-filter/signature-filter',
    'selected': false,
    'panelId': 'signature',
    'isDefault': false
  },
  {
    'propertyName': 'checksumMd5',
    'label': 'investigateFiles.fields.checksumMd5',
    'filterControl': 'content-filter/text-filter',
    'selected': false,
    'panelId': 'checksumMd5',
    'isDefault': false
  },
  {
    'propertyName': 'checksumSha256',
    'label': 'investigateFiles.fields.checksumSha256',
    'filterControl': 'content-filter/text-filter',
    'selected': false,
    'panelId': 'checksumSha256',
    'isDefault': false
  },
  {
    'propertyName': 'checksumSha1',
    'label': 'investigateFiles.fields.checksumSha1',
    'filterControl': 'content-filter/text-filter',
    'selected': false,
    'panelId': 'checksumSha1',
    'isDefault': false
  }
];

export {
  FILTER_TYPES
};