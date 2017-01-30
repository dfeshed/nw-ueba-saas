export default [
  {
    dataSourceGroup: 'LiveConnect',
    dataStore: 'RelatedIps',
    header: 'relatedIpsCount',
    title: 'context.lc.relatedIps',
    columns: [
      {
        field: 'risk',
        title: 'context.lc.risk',
        width: '12%',
        nested: '',
        dataType: 'riskRating',
        class: {
          UNSAFE: 'rsa-context-panel__liveconnect__risk-rating-unsafe',
          SAFE: 'rsa-context-panel__liveconnect__risk-rating-safe',
          UNKNOWN: 'rsa-context-panel__liveconnect__risk-rating-unknown'
        }
      },
      {
        field: 'id',
        title: 'context.lc.ipAddress',
        nested: '',
        width: '12%',
        dataType: 'id',
        className: 'rsa-context-panel__liveconnect__entity-value'
      },
      {
        field: 'domain',
        title: 'context.lc.domain',
        nested: '',
        width: '15%',
        dataType: 'domain',
        className: 'rsa-context-panel__liveconnect__entity-value'
      },
      {
        field: 'asnNumber',
        title: 'context.lc.asnShort',
        width: '15%',
        nested: ''
      },
      {
        field: 'asnOrganization',
        title: 'context.lc.organization',
        width: '20%',
        nested: ''
      },
      {
        field: 'countryCode',
        title: 'context.lc.countryCode',
        width: '12%',
        nested: ''
      },
      {
        field: 'country',
        title: 'context.lc.country',
        width: '20%',
        nested: ''
      }
    ]
  },
  {
    dataSourceGroup: 'LiveConnect',
    dataStore: 'RelatedFiles',
    header: 'relatedFilesCount',
    title: 'context.lc.relatedFiles',
    columns: [
      {
        field: 'risk',
        title: 'context.lc.risk',
        width: '12%',
        nested: '',
        dataType: 'riskRating',
        class: {
          UNSAFE: 'rsa-context-panel__liveconnect__risk-rating-unsafe',
          SAFE: 'rsa-context-panel__liveconnect__risk-rating-safe',
          UNKNOWN: 'rsa-context-panel__liveconnect__risk-rating-unknown'
        }
      },
      {
        field: 'fileName',
        title: 'context.lc.fileName',
        nested: '',
        width: '18%',
        dataType: 'filename',
        className: 'rsa-context-panel__liveconnect__entity-value'
      },
      {
        field: 'id',
        title: 'context.lc.md5',
        width: '26%',
        nested: ''
      },
      {
        field: 'compiledTime',
        title: 'context.lc.compiledTime',
        width: '18%',
        dataType: 'datetime',
        nested: ''
      },
      {
        field: 'importHashFunction',
        title: 'context.lc.importHashFunction',
        width: '35%',
        nested: ''
      }

    ]
  },
  {
    dataSourceGroup: 'LiveConnect',
    header: 'relatedDomainsCount',
    dataStore: 'RelatedDomains',
    title: 'context.lc.relatedDomains',
    columns: [
      {
        field: 'risk',
        title: 'context.lc.risk',
        width: '12%',
        nested: '',
        dataType: 'riskRating',
        class: {
          UNSAFE: 'rsa-context-panel__liveconnect__risk-rating-unsafe',
          SAFE: 'rsa-context-panel__liveconnect__risk-rating-safe',
          UNKNOWN: 'rsa-context-panel__liveconnect__risk-rating-unknown'
        }
      },
      {
        field: 'id',
        title: 'context.lc.domain',
        width: '17%',
        style: 'color:blue',
        dataType: 'domain',
        nested: '',
        className: 'rsa-context-panel__liveconnect__entity-value'
      },
      {
        field: 'countryCode',
        title: 'context.lc.countryCode',
        width: '12%',
        nested: ''
      },
      {
        field: 'country',
        title: 'context.lc.country',
        width: '14%',
        nested: ''
      },
      {
        field: 'creationDate',
        title: 'context.lc.registeredDate',
        dataType: 'datetime',
        width: '13%',
        nested: ''
      },
      {
        field: 'expiredDate',
        title: 'context.lc.expiredDate',
        dataType: 'datetime',
        width: '13%',
        nested: ''
      },
      {
        field: 'registrantEmail',
        title: 'context.lc.email',
        width: '25%',
        nested: ''
      }
    ]
  }
];
