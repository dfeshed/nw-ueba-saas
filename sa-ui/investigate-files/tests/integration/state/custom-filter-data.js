export default {
  fileFilters: {
    data: [
      {
        id: '5a6830ec3f11d6700d9ca761',
        name: 'entropy_less_than_3',
        filterType: 'FILE',
        criteria: {
          criteriaList: [],
          expressionList: [
            {
              propertyName: 'entropy',
              restrictionType: 'LESS_THAN',
              propertyValues: [
                {
                  value: 3,
                  relative: false
                }
              ]
            }
          ],
          predicateType: 'AND'
        },
        systemFilter: false
      }
    ]
  }
};