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
        width: '100',
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
        width: '100',
        dataType: 'id',
        className: 'rsa-context-panel__liveconnect__entity-value'
      },
      {
        field: 'domain',
        title: 'context.lc.domain',
        width: '100',
        dataType: 'domain',
        className: 'rsa-context-panel__liveconnect__entity-value'
      },
      {
        field: 'asnNumber',
        title: 'context.lc.asnShort',
        width: '100'
      },
      {
        field: 'asnOrganization',
        title: 'context.lc.organization',
        width: '100'
      },
      {
        field: 'country',
        title: 'context.lc.country',
        width: '100'
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
        width: '100',
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
        width: '100',
        dataType: 'filename',
        className: 'rsa-context-panel__liveconnect__entity-value'
      },
      {
        field: 'id',
        title: 'context.lc.md5',
        width: '100'
      },
      {
        field: 'compileTime',
        title: 'context.lc.compiledTime',
        width: '100',
        dataType: 'datetime'
      },
      {
        field: 'impHash',
        title: 'context.lc.importHashFunction',
        width: '400'
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
        width: '100',
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
        width: '150',
        style: 'color:blue',
        dataType: 'domain',
        className: 'rsa-context-panel__liveconnect__entity-value'
      },
      {
        field: 'whoisCountry',
        title: 'context.lc.country',
        width: '150'
      },
      {
        field: 'whoisCreatedDate',
        title: 'context.lc.registeredDate',
        dataType: 'datetime',
        width: '150'
      },
      {
        field: 'whoisExpiredDate',
        title: 'context.lc.expiredDate',
        dataType: 'datetime',
        width: '100'
      },
      {
        field: 'whoisEmail',
        title: 'context.lc.email',
        width: '200'
      }
    ]
  }
];
