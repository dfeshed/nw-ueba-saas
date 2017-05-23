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
        width: '8vw',
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
        width: '10vw',
        dataType: 'id',
        className: 'rsa-context-panel__liveconnect__entity-value'
      },
      {
        field: 'domain',
        title: 'context.lc.domain',
        width: '15vw',
        dataType: 'domain',
        className: 'rsa-context-panel__liveconnect__entity-value'
      },
      {
        field: 'asnNumber',
        title: 'context.lc.asnShort',
        width: '10vw'
      },
      {
        field: 'asnOrganization',
        title: 'context.lc.organization',
        width: '10vw'
      },
      {
        field: 'country',
        title: 'context.lc.country',
        width: '10vw'
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
        width: '8vw',
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
        width: '12vw',
        dataType: 'filename',
        className: 'rsa-context-panel__liveconnect__entity-value'
      },
      {
        field: 'id',
        title: 'context.lc.md5',
        width: '15vw'
      },
      {
        field: 'compileTime',
        title: 'context.lc.compiledTime',
        width: '10vw',
        dataType: 'datetime'
      },
      {
        field: 'impHash',
        title: 'context.lc.importHashFunction',
        width: '18vw'
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
        width: '8vw',
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
        width: '12vw',
        style: 'color:blue',
        dataType: 'domain',
        className: 'rsa-context-panel__liveconnect__entity-value'
      },
      {
        field: 'whoisCountry',
        title: 'context.lc.country',
        width: '10vw'
      },
      {
        field: 'whoisCreatedDate',
        title: 'context.lc.registeredDate',
        dataType: 'datetime',
        width: '10vw'
      },
      {
        field: 'whoisExpiredDate',
        title: 'context.lc.expiredDate',
        dataType: 'datetime',
        width: '10vw'
      },
      {
        field: 'whoisEmail',
        title: 'context.lc.email',
        width: '13vw'
      }
    ]
  }
];
